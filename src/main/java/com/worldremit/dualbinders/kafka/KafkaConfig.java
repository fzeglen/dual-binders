package com.worldremit.dualbinders.kafka;


import com.worldremit.avro.StringValue;
import org.apache.kafka.streams.kstream.KStream;
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
    public Supplier<Message<StringValue>> topologyA() {
        return () -> MessageBuilder
                .withPayload(new StringValue(UUID.randomUUID().toString()))
                .setHeader(KafkaHeaders.MESSAGE_KEY, UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public Function<KStream<String, StringValue>, KStream<String, StringValue>> topologyB() {
        return input -> input
                .mapValues(v -> new StringValue(v.getValue().toUpperCase()));
    }

    @Bean
    public Function<Flux<StringValue>, Flux<StringValue>> topologyR() {
        return input -> input.doOnNext(System.out::println)
                .map(s -> new StringValue(s.getValue() + "XXX"));
    }

    @Bean
    public Function<KStream<String, StringValue>, KStream<String, StringValue>> topologyC() {
        return input -> input
                .mapValues(v -> new StringValue(v.getValue().toLowerCase()));
    }

    @Bean
    public Consumer<Flux<StringValue>> topologyD() {
        return input -> input
                .map(StringValue::getValue)
                .doOnNext(System.out::println)
                .subscribe();
    }
}
