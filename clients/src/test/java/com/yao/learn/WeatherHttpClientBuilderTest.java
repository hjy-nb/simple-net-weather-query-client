package com.yao.learn;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherHttpClientBuilderTest {

    @Test
    public void testBuildWithDefaultConfig() {
        WeatherHttpClientBuilder builder = new WeatherHttpClientBuilder();
        CloseableHttpClient client = builder.build();

        assertNotNull(client);
        assertEquals(5000, builder.getConnectTimeout());
        assertEquals(10000, builder.getReadTimeout());
        assertEquals(10, builder.getMaxConnections());
        assertEquals(5, builder.getMaxPerRoute());
        assertFalse(builder.isUseProxy());
    }

    @Test
    public void testBuildWithCustomConfig() {
        WeatherHttpClientBuilder builder = new WeatherHttpClientBuilder();
        CloseableHttpClient client = builder
                .setConnectTimeout(3000)
                .setReadTimeout(8000)
                .setMaxConnections(20)
                .setMaxPerRoute(10)
                .build();

        assertNotNull(client);
        assertEquals(3000, builder.getConnectTimeout());
        assertEquals(8000, builder.getReadTimeout());
        assertEquals(20, builder.getMaxConnections());
        assertEquals(10, builder.getMaxPerRoute());
    }

    @Test
    public void testBuildWithProxy() {
        java.net.Proxy proxy = new java.net.Proxy(
                java.net.Proxy.Type.HTTP,
                new java.net.InetSocketAddress("127.0.0.1", 8080)
        );

        WeatherHttpClientBuilder builder = new WeatherHttpClientBuilder();
        CloseableHttpClient client = builder
                .setProxy(proxy)
                .build();

        assertNotNull(client);
        assertTrue(builder.isUseProxy());
        assertNotNull(builder.getProxy());
    }

    @Test
    public void testFluentAPI() {
        WeatherHttpClientBuilder builder = new WeatherHttpClientBuilder();
        WeatherHttpClientBuilder returned = builder.setConnectTimeout(5000);

        assertSame(builder, returned);
    }
}
