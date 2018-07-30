package org.openpaas.paasta.portal.storage.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * PaaS-TA Storage API application. 
 * (org.openpaas.paasta.portal.storage.api)
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
//@EnableCircuitBreaker
@SpringBootApplication
@ComponentScan(basePackages = {"org.openpaas.paasta.portal.storage.api"})
public class StorageApiApplication {
    /**
     * Storage API entry point.
     * @param args
     */
	public static void main(String[] args) {
		SpringApplication.run(StorageApiApplication.class, args);
	}
}
