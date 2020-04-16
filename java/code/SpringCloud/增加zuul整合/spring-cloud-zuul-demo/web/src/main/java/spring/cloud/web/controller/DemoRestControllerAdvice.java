package spring.cloud.web.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

/**
 * 对DemoRestController抛出的异常进行拦截处理，类似于aop
 */
@RestControllerAdvice(assignableTypes = {
        DemoRestController.class
})
public class DemoRestControllerAdvice {

    @ExceptionHandler(TimeoutException.class)
    public Object timeoutHandler(Throwable throwable) {
        return throwable.getMessage();
    }
}
