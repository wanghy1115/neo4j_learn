package com.example.neo4jlearn.utils;



import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
/**
 * @ClassName: ExampleCommandLineRunner
 * @Description: 来装配Driver和Session
 * @Author: wanghuaiyu
 * @Date: 2023/11/20
 */
@Component
@Slf4j
public class ExampleCommandLineRunner implements CommandLineRunner {

    private final Driver driver;
    private final ConfigurableApplicationContext applicationContext;
    public final Session session;

    @Bean
    Session session(){
        return session;
    }

    // Autowire the Driver bean by constructor injection
    public ExampleCommandLineRunner(Driver driver, ConfigurableApplicationContext applicationContext) {
        this.driver = driver;
        this.applicationContext = applicationContext;
        this.session = driver.session();

    }

    @Override
    public void run(String... args) throws Exception {
    }
}

