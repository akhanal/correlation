# correlation

Example of setting up correlationId. 

Run all three apps and make a call to the first app
http://localhost:9091/service1

This triggers a series for calls from service1 to service2 to service3.

Check the logs of all three services.


* App1 Log: ```23:32:16.669 service:service1 client: correlation:ed541e7d-d764-4553-9b14-9f4caf07c094  request [uri=/service1]```
* App2 Log: ```23:32:16.856 service:service2 client:service1 correlation:ed541e7d-d764-4553-9b14-9f4caf07c094  request [uri=/service2]```
* App3 Log: ```23:32:17.121 service:service3 client:service2 correlation:ed541e7d-d764-4553-9b14-9f4caf07c094  request [uri=/service3]```



Setup is same in all three apps. There are three apps only to show the cascaded calls and flow of correlationId.


utilize MDC context attributes in log config:```service:%X{serviceId} client:%X{clientId} correlation:%X{correlationId}```

```
@SpringBootApplication
public class App1Application {
	public static void main(String[] args) {
		SpringApplication.run(App1Application.class, args);
	}
}
```

```
@Configuration
public class CorrelationIdConfig {

    Logger logger = LoggerFactory.getLogger(CorrelationIdConfig.class);

    @Value("${serviceId}")
    private String serviceId;

    @Bean
    public ClientHttpRequestInterceptor interceptor() {
        return (httpRequest, bytes, clientHttpRequestExecution) -> {
            HttpHeaders headers = httpRequest.getHeaders();
            headers.add("clientId", serviceId);
            String correlationId = MDC.get("correlationId");
            if(StringUtils.isEmpty(correlationId)){
                throw new RuntimeException("correlationId missing.");
            }
            headers.add("correlationId",correlationId);
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(Collections.singletonList(interceptor()));
        return template;
    }

    @Bean
    public Filter correlationIdFilter () {
        return new AbstractRequestLoggingFilter() {
            @Override protected void beforeRequest(HttpServletRequest request, String message) {
                String clientId = (String) request.getHeader("clientId");
                String correlationId = (String) request.getHeader("correlationId");
                if(StringUtils.isEmpty(correlationId)){
                    correlationId = UUID.randomUUID().toString();
                }
                MDC.put("clientId", clientId);
                MDC.put("correlationId", correlationId);
                MDC.put("serviceId", serviceId);
                logger.info(message);
            }

            @Override protected void afterRequest(HttpServletRequest request, String message) {
                logger.info(message);
                MDC.clear();
            }
        };
    }
}
```
```
@RestController
public class Service1 {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/service1")
    public Map<String,String> get1() {
        Map<String, String> myResult1 = new LinkedHashMap<>();
        myResult1.put("result1", "1");
        Map<String,String> result = restTemplate.getForObject("http://localhost:9092/service2", Map.class);
        myResult1.putAll(result);
        return myResult1;
    }
}
```
