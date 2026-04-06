package com.yao.learn;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WeatherApiClient {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private final CloseableHttpClient httpClient;         // HTTP客户端实例
    private final String apiKey;                          // API密钥
    private final String baseUrl;                         // API基础URL
    private final int retryCount;                     // 重试次数   可执行请求次数
    private final int retryDelay;                  // 重试延迟(ms)  每次重新执行请求的延迟时间
    private final Map<String, String> defaultHeaders;     // 默认请求头
    private final AtomicInteger requestCounter;           // 请求计数器
    private final WeatherResponseParser parser;
    private final Map<String, String> countryConvert;     //文档示例格式,将搜索范围限定在指定的国家或地区内。

    public WeatherApiClient(String apiKey, String baseUrl, WeatherHttpClientBuilder builder){
        BUSINESS_LOGGER.info("开始创建WeatherApiClient实例");

        List<String> errorMessages = getErrorMessages(apiKey, baseUrl, builder);

        if(!errorMessages.isEmpty())
        {
            ERROR_LOGGER.error("创建WeatherApiClient实例异常 {}",String.join("\n", errorMessages));
            throw new RuntimeException("创建WeatherApiClient实例异常");
        }
        try {
            this.apiKey = apiKey;
            this.baseUrl = baseUrl;
            this.httpClient = builder.build();
            this.retryCount = LoadConfig.getPropertyInt("httpclient.retry.count", 3);
            this.retryDelay = LoadConfig.getPropertyInt("httpclient.retry.delay", 1000);
            this.requestCounter = new AtomicInteger(0);
            this.defaultHeaders = new HashMap<>();
            JsonParserHelper parserHelper = new JsonParserHelper();
            this.parser = new WeatherResponseParser(parserHelper);

            countryConvert = Map.of("中国","cn",
                    "美国","us",
                    "英国","gb",
                    "日本","jp",
                    "韩国","kr");

            BUSINESS_LOGGER.info("创建WeatherApiClient实例成功 - API密钥:{}, 基础URL:{},",
                    apiKey, baseUrl);
        } catch (Exception e) {
            ERROR_LOGGER.error("创建WeatherApiClient实例异常 {}",e.getMessage());
            throw e;
        }
    }

    //
    private List<String> getErrorMessages(String apikey, String baseUrl,WeatherHttpClientBuilder builder) {
        List<String> errorMessages = new ArrayList<>();

        if(!isValidApiKey(apikey))
        {
            errorMessages.add("无效的API密钥"+apikey);
        }
        if(!isValidBaseUrl(baseUrl))
        {
            errorMessages.add("无效的基础URL"+baseUrl);
        }
        if(builder==null)
        {
            errorMessages.add("无效的HttpClientBuilder");
        }

        return errorMessages;
    }

    //判断api密钥是否合法
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && !apiKey.isEmpty()&& apiKey.matches( "^[a-zA-Z0-9]{32,64}$");
    }

    //判断基础URL是否合法
    public boolean isValidBaseUrl(String baseUrl) {

        return baseUrl != null && !baseUrl.isEmpty()&& baseUrl.matches("^https?://[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?(:\\d+)?(/.*)?$");
    }

    //判断城市名称是否合法
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidCityName(String cityName) {
        return cityName != null && !cityName.isEmpty() && cityName.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-]{1,50}$");
    }

    //判断国家名称是否合法
    public boolean isValidCountryName(String countryName) {
        return countryName != null && !countryName.isEmpty() && countryName.matches("^[\\u4e00-\\u9fa5a-zA-Z\\s]{1,50}$");
    }

    //构建请求URL：协议://域名:端口/路径?查询参数 | 查询参数：城市、API密钥、单位、语言、扩展信息
    public String buildRequestUrl(String city) {
        if(!isValidCityName(city)){
            throw new IllegalArgumentException("无效的城市名称");
        }
         return String.format("%s?location=%s&key=%s&lang=zh", baseUrl, URLEncoder.encode(city, StandardCharsets.UTF_8),apiKey);
    }

    public String buildRequestUrl(String city, String countryName) {
        if (!isValidCityName(city)) {
            throw new IllegalArgumentException("无效的城市名称");
        }
        if (!isValidCountryName(countryName) || !countryConvert.containsKey(countryName)) {
            throw new IllegalArgumentException("无效的国家名称");
        }
        return String.format("%s?location=%s&range=%s&key=%s&lang=zh",
                baseUrl,
                URLEncoder.encode(city, StandardCharsets.UTF_8),
                URLEncoder.encode(countryConvert.get(countryName), StandardCharsets.UTF_8),
                apiKey);
    }

    //添加默认请求头
    //请求头用于传递元数据给服务器，包括：内容类型、认证信息、客户端标识、缓存控制、编码格式等。第一个参数是元数据名称，第二个参数是对应值。
    public void addDefaultHeaders(String key, String value) {
        defaultHeaders.put(key, value);
    }

    //构建GET请求
    public HttpGet buildGetRequest(String url)
    {
        HttpGet request = new HttpGet(url);

        if(!defaultHeaders.isEmpty()){
            defaultHeaders.forEach(request::addHeader);
        }

        requestCounter.incrementAndGet();

        return request;
    }

    //执行GET请求，并返回响应体
    public String executeGetRequest(String url) throws IOException {
        BUSINESS_LOGGER.info("开始执行GET请求 - URL:{}", url);

        HttpGet request = buildGetRequest(url);

        final long startTime = System.currentTimeMillis();

        for(int i = 0; i < retryCount; i++) {
            final int count=i+1;
            try {
                return httpClient.execute(request, response -> {
                    int statusCode = response.getCode();
                    if (statusCode == 200) {

                        BUSINESS_LOGGER.info("请求成功 - 响应状态码:{}, -响应平均时间:{}", statusCode,getAverageRequestTime(startTime,count));

                        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    } else {
                        throw new IOException("请求失败，状态码: " + statusCode);
                    }
                });
            } catch (IOException e) {
                if (i < retryCount - 1) {
                    BUSINESS_LOGGER.warn("请求失败，正在重试 - 重试次数:{}, 重试延迟:{}ms", i + 1, retryDelay);

                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();

                        ERROR_LOGGER.error("请求失败，线程被中断");
                        throw e;
                    }
                } else {
                    ERROR_LOGGER.error("请求失败，重试次数已用完 ");

                    throw e;
                }
            }
        }

        return null;
    }

    //获取请求计数器值
    public int getRequestCounter() {
        return requestCounter.get();
    }

    //获取平均请求时间
    public double getAverageRequestTime(long startTime, int count) {
        long time = System.currentTimeMillis() - startTime;

        return (double)time / count;
    }

    //查询城市天气

          //查询城市天气公共操作提取
    public WeatherResponse sonQueryWeather(String url) throws Exception {
        String responseBody = executeGetRequest(url);     //执行GET请求并返回响应体

        if(responseBody==null){
            ERROR_LOGGER.error("请求失败，响应体为空");
            throw new IOException("请求失败，响应体为空");
        }

        return parser.parse(responseBody);
    }
    public WeatherResponse queryWeather(String city) throws Exception {
        String url = buildRequestUrl(city);       //构建请求URL

        return sonQueryWeather(url);
    }

    public WeatherResponse queryWeather(String city, String countryName) throws Exception {
        String url = buildRequestUrl(city, countryName);

        return sonQueryWeather(url);
    }

    //关闭HttpClient
    public void close() throws IOException{
        if(httpClient!=null){
            httpClient.close();
        }
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public WeatherResponseParser getParser() {
        return parser;
    }
}
