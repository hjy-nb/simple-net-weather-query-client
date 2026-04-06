package com.yao.learn;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserHelperTest {
    private final JsonParserHelper helper = new JsonParserHelper();

    @Test
    public void testSerialize() throws Exception {
        WeatherData data = new WeatherData(25.5, 60, "晴", 3.2, 10, "2023-05-01T12:00:00", 26.0);
        String json = helper.serialize(data);
        assertNotNull(json);
        assertTrue(json.contains("temp"));
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"北京\",\"temp\":25.5,\"humidity\":60}";
        JSONObject obj = helper.deserialize(json, JSONObject.class);
        assertEquals("北京", obj.getString("name"));
        assertEquals(25.5, obj.getDoubleValue("temp"), 0.01);
    }

    @Test
    public void testGetString() {
        JSONObject obj = new JSONObject();
        obj.put("name", "张三");
        assertEquals("张三", helper.getString(obj, "name", "默认"));
        assertEquals("默认", helper.getString(obj, "age", "默认"));
    }

    @Test
    public void testGetInt() {
        JSONObject obj = new JSONObject();
        obj.put("age", 25);
        assertEquals(25, helper.getInt(obj, "age", 0));
        assertEquals(0, helper.getInt(obj, "score", 0));
    }

    @Test
    public void testGetDouble() {
        JSONObject obj = new JSONObject();
        obj.put("temp", 25.5);
        assertEquals(25.5, helper.getDouble(obj, "temp", 0.0), 0.01);
        assertEquals(0.0, helper.getDouble(obj, "humidity", 0.0), 0.01);
    }

    @Test
    public void testGetBoolean() {
        JSONObject obj = new JSONObject();
        obj.put("isRaining", true);
        assertTrue(helper.getBoolean(obj, "isRaining", false));
        assertFalse(helper.getBoolean(obj, "isSnowing", false));
    }

    @Test
    public void testGetDateTime() {
        JSONObject obj = new JSONObject();
        obj.put("updateTime", "2026-04-05 14:30:00");
        LocalDateTime dateTime = helper.getDateTime(obj, "updateTime");
        assertNotNull(dateTime);
        assertEquals(2026, dateTime.getYear());
    }

    @Test
    public void testSanitizeJson() {
        String dirty = "  {'name':'张三',}  ";
        String clean = helper.sanitizeJson(dirty);
        assertNotNull(clean);
        assertTrue(clean.contains("\"name\""));
    }

    @Test
    public void testIsRequiredFieldsMissing() {
        JSONObject obj = new JSONObject();
        obj.put("cityName", "北京");
        Set<String> required = Set.of("cityName", "temperature");
        assertTrue(helper.isRequiredFieldsMissing(obj, required));

        obj.put("temperature", 25.5);
        assertFalse(helper.isRequiredFieldsMissing(obj, required));
    }

    @Test
    public void testSetDefaultValues() {
        JSONObject obj = new JSONObject();
        obj.put("cityName", "北京");
        Map<String, Object> defaults = Map.of("temperature", 0.0, "humidity", 0);
        helper.setDefaultValues(obj, defaults);
        assertEquals(0.0, obj.getDoubleValue("temperature"), 0.01);
        assertEquals(0, obj.getIntValue("humidity"));
    }

    @Test
    public void testParseArray() {
        String json = "[{\"name\":\"北京\"},{\"name\":\"上海\"}]";
        java.util.List<JSONObject> list = helper.parseArray(json, JSONObject.class);
        assertEquals(2, list.size());
        assertEquals("北京", list.getFirst().getString("name"));
    }
}
