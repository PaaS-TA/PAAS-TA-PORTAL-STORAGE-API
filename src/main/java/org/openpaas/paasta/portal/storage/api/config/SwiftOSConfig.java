package org.openpaas.paasta.portal.storage.api.config;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants;
import org.openpaas.paasta.portal.storage.api.common.SwiftOSConstants.EnvironmentKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Swift Object Storage config
 * 
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
@Configuration
public class SwiftOSConfig extends ObjectStorageConfig {
    @Bean
    public AccountConfig accountConfig(){
        final String tenantName = env.getRequiredProperty(EnvironmentKeys.OBJECT_STORAGE_TENANT_NAME);
        final String username = env.getRequiredProperty(EnvironmentKeys.OBJECT_STORAGE_USER_NAME);
        final String password = env.getRequiredProperty(EnvironmentKeys.OBJECT_STORAGE_PASSWORD);
        final String authUrl = env.getRequiredProperty(EnvironmentKeys.OBJECT_STORAGE_AUTH_URL);

        final AccountConfig config = new AccountConfig();
        config.setTenantName(tenantName);
        config.setUsername(username);
        config.setPassword(password);
        config.setAuthUrl(authUrl + "/tokens");
        config.setAuthenticationMethod(AuthenticationMethod.KEYSTONE);
        config.setPreferredRegion("Public");

        return config;
    }

    @Bean
    public AccountFactory accountFactory(AccountConfig accountConfig){
        return new AccountFactory(accountConfig);
    }

    @Bean
    public Account account(AccountFactory accountFactory){
        return accountFactory.createAccount();
    }

    @Bean
    public Container container(Account account){
        final String containerName = env.getRequiredProperty(EnvironmentKeys.OBJECT_STORAGE_CONTAINER);

        final Container container = account.getContainer(containerName);
        if(!container.exists()){
            container.create();
            container.makePublic();
        }
        return container;
    }
}