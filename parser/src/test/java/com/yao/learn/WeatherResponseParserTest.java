package com.yao.learn;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherResponseParserTest {


    //默认是beforeeach行为
    private final JsonParserHelper helper=new JsonParserHelper();
    private final WeatherResponseParser parser=new WeatherResponseParser(helper);

    @Test
    public void testParseValidWeatherResponse() throws Exception {
        // 测试解析完整的合法天气响应JSON
        String json = "{\"code\":200,\"message\":\"success\",\"data\":{\"temperature\":25.5,\"humidity\":60,\"weather\":\"晴\",\"windSpeed\":3.2,\"visibility\":10,\"updateTime\":\"2026-04-05 14:30:00\",\"feelsLike\":26.0},\"location\":{\"cityName\":\"北京\",\"cityId\":\"101010100\",\"country\":\"中国\",\"longitude\":116.4,\"latitude\":39.9,\"timezone\":\"Asia/Shanghai\"}}";

        WeatherResponse response = parser.parse(json);

        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(25.5, response.getData().getTemperature(), 0.01);
        assertNotNull(response.getLocation());
        assertEquals("北京", response.getLocation().getCityName());
    }

    @Test
    public void testParseWeatherData() throws Exception {
        // 测试从JSONObject解析WeatherData对象
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("temperature", 25.5);
        jsonObj.put("humidity", 60);
        jsonObj.put("weather", "晴");
        jsonObj.put("windSpeed", 3.2);
        jsonObj.put("visibility", 10);
        jsonObj.put("updateTime", "2026-04-05 14:30:00");
        jsonObj.put("feelsLike", 26.0);

        WeatherData data = parser.parseWeatherData(jsonObj);

        assertNotNull(data);
        assertEquals(25.5, data.getTemperature(), 0.01);
        assertEquals(60, data.getHumidity());
        assertEquals("晴", data.getWeather());
    }

    @Test
    public void testParseLocation() {
        // 测试从JSONObject解析Location对象
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cityName", "北京");
        jsonObj.put("cityId", "101010100");
        jsonObj.put("country", "中国");
        jsonObj.put("longitude", 116.4);
        jsonObj.put("latitude", 39.9);
        jsonObj.put("timezone", "Asia/Shanghai");

        Location location = parser.parseLocation(jsonObj);

        assertNotNull(location);
        assertEquals("北京", location.getCityName());
        assertEquals(116.4, location.getLongitude(), 0.01);
    }

    @Test
    public void testValidateWeatherData() {
        // 测试验证合法的WeatherData对象
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("temperature", 25.5);
        jsonObj.put("humidity", 60);
        jsonObj.put("weather", "晴");
        jsonObj.put("windSpeed", 3.2);
        jsonObj.put("visibility", 10);
        jsonObj.put("updateTime", "2026-04-05 14:30:00");
        jsonObj.put("feelsLike", 26.0);

        WeatherData data = parser.parseWeatherData(jsonObj);
        assertTrue(parser.validateWeatherData(data));
    }

    @Test
    public void testValidateLocation() {
        // 测试验证合法的Location对象
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cityName", "北京");
        jsonObj.put("cityId", "101010100");
        jsonObj.put("country", "中国");
        jsonObj.put("longitude", 116.4);
        jsonObj.put("latitude", 39.9);
        jsonObj.put("timezone", "Asia/Shanghai");

        Location location = parser.parseLocation(jsonObj);
        assertTrue(parser.validateLocation(location));
    }

    @Test
    public void testExtractStatusCode() {
        // 测试从JSONObject提取状态码
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("code", 200);

        assertEquals(200, parser.extractStatusCode(jsonObj));
    }

    @Test
    public void testExtractStatusMessage() {
        // 测试从JSONObject提取状态消息
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("message", "成功");

        assertEquals("成功", parser.extractStatusMessage(jsonObj));
    }

    @Test
    public void testExtractWeatherDataJson() {
        // 测试提取嵌套的天气数据JSONObject
        JSONObject jsonObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        dataObj.put("temperature", 25.5);
        jsonObj.put("data", dataObj);

        JSONObject extracted = parser.extractWeatherDataJson(jsonObj);
        assertNotNull(extracted);
        assertEquals(25.5, extracted.getDoubleValue("temperature"), 0.01);
    }

    @Test
    public void testExtractLocationJson() {
        // 测试提取嵌套的位置信息JSONObject
        JSONObject jsonObj = new JSONObject();
        JSONObject locationObj = new JSONObject();
        locationObj.put("cityName", "北京");
        jsonObj.put("location", locationObj);

        JSONObject extracted = parser.extractLocationJson(jsonObj);
        assertNotNull(extracted);
        assertEquals("北京", extracted.getString("cityName"));
    }

    @Test
    public void testConstructorWithNullHelper() {
        // 测试构造器传入null时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            new WeatherResponseParser(null);
        });
    }
}
