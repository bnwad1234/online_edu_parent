package com.atguigu.edu.handler;

import com.atguigu.exception.EduException;
import com.atguigu.response.RetVal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//只要出现异常 就交给这个类进行处理
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RetVal error(Exception e){
        e.printStackTrace();
        System.out.println("处理全局异常业务逻辑");
        return RetVal.error().message("全局异常出现啦");
    }
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public RetVal error(ArithmeticException e){
        System.out.println("处理特殊异常业务逻辑");
        return RetVal.error().message("特殊异常出现啦");
    }
    @ExceptionHandler(EduException.class)
    @ResponseBody
    public RetVal error(EduException e){
        System.out.println("处理自定义异常业务逻辑");
        return RetVal.error().message(e.getMessage());
    }
}
