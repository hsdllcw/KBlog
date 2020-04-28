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
package org.opoo.press.source;

import io.kblog.domain.Page;
import io.kblog.domain.Tag;
import io.kblog.service.PageService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.opoo.press.*;
import org.opoo.press.impl.KSiteImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Alex Lin
 */
@Component("sourceParser")
@Transactional(readOnly = true)
public class KSourceParserImpl extends SourceParserImpl implements SourceParser {
    private static final Logger log = LoggerFactory.getLogger(KSourceParserImpl.class);
    @Autowired
    private PageService pageService;
    @Autowired
    private KSiteImpl site;

    /* (non-Javadoc)
     * @see org.opoo.press.SourceParser#parse(org.opoo.press.SourceEntry)
     */
    @Override
    public Source parse(SourceEntry sourceEntry) throws NoFrontMatterException {
        InputStream stream = null;
        StringWriter contentWriter = new StringWriter();
        try {
            stream = new FileInputStream(sourceEntry.getFile());
            LineIterator iterator = IOUtils.lineIterator(stream, "UTF-8");
            var iteratorLines = IteratorUtils.toList(iterator);
            if (iteratorLines.isEmpty()) {
                throw new RuntimeException("File not content: " + sourceEntry.getFile());
            }
            if (isFrontMatterStartLine(String.valueOf(iteratorLines.get(0)), sourceEntry)) {
                return super.parse(sourceEntry);
            }
            IOUtils.writeLines(iteratorLines, null, contentWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(contentWriter);
        }
        String content = contentWriter.toString();

        try {
            Map<String, Object> map = Collections.emptyMap();
            Page page = pageService.findByPath(sourceEntry.getFile().getAbsolutePath().substring(site.getBasedir().getAbsolutePath().length()));
            if (page != null) {
                // noinspection unchecked,rawtypes,rawtypes
                map = (Map) BeanUtils.describe(page);
                if (map.containsKey("tags") && map.get("tags") != null) {
                    map.put("tags", page.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
                }
                if (map.containsKey("category") && map.get("category") != null) {
                    map.put("category", Objects.requireNonNull(page.getCategory()).getName());
                }
                if (map.containsKey("published") && map.get("published") != null) {
                    map.put("published", page.isPublished());
                }
                if (map.containsKey("date") && map.get("date") != null) {
                    map.put("date", page.getDate());
                }
            }

            return new SimpleSource(sourceEntry, map, content);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isFrontMatterStartLine(String line, SourceEntry SourceEntry) {
        if (Source.TRIPLE_DASHED_LINE.equals(line)) {
            return true;
        }
        if (line.length() == 4) {
            if (65279 == line.charAt(0) && Source.TRIPLE_DASHED_LINE.equals(line.substring(1))) {
                log.debug("UTF-8 with BOM file: " + SourceEntry.getFile());
                return true;
            }
        }
        return false;
    }
}