package com.tend.proxy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tend.proxy.common.api.vo.Result;
import com.tend.proxy.exception.ApiException;
import com.tend.proxy.service.IApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service")
public class ProxyController {


    @Autowired
    private IApiService apiService;

    @RequestMapping("/getEntInvoiceInfo")
    public Result getEntInvoiceInfo(String entName) throws Exception {
        String result = null;
        String param = "{\"entName\":\"" + entName + "\"}";
        try {
            result = apiService.getEntInvoiceInfo(entName);
            JSONObject jsonObject = JSON.parseObject(result);
            validResult(jsonObject, param);
            return Result.ok(jsonObject);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            ApiException apiException = new ApiException(e.getMessage(), e, "getEntInvoiceInfo", param);
            throw apiException;
        }
    }


    @RequestMapping("/getOpeningBankInfo")
    public Result getOpeningBankInfo(String bankAccount) throws Exception {
        String result = null;
        String param = "{\"bankAccount\":\"" + bankAccount + "\"}";
        try {
            result = apiService.getEntInvoiceInfo(bankAccount);
            JSONObject jsonObject = JSON.parseObject(result);
            validResult(jsonObject, param);
            return Result.ok(jsonObject);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            ApiException apiException = new ApiException(e.getMessage(), e, "getEntInvoiceInfo", param);
            throw apiException;
        }
    }

    private void validResult(JSONObject jsonObject, String param) throws ApiException {
        String ismatch = jsonObject.getString("ISMATCH");
        if ("0".equals(ismatch)) {
            JSONObject resultdata = jsonObject.getJSONObject("RESULTDATA");
            throw new ApiException("API查询错误", "getEntInvoiceInfo", param, resultdata);
        }
    }


}
