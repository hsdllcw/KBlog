package io.kblog.controller

import com.jeecms.common.jpa.SearchFilter
import io.kblog.domain.Base
import io.kblog.service.BaseService
import io.kblog.support.common.ResponseBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * The BaseController interface.
 * @author hsdllcw on 2020/4/19.
 * @version 1.0.0
 */
abstract class BaseController<T, E> {
    @Autowired
    lateinit var baseService: BaseService<T, E>

    @PutMapping(value = ["", "/"])
    open fun create(@Valid @RequestBody bean: E): Any {
        return ResponseBean(baseService.createByVo(bean))
    }

    @PutMapping(value = ["/{id}"])
    open fun update(@Valid @RequestBody bean: E): Any {
        return ResponseBean(baseService.updateByVo(bean))
    }

    @GetMapping(value = ["/{id}"])
    open fun get(@PathVariable(required = false) id: Int?): Any {
        return ResponseBean(baseService.get(id))
    }

    @RequestMapping(value = ["", "/", "/page", "/page/", "/page/{page}", "/page/{page}/size", "/page/{page}/size/", "/page/{page}/size/{size}"], method = [RequestMethod.GET, RequestMethod.POST])
    open fun list(@RequestBody(required = false) params: Map<String, Any>?, @PathVariable(required = false) page: Int?, @PathVariable(required = false) size: Int?): Any {
        val sort = (params ?: emptyMap()).getOrDefault("sort", "DESC_id").toString().split("_")
        return ResponseBean(baseService.findAll(SearchFilter.spec(mutableMapOf<String, Array<String>>().apply {
            params?.filter { it.key.contains("search_") }?.forEach { (key, value) ->
                this.putAll(mapOf(key.substring("search_".length) to if (value is ArrayList<*>) value.map { it.toString() }.toTypedArray() else arrayOf(value.toString())))
            }
        }), PageRequest.of(page ?: 0, size ?: 10, Sort.by(Sort.Direction.valueOf(sort.first()), *sort.filterIndexed { index: Int, _: String -> index != 0 }.toTypedArray()))))
    }

    @DeleteMapping(value = ["/{id}", "/delete"])
    open fun delete(@PathVariable(required = false) id: Int?, @RequestBody(required = false) ids: List<Int>?): Any {
        return ResponseBean(id?.let { baseService.deleteById(id) } ?: ids?.let { baseService.deleteById(ids) }
        ?: throw MissingServletRequestParameterException("ids", "List<Int>"))
    }
}
