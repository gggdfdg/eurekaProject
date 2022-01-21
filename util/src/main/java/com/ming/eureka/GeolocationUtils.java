package com.ming.eureka;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 地址位置类
 * 
 * @author lazier
 */
public class GeolocationUtils {

    private static final int STATUS_SUCCESS = 0;

    /**
     * ip获取地理位置接口
     */
    private static final String API_LOCATION_API = "http://api.map.baidu.com/location/";

    /**
     * 接口秘钥
     */
    private static final String API_KEY = "XoZYVVPR337Ot2AOBfUqLEum";

    public static Geolocation getLocation(String ip) {
        Geolocation geolocation = new Geolocation();
        try {
            geolocation.setIp(ip);
            String urlString = API_LOCATION_API + "ip?ak=" + API_KEY + "&ip=" + ip + "&coor=bd09ll";
            StringBuffer jsonBuffer = new StringBuffer();
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            Map jsonObject = mapper.readValue(jsonBuffer.toString(), Map.class);
            if (jsonObject.get("status") != null && (Integer) jsonObject.get("status") == STATUS_SUCCESS) {
                Map result = (Map) jsonObject.get("content");
                if (result != null) {
                    geolocation.setAddress(result.get("address").toString());
                    Map point = (Map) result.get("point");
                    Map addressDetail = (Map) result.get("address_detail");
                    geolocation.setLongitude(Double.valueOf(point.get("x").toString()));
                    geolocation.setLatitude(Double.valueOf(point.get("y").toString()));
                    geolocation.setProvince(addressDetail.get("province").toString());
                    geolocation.setCity(addressDetail.get("city").toString());
                    geolocation.setStreet(addressDetail.get("street").toString());
                    geolocation.setStreetNumber(addressDetail.get("street_number").toString());
                    geolocation.setDistrict(addressDetail.get("district").toString());

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return geolocation;
    }
}
