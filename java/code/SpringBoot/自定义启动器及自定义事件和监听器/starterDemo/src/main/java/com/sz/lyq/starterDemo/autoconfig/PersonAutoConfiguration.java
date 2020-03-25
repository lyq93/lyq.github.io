package com.sz.lyq.starterDemo.autoconfig;

import com.sz.lyq.starterDemo.domian.Person;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "person",name = "enabled", havingValue = "true", matchIfMissing = true)
public class PersonAutoConfiguration {
    @ConfigurationProperties(value = "person")
    @Bean
    public Person person(){
        return new Person();
    }
}
