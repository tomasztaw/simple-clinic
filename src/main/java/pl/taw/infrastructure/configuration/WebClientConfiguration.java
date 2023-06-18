package pl.taw.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfiguration {

//    @Value("${api.cepik.url}")
//    private String cepikUrl;
//
//    @Bean
//    public WebClient webClient(ObjectMapper objectMapper) {
//        final var strategies = ExchangeStrategies
//            .builder()
//            .codecs(configurer -> {
//                configurer
//                    .defaultCodecs()
//                    .jackson2JsonEncoder(
//                        new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
//                configurer
//                    .defaultCodecs()
//                    .jackson2JsonDecoder(
//                        new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
//            }).build();
//        return WebClient.builder()
//            .exchangeStrategies(strategies)
//            .build();
//    }
//
//    @Bean
//    public ApiClient apiClient(final WebClient webClient) {
//        ApiClient apiClient = new ApiClient(webClient);
//        apiClient.setBasePath(cepikUrl);
//        return apiClient;
//    }
//
//    @Bean
//    public PojazdyApi pojazdyApi(final ApiClient apiClient) {
//        return new PojazdyApi(apiClient);
//    }
//
//    @Bean
//    public SownikiApi sownikiApi(final ApiClient apiClient) {
//        return new SownikiApi(apiClient);
//    }
}
