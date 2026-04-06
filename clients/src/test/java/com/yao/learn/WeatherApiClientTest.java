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
        builder = new WeatherHttpClientBuilder();
        client = new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                "https://devapi.qweather.com/v7/weather/now",
                builder);
    }

    @Test
    public void testConstructorWithValidParameters() {
        assertNotNull(client);
        assertEquals("abcdefghijklmnopqrstuvwxyz123456", client.getApiKey());
        assertEquals("https://devapi.qweather.com/v7/weather/now", client.getBaseUrl());
    }

    @Test
    public void testConstructorWithNullBuilder() {
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                    "https://devapi.qweather.com/v7/weather/now",
                    null);
        });
    }

    @Test
    public void testConstructorWithInvalidApiKey() {
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("invalid",
                    "https://devapi.qweather.com/v7/weather/now",
                    builder);
        });
    }

    @Test
    public void testConstructorWithInvalidBaseUrl() {
        assertThrows(RuntimeException.class, () -> {
            new WeatherApiClient("abcdefghijklmnopqrstuvwxyz123456",
                    "not_a_url",
                    builder);
        });
    }

    @Test
    public void testIsValidApiKey() {
        assertTrue(client.isValidApiKey("abcdefghijklmnopqrstuvwxyz123456"));
        assertFalse(client.isValidApiKey("short"));
        assertFalse(client.isValidApiKey(null));
        assertFalse(client.isValidApiKey(""));
    }

    @Test
    public void testIsValidBaseUrl() {
        assertTrue(client.isValidBaseUrl("https://devapi.qweather.com/v7/weather/now"));
        assertTrue(client.isValidBaseUrl("http://localhost:8080/api"));
        assertFalse(client.isValidBaseUrl("not_a_url"));
        assertFalse(client.isValidBaseUrl(null));
    }

    @Test
    public void testIsValidCityName() {
        assertTrue(client.isValidCityName("北京"));
        assertTrue(client.isValidCityName("Shanghai"));
        assertTrue(client.isValidCityName("New York"));
        assertFalse(client.isValidCityName(""));
        assertFalse(client.isValidCityName(null));
    }

    @Test
    public void testBuildRequestUrlWithCity() {
        String url = client.buildRequestUrl("北京");
        assertTrue(url.startsWith("https://devapi.qweather.com/v7/weather/now?location="));
        assertTrue(url.contains("key=abcdefghijklmnopqrstuvwxyz123456"));
        assertTrue(url.contains("lang=zh"));
    }


    @Test
    public void testBuildRequestUrlWithInvalidCity() {
        assertThrows(IllegalArgumentException.class, () -> {
            client.buildRequestUrl("");
        });
    }

    @Test
    public void testAddDefaultHeaders() {
        client.addDefaultHeaders("User-Agent", "TestClient/1.0");
        assertEquals(1, client.getDefaultHeaders().size());
        assertEquals("TestClient/1.0", client.getDefaultHeaders().get("User-Agent"));
    }

    @Test
    public void testGetRequestCounter() {
        assertEquals(0, client.getRequestCounter());
    }


    @Test
    public void testClose() {
        assertDoesNotThrow(() -> {
            client.close();
        });
    }
}
