package pycro.usts.common.config.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pycro.usts.common.result.Result;




/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 5:48 PM
 */
@ControllerAdvice // AOP
public class GlobalExceptionHandler {


    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result<?> error(ArithmeticException e) {
        return Result
                .fail(e.getClass().getName() + ":" + e.getMessage())
                .message("执行了特定异常处理");

    }

    @ExceptionHandler(PycroException.class)
    @ResponseBody
    public Result<?> error(PycroException e) {
        // .code() 、 .message() 实现了可扩展性
        return Result
                .fail(e.getClass().getName() + ":" + e.getMessage())
                .code(e.getCode())
                .message("执行了自定义异常处理");
    }

    /**
     * spring security异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result<?> error(AccessDeniedException e) {
        return Result
                .fail(e.getClass().getName() + ":" + e.getMessage())
                .code(205)
                .message("没有访问权限");
    }

    @ExceptionHandler(Exception.class)// 异常通知
    @ResponseBody
    public Result<?> error(Exception e) {
        e.printStackTrace();
        return Result.fail().message("执行了全局异常处理");
    }
}
