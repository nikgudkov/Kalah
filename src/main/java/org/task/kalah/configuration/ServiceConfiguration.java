package org.task.kalah.configuration;

import org.task.kalah.service.GameIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class ServiceConfiguration {

    @Bean
    public GameIdGenerator gameIdGenerator() {
        return new GameIdGenerator(new AtomicLong(0L));
    }

}
