package com.tend.proxy.controller;


import com.tend.proxy.common.api.vo.Result;
import com.tend.proxy.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {



    /**
     * 兜底处理其它异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, Exception e){
        log.error("全局异常处理：{}", e.getMessage());
        return Result.error("代理服务器异常");
    }
    @ExceptionHandler(value = {ApiException.class})
    @ResponseBody
    public Object apiExceptionHandler(HttpServletRequest request, ApiException e){
        log.error("调用服务异常，查询方法：{}，参数：{}", e.getServiceName(),e.getParams());
        log.error(e.getMessage(), e);
        return Result.error("代理服务器异常",e.getServiceResult());
    }
}
