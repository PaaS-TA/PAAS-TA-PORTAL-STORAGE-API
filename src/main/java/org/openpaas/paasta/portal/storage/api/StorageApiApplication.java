package org.openpaas.paasta.portal.storage.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PaaS-TA Storage API application. 
 * (org.openpaas.paasta.portal.storage.api)
 *
 * @version 
 * @since 2018. 4. 2.
 *
 */
//@EnableCircuitBreaker
@SpringBootApplication
public class StorageApiApplication {
    /**
     * Storage API entry point.
     * @param args
     */
	public static void main(String[] args) {
		SpringApplication.run(StorageApiApplication.class, args);
	}
}
