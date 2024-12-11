package com.tend.proxy.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Component
public class HttpRequest {
    @Value("${http.proxy.enable}")
    private  Boolean enableProxy;
    @Value("${http.proxy.host}")
    private  String proxyHost;
    @Value("${http.proxy.port}")
    private  Integer proxyPort;


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection;
            // 打开和URL之间的连接
            if (enableProxy){
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                connection = realUrl.openConnection(proxy);
            }else {
                connection= realUrl.openConnection();
            }
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        PrintWriter outPrintWriter = null;
        String result = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn;
            // 打开和URL之间的连接
            if (enableProxy){
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                conn = (HttpURLConnection) realUrl.openConnection(proxy);
            }else {
                conn= (HttpURLConnection) realUrl.openConnection();
            }
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 发送POST请求必须设置如下两行
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            outPrintWriter = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            outPrintWriter.print(param);
            // flush输出流的缓冲
            outPrintWriter.flush();
            // 解压数据
            GZIPInputStream gzin = new GZIPInputStream(
                    conn.getInputStream());

            byte[] buf = new byte[1024];
            int num;
            while ((num = gzin.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, num);
            }
            gzin.close();
            // 用户UTF-8编码格式转换
            result = out.toString("UTF-8");
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}