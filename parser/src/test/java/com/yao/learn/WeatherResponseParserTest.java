package com.yao.learn;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherResponseParserTest {

    private final JsonParserHelper helper = new JsonParserHelper();
    private final WeatherResponseParser parser = new WeatherResponseParser(helper);

    @Test
    public void testParseValidWeatherResponse() throws Exception {
        String json = "{\"code\":\"200\",\"updateTime\":\"2026-04-06T14:18+08:00\",\"now\":{\"obsTime\":\"2026-04-06T14:08+08:00\",\"temp\":\"16\",\"feelsLike\":\"13\",\"text\":\"多云\",\"windSpeed\":\"10\",\"humidity\":\"15\",\"vis\":\"30\"},\"location\":{\"name\":\"北京\",\"id\":\"101010100\",\"country\":\"中国\",\"lon\":116.4,\"lat\":39.9,\"tz\":\"Asia/Shanghai\"}}";

        WeatherResponse response = parser.parse(json);

        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(16.0, response.getData().getTemperature(), 0.01);
        assertNotNull(response.getLocation());
        assertEquals("北京", response.getLocation().getCityName());
    }

    @Test
    public void testParseWeatherData() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("temp", "25.5");
        jsonObj.put("humidity", "60");
        jsonObj.put("text", "晴");
        jsonObj.put("windSpeed", "3.2");
        jsonObj.put("vis", "10");
        jsonObj.put("obsTime", "2026-04-05 14:30:00");
        jsonObj.put("feelsLike", "26.0");

        WeatherData data = parser.parseWeatherData(jsonObj);

        assertNotNull(data);
        assertEquals(25.5, data.getTemperature(), 0.01);
        assertEquals(60, data.getHumidity());
        assertEquals("晴", data.getWeather());
    }

    @Test
    public void testParseLocation() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", "北京");
        jsonObj.put("id", "101010100");
        jsonObj.put("country", "中国");
        jsonObj.put("lon", 116.4);
        jsonObj.put("lat", 39.9);
        jsonObj.put("tz", "Asia/Shanghai");

        Location location = parser.parseLocation(jsonObj);

        assertNotNull(location);
        assertEquals("北京", location.getCityName());
        assertEquals(116.4, location.getLongitude(), 0.01);
    }

    @Test
    public void testValidateWeatherData() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("temp", "25.5");
        jsonObj.put("humidity", "60");
        jsonObj.put("text", "晴");
        jsonObj.put("windSpeed", "3.2");
        jsonObj.put("vis", "10");
        jsonObj.put("obsTime", "2026-04-05 14:30:00");
        jsonObj.put("feelsLike", "26.0");

        WeatherData data = parser.parseWeatherData(jsonObj);
        assertTrue(parser.validateWeatherData(data));
    }

    @Test
    public void testValidateLocation() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", "北京");
        jsonObj.put("id", "101010100");
        jsonObj.put("country", "中国");
        jsonObj.put("lon", 116.4);
        jsonObj.put("lat", 39.9);
        jsonObj.put("tz", "Asia/Shanghai");

        Location location = parser.parseLocation(jsonObj);
        assertTrue(parser.validateLocation(location));
    }

    @Test
    public void testExtractStatusCode() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("code", 200);

        assertEquals(200, parser.extractStatusCode(jsonObj));
    }


    @Test
    public void testConstructorWithNullHelper() {
        assertThrows(IllegalArgumentException.class, () -> {
            new WeatherResponseParser(null);
        });
    }
}
