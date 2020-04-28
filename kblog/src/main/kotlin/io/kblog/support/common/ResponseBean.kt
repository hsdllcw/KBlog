package io.kblog.support.common

/**
 * The ResponseBean class.
 * @author hsdllcw on 2020/4/10.
 * @version 1.0.0
 */
class ResponseBean(
        var result: Any? = null,
        var message: String = "success",
        var status: Int = 200
)