package com.mcart.productservice.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host:localhost}")
    private String host;

    @Value("${opensearch.port:443}")
    private int port;

    @Value("${opensearch.username:admin}")
    private String username;

    @Value("${opensearch.password:admin}")
    private String password;

    @Value("${opensearch.scheme:https}")
    private String scheme;

    // Only creates this bean if opensearch.enabled=true
    // When disabled, search falls back to MongoDB
    @Bean
    @ConditionalOnProperty(name = "opensearch.enabled", havingValue = "true", matchIfMissing = false)
    public RestHighLevelClient openSearchClient() {

        final BasicCredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();

        credentialsProvider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password)
        );

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, scheme))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
    }
}
