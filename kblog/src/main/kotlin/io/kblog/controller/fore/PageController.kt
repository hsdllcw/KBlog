//package io.kblog.controller.fore
//
//import com.jeecms.common.jpa.SearchFilter
//import io.kblog.service.PageService
//import io.kblog.support.common.ResponseBean
//import io.kblog.support.config.ContextConfig
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.web.bind.annotation.*
//
///**
// * The ForePageController class.
// * @author hsdllcw on 2025/9/8.
// * @version 1.0.0
// */
//@RestController("ForePageController")
//@RequestMapping("${ContextConfig.API_URI}/page")
//class PageController {
//    @Autowired
//    lateinit var pageService: PageService
//
//    @RequestMapping(
//        value = ["", "/", "/page", "/page/", "/page/{page}", "/page/{page}/size", "/page/{page}/size/", "/page/{page}/size/{size}"],
//        method = [RequestMethod.GET, RequestMethod.POST]
//    )
//    fun list(
//        @RequestBody(required = false) params: Map<String, Any>?,
//        @PathVariable(required = false) page: Int?,
//        @PathVariable(required = false) size: Int?
//    ): Any {
//        val sort = (params ?: emptyMap()).getOrDefault("sort", "DESC_id").toString().split("_")
//        return ResponseBean(
//            pageService.findAll(
//                SearchFilter.spec(mutableMapOf<String, Array<String>>().apply {
//                    params?.filter { it.key.contains("search_") }?.forEach { (key, value) ->
//                        this.putAll(mapOf(key.substring("search_".length) to if (value is ArrayList<*>) value.map { it.toString() }
//                            .toTypedArray() else arrayOf(value.toString())))
//                    }
//                }),
//                PageRequest.of(
//                    page ?: 0,
//                    size ?: 10,
//                    Sort.by(
//                        Sort.Direction.valueOf(sort.first()),
//                        *sort.filterIndexed { index: Int, _: String -> index != 0 }.toTypedArray()
//                    )
//                )
//            )
//        )
//    }
//}