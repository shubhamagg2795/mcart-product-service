package com.mcart.productservice.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

    private static final String HOST =
            "search-products-search-demo-gxuege2v7pwykprkpew3kt6rjq.ap-south-1.es.amazonaws.com";

    private static final int PORT = 443;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Demo12345!";

    @Bean
    public RestHighLevelClient openSearchClient() {

        final BasicCredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();

        credentialsProvider.setCredentials(
                new AuthScope(HOST, PORT),
                new UsernamePasswordCredentials(USERNAME, PASSWORD)
        );

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST, PORT, "https"))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        );
    }
}