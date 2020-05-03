package io.kblog.controller

import io.kblog.support.common.ResponseBean
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.reflect.UndeclaredThrowableException
import javax.servlet.http.HttpServletRequest

/**
 * The GlobalExceptionHandler class.
 * @author hsdllcw on 2020/5/3.
 * @version 1.0.0
 */
@ControllerAdvice
class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UndeclaredThrowableException::class)
    fun handleException(request: HttpServletRequest, exeption: UndeclaredThrowableException): Any {
        return ResponseBean(null, exeption.undeclaredThrowable.message, HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(request: HttpServletRequest, exeption: MethodArgumentNotValidException): Any {
        return ResponseBean(exeption.bindingResult.allErrors.let {
            if (it.size > 1) it
            else null
        }, exeption.bindingResult.allErrors.let {
            if (it.size == 1) it.first().defaultMessage
            else null
        }, HttpStatus.BAD_REQUEST.value())
    }
}