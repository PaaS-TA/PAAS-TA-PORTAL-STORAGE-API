package org.openpaas.paasta.portal.storage.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate config
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */

@Configuration
public class DefaultConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
