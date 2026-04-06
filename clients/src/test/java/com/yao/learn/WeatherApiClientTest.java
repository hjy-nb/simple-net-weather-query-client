package com.yao.learn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherApiClientTest {

    private WeatherApiClient client;
    private WeatherHttpClientBuilder builder;

    @BeforeEach
    public void setUp() {
        // 初始化HTTP客户端构建器（使用默认配置）
        builder = new WeatherHttpClientBuilder();

        // 创建API客户端实例（使用测试用的apiKey和baseUrl）
        client = new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                "https://api.weather.com/v1",
                builder);
    }

    @Test
    public void testConstructorWithValidParameters() {
        // 测试使用合法参数构造客户端
        assertNotNull(client);
        assertEquals("abcdefghijklmnopqrstuvwxyz123456", client.getApiKey());
        assertEquals("https://api.weather.com/v1", client.getBaseUrl());
    }

    @Test
    public void testConstructorWithNullBuilder() {
        // 测试构造器传入null Builder时抛出异常
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                    "https://api.weather.com/v1",
                    null);
        });
    }

    @Test
    public void testConstructorWithInvalidApiKey() {
        // 测试构造器传入无效API密钥时抛出异常
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("invalid",
                    "https://api.weather.com/v1",
                    builder);
        });
    }

    @Test
    public void testConstructorWithInvalidBaseUrl() {
        // 测试构造器传入无效基础URL时抛出异常
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                    "not_a_url",
                    builder);
        });
    }

    @Test
    public void testIsValidApiKey() {
        // 测试API密钥验证逻辑
        assertTrue(client.isValidApiKey("abcdefghijklmnopqrstuvwxyz123456")); // 32位合法
        assertFalse(client.isValidApiKey("short")); // 太短
        assertFalse(client.isValidApiKey(null)); // null
        assertFalse(client.isValidApiKey("")); // 空字符串
    }

    @Test
    public void testIsValidBaseUrl() {
        // 测试基础URL验证逻辑
        assertTrue(client.isValidBaseUrl("https://api.weather.com"));
        assertTrue(client.isValidBaseUrl("http://localhost:8080/api"));
        assertFalse(client.isValidBaseUrl("not_a_url"));
        assertFalse(client.isValidBaseUrl(null));
    }

    @Test
    public void testIsValidCityName() {
        // 测试城市名称验证逻辑
        assertTrue(client.isValidCityName("北京"));
        assertTrue(client.isValidCityName("Shanghai"));
        assertTrue(client.isValidCityName("New York"));
        assertFalse(client.isValidCityName(""));
        assertFalse(client.isValidCityName(null));
    }

    @Test
    public void testBuildRequestUrlWithCity() {
        String url = client.buildRequestUrl("北京");
        assertTrue(url.startsWith("https://api.weather.com/v1?location="));
        assertTrue(url.contains("key=abcdefghijklmnopqrstuvwxyz123456"));
        assertTrue(url.contains("unit=c"));
        assertTrue(url.contains("lang=zh"));
    }

    @Test
    public void testBuildRequestUrlWithCityAndCountry() {
        // 测试构建城市+国家请求URL
        String url = client.buildRequestUrl("北京", "中国");
        assertTrue(url.contains("location=%E5%8C%97%E4%BA%AC,%E4%B8%AD%E5%9B%BD"));
        assertTrue(url.contains("key=abcdefghijklmnopqrstuvwxyz123456"));
    }

    @Test
    public void testBuildRequestUrlWithInvalidCity() {
        // 测试构建URL时传入无效城市名抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            client.buildRequestUrl("");
        });
    }

    @Test
    public void testAddDefaultHeaders() {
        // 测试添加默认请求头
        client.addDefaultHeaders("User-Agent", "TestClient/1.0");
        assertEquals(1, client.getDefaultHeaders().size());
        assertEquals("TestClient/1.0", client.getDefaultHeaders().get("User-Agent"));
    }

    @Test
    public void testGetRequestCounter() {
        // 测试请求计数器初始值为0
        assertEquals(0, client.getRequestCounter());
    }

    @Test
    public void testGetAverageRequestTime() {
        // 测试计算平均请求时间
        double avgTime = client.getAverageRequestTime(1000, 5);
        assertEquals(200.0, avgTime, 0.01);
    }

    @Test
    public void testClose() {
        // 测试关闭HTTP客户端不抛异常
        assertDoesNotThrow(() -> {
            client.close();
        });
    }

    @Test
    public void testQueryWeatherWithNullResponse() {
        // 测试查询天气时响应体为空抛出IOException
        // 注意：这个测试需要真实的网络连接，实际项目中应该用Mock
        assertThrows(IOException.class, () -> {
            client.queryWeather("北京");
        });
    }
}
