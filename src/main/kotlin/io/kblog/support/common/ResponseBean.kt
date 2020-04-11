package io.kblog.support.common

/**
 * The ResponseBean class.
 * @author hsdllcw on 2020/4/10.
 * @version 1.0.0
 */
class ResponseBean(
        var message: String = "success",
        var statusCode: Int = 200,
        var result: Any? = null
)