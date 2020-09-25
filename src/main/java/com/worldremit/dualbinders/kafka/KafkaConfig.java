package com.worldremit.dualbinders.kafka;


import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class KafkaConfig {

    @Bean
    public Supplier<Message<String>> topologyA() {
        return () -> MessageBuilder
                .withPayload(UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.MESSAGE_KEY, UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> topologyB() {
        return input -> input
                .mapValues((ValueMapper<String, String>) String::toUpperCase);
    }

    @Bean
    public Function<Flux<String>, Flux<String>> topologyR() {
        return input -> input
                .map(s -> s + "XXX");
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> topologyC() {
        return input -> input
                .mapValues((ValueMapper<String, String>) String::toLowerCase);
    }

    @Bean
    public Consumer<Flux<String>> topologyD() {
        return input -> input
                .doOnNext(System.out::println)
                .subscribe();
    }
}
