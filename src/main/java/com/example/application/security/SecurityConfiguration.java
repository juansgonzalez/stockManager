package com.example.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@EnableWebSecurity 
@Configuration 
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";
    
    private static UserDetails userDetails;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable()  
    	.requestCache().requestCache(new CustomRequestCache()) 
    	.and().authorizeRequests() 
    	.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()  
    	
    	.anyRequest().authenticated()  
    	
    	.and().formLogin()  
    	.loginPage(LOGIN_URL).permitAll()
    	.loginProcessingUrl(LOGIN_PROCESSING_URL)  
    	.failureUrl(LOGIN_FAILURE_URL)
    	.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL); 
    }
    
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
            User.withUsername("operator")
                .password("{noop}password")
                .roles("OPERATOR")
                .build();
        
        UserDetails admin =
                User.withUsername("admin")
                    .password("{noop}admin")
                    .roles("ADMIN")
                    .build();

        return new InMemoryUserDetailsManager(user,admin);
    }
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
            "/VAADIN/**",
            "/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/images/**",
            "/styles/**",
            "/h2-console/**");
    }
    
	public static boolean isAdmin() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
    	if (principal instanceof UserDetails) {
    		userDetails = ((UserDetails)principal);
    	
	    	for(GrantedAuthority granted : userDetails.getAuthorities()) { 
	    		if (granted.getAuthority().equals("ROLE_ADMIN"))
	    			return true;
	    	}
    	}
    	
    	return false;
	}
}
