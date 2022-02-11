/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ming.eureka.aes.Encodes;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author lll 2015年10月26日
 */
@Slf4j
public class HttpClientUtil { 
	
	private static final CloseableHttpClient httpclient = HttpClientBuilder.create().build();
	
    /**
     * 向HTTPS地址发送POST请求
     * @param reqURL 请求地址
     * @param params 请求参数
     * @return 响应内容
     */ 
    @SuppressWarnings("finally") 
    public static String post(String reqURL, Map<String, String> params){ 
        String responseContent = null;                   //响应内容 
        try { 
            HttpPost httpPost = new HttpPost(reqURL);                        //创建HttpPost 
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //构建POST请求的表单参数 
            for(Map.Entry<String,String> entry : params.entrySet()){ 
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue())); 
            }
            
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8")); 
             
            HttpResponse response = httpclient.execute(httpPost); //执行POST请求 
            HttpEntity entity = response.getEntity();             //获取响应实体 
             
            if (null != entity) { 
                responseContent = EntityUtils.toString(entity, "UTF-8"); 
                EntityUtils.consume(entity); //Consume response content 
            } 
            httpPost.releaseConnection();
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace(); 
        } catch (ClientProtocolException e) { 
            e.printStackTrace(); 
        } catch (ParseException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            return responseContent; 
        } 
    } 
    
    /**
	 * 发送GET请求
	 * 
	 * @param reqURL
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String get(String reqURL, Map<String, String> params) {
		String responseContent = null; // 响应内容
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		try {
			String url = reqURL.replace("?", "");
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (i == 0) {
					url = StringUtils.join(url,"?");
					i++;
				} else {
					url = StringUtils.join(url,"&");
				}
				url = StringUtils.join(url,entry.getKey(),"=",
						Encodes.encodeUrlSafeBase64(entry.getValue().getBytes()));
			}
			httpGet = new HttpGet(url); // 创建HttpPost

			response = httpclient.execute(httpGet); // 执行请求
			if (response != null) {
				HttpEntity entity = response.getEntity(); // 获取响应实体
				
				if (null != entity) {
					responseContent = EntityUtils.toString(entity, "UTF-8");
					EntityUtils.consume(entity); // Consume response content
				}
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
			return responseContent;
		}
	}
	
	/**
	 * 下载文件
	 * @param url
	 * @return
	 */
	public static BufferedHttpEntity downloadFile(String url) {
		log.info("download file  {}", url);
		HttpGet httpGet =  new HttpGet(url);
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			@Cleanup CloseableHttpResponse response = null;
			response = httpclient.execute(httpGet);
			
			log.info("download file resp:{}", StringUtils.join(response.getAllHeaders(),","));
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
			} else {
				return null;
			}
			
			return (BufferedHttpEntity) entity;
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			httpGet.releaseConnection();
		}
		return null;
	}
	
	
	public static void main(String[] args) throws Exception {
		BufferedHttpEntity f = HttpClientUtil.downloadFile("http://vweixinf.tc.qq.com/102/20202/snsvideodownload?filekey=30270201010420301e020166040253480410fba41f3c8a93e53ac4208109920d085b0203051b5c0400&bizid=1023&hy=SH&fileparam=302c0201010425302302040d103e68020458315a1d02024eea02031e8d7f02030f424002041070370a0201000400");
		f.getContent();
	}
}
