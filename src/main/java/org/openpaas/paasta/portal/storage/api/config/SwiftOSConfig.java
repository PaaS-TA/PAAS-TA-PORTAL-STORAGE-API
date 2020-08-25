package org.openpaas.paasta.portal.storage.api.config;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSEnvironmentKeys;
import org.openpaas.paasta.portal.storage.api.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Swift Object Storage config class.<br>
 * Create configuration bean for Swift Object Storage.
 * </p>
 * @author hgcho (Hyungu Cho)
 * @version 
 * @since 2018. 4. 2.
 *
 */
@Configuration
@RestController
public class SwiftOSConfig extends ObjectStorageConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger( SwiftOSConfig.class );
    
    /**
     * <p>Create AccountConfig instance for Swift Object Storage. <br>
     * Configurations related to Swift read from spring boot config file.</p>
     * @return {@link org.javaswift.joss.client.factory.AccountConfig AccountConfig}
     *
     */
    @Bean
    public AccountConfig accountConfig(){
        final String tenantName = env.getRequiredProperty(SwiftOSEnvironmentKeys.OBJECT_STORAGE_TENANT_NAME);
        final String username = env.getRequiredProperty(SwiftOSEnvironmentKeys.OBJECT_STORAGE_USER_NAME);
        final String password = env.getRequiredProperty(SwiftOSEnvironmentKeys.OBJECT_STORAGE_PASSWORD);
        final String authUrl = env.getRequiredProperty(SwiftOSEnvironmentKeys.OBJECT_STORAGE_AUTH_URL);
        final String authMethod = env.getRequiredProperty( SwiftOSEnvironmentKeys.OBJECT_STORAGE_AUTH_METHOD );
        final String preferredRegion = env.getRequiredProperty( SwiftOSEnvironmentKeys.OBJECT_STORAGE_PREFERRED_REGION );

        AccountConfig accountConfig = new AccountConfig();
//        accountConfig.setTenantName(tenantName);
        accountConfig.setUsername(username);
        accountConfig.setPassword(password);
        accountConfig.setAuthUrl(authUrl);
        accountConfig.setAuthenticationMethod(getAuthenticationMethod( authMethod ));
//        accountConfig.setPreferredRegion(preferredRegion);

        LOGGER.info( "Create account config for object storage : {}", accountConfig.getUsername() );
        LOGGER.info( "Create account config for object storage : {}", accountConfig.getPassword() );
        LOGGER.info( "Create account config for object storage : {}", accountConfig.getAuthUrl() );
        LOGGER.info( "Create account config for object storage : {}", accountConfig.getAuthenticationMethod() );

        return accountConfig;
    }
    
    private AuthenticationMethod getAuthenticationMethod(final String authMethodString) {
        AuthenticationMethod matchMethod = null;
        
        for (AuthenticationMethod method : AuthenticationMethod.values()) {
            if (method.name().toLowerCase().equals( authMethodString.toLowerCase() ) )
                matchMethod = method;
        }
        
        if (null == matchMethod)
            throw new IllegalArgumentException( "Selecting authentication method of object storage is invalid. Current is " + authMethodString );
        
        return matchMethod;
    }
    
    /**
     * Print detail information of account config
     * @param config
     * @return
     */
    private String getAccountConfigDetail(AccountConfig config) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append( "Show contents of account config..." ).append( '\n' );
        if (null != config) {
            buffer.append( "- Tenant name : " ).append( config.getTenantName() )
            .append( "- User : " ).append( config.getUsername() )
            .append( "- Password : " ).append( config.getPassword())
            .append( "- Authentication URL : " ).append( config.getAuthUrl() )
            .append( "- Authentication Method : " ).append( config.getAuthenticationMethod() )
            .append( "- Preferred Region : ").append( config.getPreferredRegion() );
        } else {
            buffer.append( "- AccountConfig is null..." );
        }
        buffer.append( '\n' );
        
        return buffer.toString();
    }
    
    @GetMapping( "/swift/config" )
    public String getMappingAccountConfig() {
        // debug only
        final AccountConfig accountConfig = BeanUtils.getBean( "accountConfig" );
        return getAccountConfigDetail( accountConfig );
    }

    /**
     * Create AccountFactory instance for Swift Object Storage
     * @param accountConfig
     * @return {@link org.javaswift.joss.client.factory.AccountFactory AccountFactory}
     */
    @Bean
    public AccountFactory accountFactory(AccountConfig accountConfig){
        final AccountFactory factory = new AccountFactory(accountConfig);
        LOGGER.debug( "Create account factory for object storage : {}", factory );
        
        return factory;
    }

    /**
     * Create account instance for Swift Object Storage
     * @param accountFactory
     * @return {@link org.javaswift.joss.model.Account Account}
     */
    @Bean
    public Account account(AccountFactory accountFactory){
        try {
            final Account account = accountFactory.createAccount();
            LOGGER.debug( "Create account for object storage : {}", account );
            
            return account;
        } catch (Throwable t) {
            final AccountConfig accountConfig = BeanUtils.getBean( "accountConfig" );
            LOGGER.error( "Account config setting is invalid." );
            LOGGER.error( getAccountConfigDetail(accountConfig) );
            
            throw t;
        }
    }

    /**
     * Create container instance for Swift Object Storage
     * @param account
     * @return {@link org.javaswift.joss.model.Container Container}
     */
    @Bean
    public Container container(Account account){
        final String containerName = env.getRequiredProperty(SwiftOSEnvironmentKeys.OBJECT_STORAGE_CONTAINER);
        final Container container = account.getContainer(containerName);
        if(!container.exists()){
            container.create();
            // TODO If administrator wants to make private container...?
            container.makePublic();
            LOGGER.debug( "Container {} is created in Object Storage.", containerName );
        } else {
            LOGGER.debug( "Container {} exists already in Object Storage.", containerName );
        }
        
        return container;
    }
}
