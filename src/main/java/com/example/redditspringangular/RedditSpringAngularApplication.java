package com.example.redditspringangular;

import com.example.redditspringangular.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfiguration.class)
public class RedditSpringAngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedditSpringAngularApplication.class, args);
    }

}
