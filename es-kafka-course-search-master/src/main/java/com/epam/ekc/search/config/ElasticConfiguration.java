package com.epam.ekc.search.config;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfiguration {

    @Value("${elastic.host}")
    private String elasticHost;

    @Value("${elastic.port}")
    private int elasticPort;

//    @Bean(destroyMethod = "close")
//    public RestHighLevelClient client() {
//        return new RestHighLevelClient(RestClient.builder(
//                new HttpHost(elasticHost, elasticPort, "http")));
//    }

    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName("es");
        signer.setRegionName("us-west-2");
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(signer.getServiceName(),
                signer, credentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(elasticHost))
                .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));

    }
}