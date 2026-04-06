package com.yao.learn;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;

public class WeatherResponseParser {
    private static final Logger BUSINESS_LOGGER=LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER=LoggerManagement.getErrorLogger();

    private final JsonParserHelper parserHelper;          // 解析助手

    public WeatherResponseParser(JsonParserHelper parserHelper){
        BUSINESS_LOGGER.info("开始初始化WeatherResponseParser");

        if(parserHelper==null){
            ERROR_LOGGER.error("JsonParserHelper不能为空");
            BUSINESS_LOGGER.error("初始化WeatherResponseParser失败");

            throw new IllegalArgumentException("JsonParserHelper不能为空");
        }

        this.parserHelper = parserHelper;
    }

    //解析天气响应
    public WeatherResponse parse(String json) throws Exception {
        return parserHelper.deserialize(json, WeatherResponse.class);
    }

    //解析天气数据
    public WeatherData parseWeatherData(JSONObject jsonObj){
        return jsonObj.toJavaObject(WeatherData.class);   //自动映射到构造函数所需字段  |有无参构造器+setter
    }

    //解析位置信息
    public Location parseLocation(JSONObject jsonObj) {
        return jsonObj.toJavaObject(Location.class);
    }

    //验证天气数据
    public boolean validateWeatherData(WeatherData weatherData) {
        return weatherData.isValid(weatherData);
    }

    //验证位置信息
    public boolean validateLocation(Location location) {
        return location.isValid(location);
    }

    //验证天气响应
    public boolean validateWeatherResponse(WeatherResponse weatherResponse) {
        return weatherResponse.isValid(weatherResponse);
    }

    //提取状态码
    public int extractStatusCode(JSONObject jsonObj) {
        return parserHelper.getInt(jsonObj, "code", 0);
    }


    public JsonParserHelper getParserHelper() {
        return parserHelper;
    }
}
