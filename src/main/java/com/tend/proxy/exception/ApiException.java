package com.tend.proxy.exception;

public class  ApiException extends Exception{
    private String serviceName;
    private String params;

    private Object serviceResult;

    public ApiException(String message, Throwable cause, String serviceName, String params) {
        super(message, cause);
        this.serviceName = serviceName;
        this.params = params;
    }
    public ApiException(String message, String serviceName, String params, Object serviceResult) {
        super(message);
        this.serviceName = serviceName;
        this.params = params;
        this.serviceResult = serviceResult;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getParams() {
        return params;
    }
    public Object getServiceResult() {
        return serviceResult;
    }
}
