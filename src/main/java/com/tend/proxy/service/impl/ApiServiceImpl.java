package com.tend.proxy.service.impl;

import com.quantum.auth.RSA2048;
import com.tend.proxy.common.utils.AuthUtils;
import com.tend.proxy.common.utils.HttpRequest;
import com.tend.proxy.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xidea.el.json.JSONDecoder;
import org.xidea.el.json.JSONEncoder;
import com.google.common.collect.Maps;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
public class ApiServiceImpl implements IApiService {

    @Value("${saas.service.uid}")
    private String uid;
    @Value("${saas.service.url}")
    private String serviceUrl;
    @Value("${saas.auth.fileName}")
    private String authFileName;
    @Value("${saas.auth.path}")
    private String authPath;

    private static final String ENT_INVOICE_INFO_SERVICE_NAME = "getOpeningBankInfo";
    private static final String OPENING_BANK_INFO_SERVICE_NAME = "getOpeningBankInfo";
    @Override
    public String getEntInvoiceInfo(String entName) throws Exception{
        String serverName = ENT_INVOICE_INFO_SERVICE_NAME;
        Map<String, Object> params = Maps.newHashMap();
        params.put("entName", entName);
        return executeServiceApi(uid, serverName, params);
    }

    @Override
    public String getOpeningBankInfo(String bankAccount) throws Exception {

        String serverName = OPENING_BANK_INFO_SERVICE_NAME;
        Map<String, Object> params = Maps.newHashMap();
        params.put("bankAccount", bankAccount);
        return executeServiceApi(uid, serverName, params);
    }

    private String executeServiceApi(String uid, String serverName, Map<String, Object> params) throws Exception {
        //加密过程
        String serverParams = getServerJsonStr(uid, serverName, params);
        String postParams = MessageFormat.format("uid={0}&data={1}", uid, getPostJsonStr(serverParams));
        //获取数据   String postParams = MessageF
        String result = HttpRequest.sendPost(serviceUrl, postParams);
        result = URLDecoder.decode(result, "UTF-8");
        //转换数据
        Map<String, Object> resultMap = JSONDecoder.decode(result);
        //解密数据
        String value = RasAndDesDecode(resultMap);
        return value;
    }

    /**
     * 解密过程
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String RasAndDesDecode(Map<String, Object> data) throws Exception {
        return AuthUtils.decode(data, getKey());
    }

    /**
     * 获取公钥
     *
     * @return
     * @throws Exception
     */
    private Key getKey() throws Exception {
        Key key = null;
        try {
            ClassPathResource resource = new ClassPathResource(authPath+ File.separator+authFileName);
            log.info("path:{}",resource.getFile().getPath());
            File autFile = resource.getFile();
            if (!autFile.exists()) {
                throw new Exception("认证文件不存");
            }
            String path = autFile.getPath();
            key = RSA2048.getKey(path);//密钥路径
        } catch (Exception ex) {
            throw ex;
        }
        return key;
    }

    /**
     * 服务参数加密
     *
     * @param serverParams
     * @return 返回加密后的密文
     * @throws Exception
     */
    private String getPostJsonStr(String serverParams) throws Exception {
        Map<String, Object> authParams = AuthUtils.encode(serverParams, getKey());
        return URLEncoder.encode(JSONEncoder.encode(authParams), "UTF-8");
    }

    /**
     * 服务参数
     *
     * @param uid        用户Uid
     * @param serverName 服务方法名称
     * @return 服务相关参数的JSON数据
     */
    private String getServerJsonStr(String uid, String serverName,Map<String, Object> params) {
        Map<String, Object> conditoinMap = new HashMap<String, Object>();
        conditoinMap.put("uid", uid);
        conditoinMap.put("service", serverName);

        conditoinMap.put("params", params);

        return JSONEncoder.encode(conditoinMap);
    }


}
