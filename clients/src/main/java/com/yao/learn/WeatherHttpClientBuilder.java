package com.yao.learn;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import javax.net.ssl.SSLContext;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

//创建和配置 HttpClient 实例
public class WeatherHttpClientBuilder {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();
    
    private int connectTimeout;      // 连接超时 (ms)
    private int readTimeout;        // 读取超时 (ms)
    private int maxConnections;        // 最大连接数
    private int maxPerRoute;            // 每路由最大连接
    private boolean useProxy;       // 是否使用代理
    private Proxy proxy;                    // 代理配置
    private SSLContext sslContext;          // SSL 上下文

    public WeatherHttpClientBuilder() {
        BUSINESS_LOGGER.info("开始创建HttpClient实例");

        connectTimeout = LoadConfig.getPropertyInt("httpclient.connect.timeout",5000);
        readTimeout = LoadConfig.getPropertyInt("httpclient.read.timeout",10000);
        maxConnections = LoadConfig.getPropertyInt("httpclient.max.connections",10);
        maxPerRoute = LoadConfig.getPropertyInt("httpclient.max.per.route",5);
        useProxy = LoadConfig.getPropertyBoolean("httpclient.proxy.enabled",false);

    }

    //设置连接超时
    public WeatherHttpClientBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;

        return this;
    }

    //设置读取超时
    public WeatherHttpClientBuilder setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;

        return this;
    }

    //设置最大连接数
    public WeatherHttpClientBuilder setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;

        return this;
    }

    //设置每路由最大连接
    public WeatherHttpClientBuilder setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;

        return this;
    }

    //设置代理
    public WeatherHttpClientBuilder setProxy(Proxy proxy) {
        this.useProxy = true;
        this.proxy = proxy;

        return this;
    }

    //设置SSL上下文
    public WeatherHttpClientBuilder setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;

        return this;
    }

    //创建连接管理器
    public PoolingHttpClientConnectionManager createConnectionManager()
    {
        try {
            PoolingHttpClientConnectionManager connectionManager;
            ConnectionConfig connectionConfig = createConnectionConfig();

            if (sslContext != null) {
                DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);
                connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                        .setTlsSocketStrategy(tlsStrategy)
                        .setMaxConnTotal(maxConnections)
                        .setMaxConnPerRoute(maxPerRoute)
                        .setDefaultConnectionConfig(connectionConfig)
                        .build();
            } else {
                connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(maxConnections)
                        .setMaxConnPerRoute(maxPerRoute)
                        .setDefaultConnectionConfig(connectionConfig)
                        .build();
            }

            return connectionManager;
        } catch (Exception e) {
            ERROR_LOGGER.error("创建连接管理器异常", e);
            throw new RuntimeException(e);
        }
    }

    //创建请求配置
    //Timeout用于设置连接超时和读取超时
    //RequestConfig用于配置http响应超时
    public RequestConfig createRequestConfig()
    {
        try{
             return RequestConfig.custom()
                    .setResponseTimeout(Timeout.of(readTimeout, TimeUnit.MILLISECONDS))
                    .build();
        }
        catch (Exception e) {
            ERROR_LOGGER.error("创建响应请求异常",e);
            throw new RuntimeException(e);
        }
    }

    //创建connection config 用于http连接超时
    public ConnectionConfig createConnectionConfig()
    {
        try{
             return ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.of(connectTimeout, TimeUnit.MILLISECONDS))
                    .build();
        }
        catch (Exception e) {
            ERROR_LOGGER.error("创建connection config异常",e);
            throw new RuntimeException(e);
        }
    }

    //创建HttpClient实例
    public CloseableHttpClient build() {
        PoolingHttpClientConnectionManager connectionManager = createConnectionManager();
        RequestConfig requestConfig = createRequestConfig();

        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig);

        if (useProxy && proxy != null) {
            java.net.InetSocketAddress addr = (java.net.InetSocketAddress) proxy.address();
            HttpHost proxyHost = new HttpHost(addr.getHostName(), addr.getPort());
            builder.setRoutePlanner(new DefaultProxyRoutePlanner(proxyHost));
        }

        BUSINESS_LOGGER.info("创建 HttpClient 实例成功 - 连接超时:{}ms, 读取超时:{}ms, 最大连接数:{}, 每路由最大:{}, 使用代理:{}\n",
                connectTimeout, readTimeout, maxConnections, maxPerRoute, useProxy);


        return builder.build();
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }
}
