package com.tend.proxy.service;


public interface IApiService {

    String getEntInvoiceInfo(String entName) throws Exception;

    String getOpeningBankInfo(String bankAccount) throws Exception;

}
