package org.openpaas.paasta.portal.storage.api.config.security;

import org.openpaas.paasta.portal.storage.api.config.SwiftOSConstants.SwiftOSControllerURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The type Security config.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger( SecurityConfig.class );

    @Value("${spring.security.username}")
    String username;

    @Value("${spring.security.password}")
    String password;

    /**
     * Configure global.
     *
     * @throws Exception the exception
     */

    private PasswordEncoder passwordEncoder = null;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        if (null == passwordEncoder) {
            passwordEncoder = new BCryptPasswordEncoder();
        }
        
        return passwordEncoder;
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        LOGGER.info( "User : {} / Password : {}", username, password );
        auth.inMemoryAuthentication()
            /* .passwordEncoder( passwordEncoder() ) */
            .withUser(username).password(password).roles("USER");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/*").permitAll()
            .antMatchers( SwiftOSControllerURI.OBJECT_STORAGE_HELLO_SERVICE ).permitAll()
            .antMatchers( 
                SwiftOSControllerURI.OBJECT_STORAGE_ROOT_URI + "/**").hasRole("USER")
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable().cors().disable();
    }

}
