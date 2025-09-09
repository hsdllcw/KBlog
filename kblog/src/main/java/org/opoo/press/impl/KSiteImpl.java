/*
 * Copyright 2013 Alex Lin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opoo.press.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.opoo.press.*;
import org.opoo.press.Observer;
import org.opoo.press.processor.ProcessorsProcessor;
import org.opoo.press.task.TaskExecutor;
import org.opoo.press.util.StaleUtils;
import org.opoo.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Alex Lin
 */
@Component("site")
@Transactional(readOnly = true)
public class KSiteImpl implements Site, SiteBuilder, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(KSiteImpl.class);

    @Autowired
    private ConfigImpl config;
    private Map<String, Object> data;
    private File dest;
    private File templates;
    private File working;
    private File basedir;
    private ValidDirList sources;
    private ValidDirList assets;

    private String root;

    private List<Page> pages;
    private List<Post> posts;
    private List<StaticFile> staticFiles;

    private Map<String, Category> categories;
    private List<Tag> tags;

    private Date time;
    private boolean showDrafts = false;

    private Renderer renderer;
    private Locale locale;
    private String permalink;
    private TaskExecutor taskExecutor;

    private Theme theme;
    private ProcessorsProcessor processors;

    private ClassLoader classLoader;
    @Autowired
    private KFactoryImpl factory;

    public KSiteImpl() throws IOException {
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        this.data = new HashMap<>(/*config*/);
        this.basedir = config.getBasedir();
        this.root = config.get("root", "");
        this.permalink = config.get("permalink");
        this.showDrafts = config.get("show_drafts", false);
        boolean debug = config.get("debug", false);

        if (showDrafts) {
            log.info("+ Show drafts option set 'ON'");
        }
        if (debug) {
            for (Map.Entry<String, Object> en : config.entrySet()) {
                var name = en.getKey();
                name = StringUtils.leftPad(name, 25);
                log.info(name + ": " + en.getValue());
            }
        }
        //theme
        theme = createTheme();

        //templates
        templates = theme.getTemplates();
        log.debug("Template directory: {}", templates);

        //sources
        sources = new ValidDirList();
        sources.addDir(theme.getSource());
        List<String> sourcesConfig = config.get("source_dirs");
        sources.addDirs(basedir, sourcesConfig);
        log.debug("Source directories: {}", sources);

        //assets
        assets = new ValidDirList();
        assets.addDir(theme.getAssets());
        List<String> assetsConfig = config.get("asset_dirs");
        assets.addDirs(basedir, assetsConfig);
        log.debug("Assets directories: {}", assets);

        //target directory
        String destDir = config.get("dest_dir");
        this.dest = PathUtils.appendBaseIfNotAbsolute(basedir, destDir);
        log.debug("Destination directory: {}", dest);

        //working directory
        String workingDir = config.get("work_dir");
        this.working = PathUtils.appendBaseIfNotAbsolute(basedir, workingDir);
        log.debug("Working directory: {}", working);

        reset();
        setup();
    }

    private Theme createTheme() throws IOException {
        var name = config.get("theme", "default");
        var themes = new File(basedir, "themes");
        var themeDir = PathUtils.appendBaseIfNotAbsolute(themes, name);
        if (!themeDir.exists() || !themeDir.isDirectory()) {
            throw new IllegalArgumentException("Theme directory not exists or not valid, please install theme first: "
                    + themeDir);
        }

//		PathUtils.checkDir(themeDir, PathUtils.Strategy.THROW_EXCEPTION_IF_NOT_EXISTS);
        compileTheme(themeDir);
        return new KThemeImpl(themeDir, this);
    }

    private void compileTheme(File themeDir) {
        ThemeCompiler themeCompiler = config.get("theme.compiler");
        if (themeCompiler != null) {
            log.debug("Compile theme by '{}'", themeCompiler.getClass().getName());
            themeCompiler.compile(themeDir);
        } else {
            log.debug("no theme compiler found.");
        }
    }

    public void build() {
        build(false);
    }

    public void build(boolean force) {
        if (force) {
            log.info("force build.");
            buildInternal();
            return;
        }

        if (StaleUtils.isStale(this, false)) {
            buildInternal();
            return;
        }

        // only asset file(s) changed.
        List<File> staleAssets = StaleUtils.getStaleAssets(this);
        if (staleAssets != null) {
            for (File staleAsset : staleAssets) {
                //copy asset directory to destination directory
                log.info("Copying stale asset: {}...", staleAsset);
                try {
                    FileUtils.copyDirectory(staleAsset, dest, buildFilter());
                    StaleUtils.saveLastBuildInfo(this);
                    return;
                } catch (IOException e) {
                    throw new RuntimeException("Copy stale asset exception: " + staleAsset, e);
                }
            }
        }

        log.info("Nothing to build - all site output files are up to date.");
    }

    @Override
    public void clean() throws Exception {
        log.info("Cleaning destination directory " + dest);
        FileUtils.deleteDirectory(dest);

        log.info("Cleaning working directory " + working);
        FileUtils.deleteDirectory(working);
    }

    private void buildInternal() {
//		if(!setup){
//			setup = true;
//			setup();
//		}

        reset();
        read();
        generate();
        render();
        //如果输出目录webapp，会导致webapp下的文件被全部删除
        //cleanup();
        write();

        StaleUtils.saveLastBuildInfo(this);
    }

    void reset() {
        this.time = config.get("time", new Date());
        this.pages = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.staticFiles = Collections.synchronizedList(new ArrayList<>());
    }

    public void resetCategories() {
        this.categories = new LinkedHashMap<>();
        Map<String, String> names = config.get("category_names");
        if (names == null || names.isEmpty()) {
            return;
        }
        //sort name
        names = new TreeMap<>(names);
        for (Map.Entry<String, String> en : names.entrySet()) {
            var path = en.getKey();
            var name = en.getValue();

            var nicename = path;
            String parentPath = null;
            int index = path.lastIndexOf('.');
            if (index != -1) {
                nicename = path.substring(index + 1);
                parentPath = path.substring(0, index);
            }

            Category parent = null;
            if (parentPath != null) {
                parent = categories.get(parentPath);
                if (parent == null) {
                    throw new IllegalArgumentException("Parent category not found: " + parentPath);
                }
            }
            CategoryImpl category = new CategoryImpl(nicename, name, parent, this);
            categories.put(path, category);
        }
    }

    void resetTags() {
        this.tags = new ArrayList<>();
        Map<String, String> names = config.get("tag_names");
        if (names == null || names.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> en : names.entrySet()) {
            tags.add(new TagImpl(en.getKey(), en.getValue(), this));
        }
    }

    void setup() {
        //ensure source not in destination
//        for (File source : sources) {
//            source = PathUtils.canonical(source);
//            if (dest.equals(source) || source.getAbsolutePath().startsWith(dest.getAbsolutePath())) {
//                throw new IllegalArgumentException("Destination directory cannot be or contain the Source directory.");
//            }
//        }

        //locale
        String localeString = config.get("locale");
        if (localeString != null) {
            locale = LocaleUtils.toLocale(localeString);
            log.debug("Set locale: " + locale);
        }

        //object instances
        classLoader = createClassLoader(config, theme);
        taskExecutor = new TaskExecutor(config);

        factory.setSite(this);
        processors = new ProcessorsProcessor(factory.getPluginManager().getProcessors());

        //Construct RendererImpl after initializing all plugins
        renderer = factory.createRenderer(this);
    }

    private ClassLoader createClassLoader(Config config, Theme theme) {
        log.debug("Create site ClassLoader.");

        ClassLoader parent = KSiteImpl.class.getClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }


        String sitePluginDir = config.get("plugin_dir");
        var themePluginDir = (String) theme.get("plugin_dir");

        var classPathEntries = new ArrayList<File>(2);

        if (StringUtils.isNotBlank(sitePluginDir)) {
            File sitePlugins = PathUtils.canonical(new File(config.getBasedir(), sitePluginDir));
            addClassPathEntries(classPathEntries, sitePlugins);
        }

        if (StringUtils.isNotBlank(themePluginDir)) {
            File themePlugins = PathUtils.canonical(new File(theme.getPath(), themePluginDir));
            addClassPathEntries(classPathEntries, themePlugins);
        }

        //theme classes
        var themeClasses = new File(theme.getPath(), "target/classes");
        var themeSrc = new File(theme.getPath(), "src");
        if (themeSrc.exists() && themeClasses.exists() && themeClasses.isDirectory()) {
            classPathEntries.add(themeClasses);
        }

        //theme target/plugins
        var themeTargetPlugins = new File(theme.getPath(), "target/plugins");
        if (themeTargetPlugins.exists() && Objects.requireNonNull(themeTargetPlugins.list()).length > 0) {
            addClassPathEntries(classPathEntries, themeTargetPlugins);
        }

        if (classPathEntries.isEmpty()) {
            log.info("No custom classpath entries.");
            return parent;
        }

        URL[] urls = new URL[classPathEntries.size()];

        try {
            for (int i = 0; i < classPathEntries.size(); i++) {
                urls[i] = classPathEntries.get(i).toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new URLClassLoader(urls, parent);
    }

    private void addClassPathEntries(List<File> classPathEntries, File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles(new ValidPluginClassPathEntryFileFilter());
            if (files != null && files.length > 0) {
                classPathEntries.addAll(Arrays.asList(files));
            }
        }
    }

    void read() {
        //read categories and tags from configuration
        resetCategories();
        resetTags();

        Runnable t1 = this::readSources;

        Runnable t2 = this::readStaticFiles;

        taskExecutor.run(t1, t2);

        postRead();
    }

    private void readSources() {
        log.info("Reading sources ...");
        final var sourceEntryLoader = factory.getSourceEntryLoader();
        final var sourceParser = factory.getSourceParser();

        //load sources and load static files
        var fileFilter = buildFilter();
        var list = new ArrayList<SourceEntry>();
        for (var src : sources) {
            var tempList = sourceEntryLoader.loadSourceEntries(src, fileFilter);
            if (tempList != null && !tempList.isEmpty()) {
                list.addAll(tempList);
            }
        }


        for (var en : list) {
            read(en, sourceParser);
        }

        Collections.sort(posts);
        Collections.reverse(posts);
        setPostNextOrPrevious(posts);
    }


    /**
     * @param posts 文档列表
     */
    private void setPostNextOrPrevious(List<Post> posts) {
        var it = posts.iterator();
        Post prev = null;
        Post curr;
        while (it.hasNext()) {
            curr = it.next();
            if (prev != null) {
                prev.setPrevious(curr);
                curr.setNext(prev);
            }
            prev = curr;
        }
    }

    private void readStaticFiles() {
        log.info("Reading assets ...");
        final var sourceEntryLoader = factory.getSourceEntryLoader();
        var fileFilter = buildFilter();
        for (var assetDir : assets) {
            var tempList = sourceEntryLoader.loadSourceEntries(assetDir, fileFilter);
            for (var se : tempList) {
                log.debug("read static file {}", se.getFile());
                staticFiles.add(new StaticFileImpl(this, se));
            }
        }
    }

    /**
     *
     */
    private void postRead() {
        processors.postRead(this);
    }


    private void read(SourceEntry en, SourceParser parser) {
        try {
            var src = parser.parse(en);
            log.debug("read source {}", src.getSourceEntry().getFile());

            var map = src.getMeta();
            var layout = (String) map.get("layout");
            if ("post".equals(layout)) {
                readPost(src);
            } else {
                pages.add(factory.createPage(this, src));
            }
        } catch (NoFrontMatterException e) {
            this.staticFiles.add(new StaticFileImpl(this, en));
        }
    }

    private void readPost(Source src) {
        if (isDraft(src.getMeta())) {
            if (showDrafts) {
                posts.add(factory.createDraft(this, src));
            }
        } else {
            posts.add(factory.createPost(this, src));
        }
    }

    private boolean isDraft(Map<String, Object> meta) {
        if (!meta.containsKey("published")) {
            return false;
        }
        Boolean b = (Boolean) meta.get("published");
        return !b;
    }

    FileFilter buildFilter() {
        final List<String> includes = config.get("includes");
        final List<String> excludes = config.get("excludes");
        return file -> {
            var name = file.getName();
            if (includes != null && includes.contains(name)) {
                return true;
            }
            if (excludes != null && excludes.contains(name)) {
                return false;
            }
            char firstChar = name.charAt(0);
            if (firstChar == '.' || firstChar == '_' || firstChar == '#') {
                return false;
            }
            char lastChar = name.charAt(name.length() - 1);
            if (lastChar == '~') {
                return false;
            }
            return !file.isHidden();
        };
    }


    void generate() {
        for (Generator g : factory.getPluginManager().getGenerators()) {
            g.generate(this);
        }
        postGenerate();
    }

    /**
     *
     */
    private void postGenerate() {
        processors.postGenerate(this);
    }

    void render() {
        final Map<String, Object> rootMap = buildRootMap();
        renderer.prepareLayoutWorkingTemplates();

//		for(Post post: posts){
//			post.convert();
//			postConvertPost(post);
//
//			post.render(rootMap);
//			postRenderPost(post);
//		}
        log.info("Rendering {} posts...", posts.size());
        taskExecutor.run(posts, post -> {
            post.convert();
            postConvertPost(post);
//
            post.render(rootMap);
            postRenderPost(post);
        });
        postRenderPosts();

        log.info("Rendering {} pages...", pages.size());
        taskExecutor.run(pages, page -> {
            page.convert();
            postConvertPage(page);

            page.render(rootMap);
            postRenderPage(page);
        });
        postRenderPages();
    }

    /**
     * @param post 文档
     */
    private void postConvertPost(Post post) {
        processors.postConvertPost(this, post);
    }

    /**
     * @param page 文档
     */
    private void postConvertPage(Page page) {
        processors.postConvertPage(this, page);
    }

    /**
     * @param post 文档
     */
    private void postRenderPost(Post post) {
        processors.postRenderPost(this, post);
    }

    /**
     *
     */
    private void postRenderPosts() {
        processors.postRenderAllPosts(this);
    }

    /**
     * @param page 文档
     */
    private void postRenderPage(Page page) {
        processors.postRenderPage(this, page);
    }

    /**
     *
     */
    private void postRenderPages() {
        processors.postRenderAllPages(this);
    }


    Map<String, Object> buildRootMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("site", this);
        map.put("root_url", getRoot());
        map.put("basedir", getRoot());
        map.put("opoopress", config.get("opoopress"));
        map.put("theme", theme);
        return map;
    }

    /**
     *
     */
    void cleanup() {
        log.info("cleanup...");
        final var destFiles = getAllDestFiles(dest);
        var files = new ArrayList<File>();
        for (StaticFile staticFile : staticFiles) {
            files.add(staticFile.getOutputFile(dest));
        }

        log.debug("Files in target: {}", destFiles.size());
        log.debug("Assets file in src: {}", files.size());


        //find obsolete files
        for (var file : files) {
            destFiles.remove(file);
        }
//		destFiles.removeAll(files);

        log.debug("Files in target will be deleted: {}", destFiles.size());

        //delete obsolete files
        if (!destFiles.isEmpty()) {
//			for(File destFile: destFiles){
//				//FileUtils.deleteQuietly(destFile);
//				if(IS_DEBUG_ENABLED){
//					log.debug("Delete file " + destFile);
//				}
//			}

            taskExecutor.run(destFiles, file -> {
                FileUtils.deleteQuietly(file);
                log.debug("File deleted: {}", file);
            });
        }

        //call post cleanup
        postCleanup();
    }

    /**
     * @param dest 目标文件
     * @return List<File>
     */
    private List<File> getAllDestFiles(File dest) {
        var files = new ArrayList<File>();
        if (dest != null && dest.exists()) {
            listDestFiles(files, dest);
        }
        return files;
    }

    private void listDestFiles(List<File> files, File dir) {
        var list = dir.listFiles();
        for (var f : Objects.requireNonNull(list)) {
            if (f.isFile()) {
                files.add(f);
            } else if (f.isDirectory()) {
                listDestFiles(files, f);
            }
        }
    }

    /**
     *
     */
    private void postCleanup() {
        processors.postCleanup(this);
    }

    void write() {
        log.info("Writing {} posts, {} pages, and {} static files ...",
                posts.size(), pages.size(), staticFiles.size());

        if (!dest.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dest.mkdirs();
        }

        List<Writable> list = new ArrayList<>();
        list.addAll(posts);
        list.addAll(pages);
        if (!staticFiles.isEmpty()) {
            list.addAll(staticFiles);
        }

        taskExecutor.run(list, o -> o.write(dest));
        postWrite();
    }

    /**
     *
     */
    private void postWrite() {
        processors.postWrite(this);
    }

    /**
     * @return the pages
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * @return the posts
     */
    public List<Post> getPosts() {
        return posts;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getConfig()
     */
    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public List<File> getSources() {
        return sources;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getDestination()
     */
    @Override
    public File getDestination() {
        return dest;
    }

    public List<StaticFile> getStaticFiles() {
        return staticFiles;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getTime()
     */
    @Override
    public Date getTime() {
        return time;
    }

    public Object get(String name) {
        if (data.containsKey(name)) {
            return data.get(name);
        }

        if (config.containsKey(name)) {
            return config.get(name);
        }

        if (theme != null) {
            return theme.get(name);
        }

        return null;
    }

    public void set(String name, Object value) {
        data.put(name, value);
        //MapUtils.put(data, name, value);
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getRenderer()
     */
    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public File getTemplates() {
        return templates;
    }

    @Override
    public List<File> getAssets() {
        return assets;
    }

    @Override
    public File getWorking() {
        return working;
    }

    @Override
    public Converter getConverter(Source source) {
        return factory.getPluginManager().getConverter(source);
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getRoot()
     */
    @Override
    public String getRoot() {
        return root;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getLocale()
     */
    @Override
    public Locale getLocale() {
        return locale;
    }


    /* (non-Javadoc)
     * @see org.opoo.press.Site#getCategories()
     */
    @Override
    public List<Category> getCategories() {
        return new CategoriesList(categories);
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getTags()
     */
    @Override
    public List<Tag> getTags() {
        return tags;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.SiteHelper#buildCanonical(java.lang.String)
     */
    @Override
    public String buildCanonical(String url) {
		/*
		String canonical = (String) config.get("url");
		String permalink = (String) config.get("permalink");
		String pageUrl = url;
		if(permalink != null && permalink.endsWith(".html")){
			canonical += pageUrl;
		}else{
			canonical += StringUtils.removeEnd(pageUrl, "index.html");
		}
		return canonical;
		*/
        return url;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.SiteHelper#toSlug(java.lang.String)
     */
    @Override
    public String toSlug(String tagName) {
        return factory.getSlugHelper().toSlug(tagName);
    }

    /* (non-Javadoc)
     * @see org.opoo.press.SiteHelper#toNicename(java.lang.String)
     */
    @Override
    public String toNicename(String categoryName) {
        return factory.getSlugHelper().toSlug(categoryName);
    }

    /* (non-Javadoc)
     * @see org.opoo.press.SiteHelper#getCategory(java.lang.String)
     */
    @Override
    public Category getCategory(String categoryNameOrNicename) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        //If path equals
        if (categories.containsKey(categoryNameOrNicename)) {
            return categories.get(categoryNameOrNicename);
        }
        for (var category : new ArrayList<>(categories.values())) {
            if (category.isNameOrNicename(categoryNameOrNicename)) {
                return category;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.opoo.press.SiteHelper#getTag(java.lang.String)
     */
    @Override
    public Tag getTag(String tagNameOrSlug) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        for (var tag : new ArrayList<>(tags)) {
            if (tag.isNameOrSlug(tagNameOrSlug)) {
                return tag;
            }
        }
        return null;
    }

    public void setConfig(ConfigImpl config) {
        this.config = config;
    }

    public void setFactory(KFactoryImpl factory) {
        this.factory = factory;
    }

    private static class CategoriesList extends AbstractList<Category> {
        private final List<Category> list = new ArrayList<>();
        private final Map<String, Category> categories;

        private CategoriesList(Map<String, Category> categories) {
            this.categories = categories;
            //this.list = new ArrayList<Category>(categories.values());
            for (Category category : categories.values()) {
                if (!category.getPosts().isEmpty()) {
                    list.add(category);
                }
            }
        }

        @Override
        public boolean add(Category category) {
            categories.put(category.getPath(), category);
            return list.add(category);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractList#get(int)
         */
        @Override
        public Category get(int index) {
            return list.get(index);
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return list.size();
        }
    }

    /* (non-Javadoc)
     * @see org.opoo.press.Site#getPermalink()
     */
    @Override
    public String getPermalink() {
        return permalink;
    }

    /**
     * @return the site
     */
    @Override
    public File getBasedir() {
        return basedir;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Factory getFactory() {
        return factory;
    }

    @Override
    public Observer getObserver() {
        return new KSiteObserver(this);
    }

    /**
     * @return the showDrafts
     */
    public boolean showDrafts() {
        return showDrafts;
    }


    /* (non-Javadoc)
     * @see org.opoo.press.Site#getTheme()
     */
    @Override
    public Theme getTheme() {
        return theme;
    }

    ProcessorsProcessor getProcessors() {
        return processors;
    }

    @SuppressWarnings("UnusedReturnValue")
    static class ValidDirList extends ArrayList<File> {
        private static final long serialVersionUID = 6306507738477638252L;

        public ValidDirList addDir(File dir) {
            if (PathUtils.isValidDirectory(dir)) {
                add(dir);
            }
            return this;
        }

        public ValidDirList addDir(File base, String path) {
            return addDir(new File(base, path));
        }

        public ValidDirList addDirs(File base, List<String> paths) {
            for (String path : paths) {
                addDir(base, path);
            }
            return this;
        }
    }

    static class ValidPluginClassPathEntryFileFilter implements FileFilter {
        /* (non-Javadoc)
         * @see java.io.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File file) {
            var name = file.getName();
            char firstChar = name.charAt(0);
            if (firstChar == '.' || firstChar == '_' || firstChar == '#') {
                return false;
            }
            char lastChar = name.charAt(name.length() - 1);
            if (lastChar == '~') {
                return false;
            }
            if (file.isHidden()) {
                return false;
            }
            if (file.isDirectory()) {
                return true;
            }
            if (file.isFile()) {
                name = name.toLowerCase();
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
            return false;
        }
    }
}
