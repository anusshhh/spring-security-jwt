package com.learning.spring_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.learning.spring_security.entity.Role;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return basicSecurityFilterChain(httpSecurity);
	}

	// *** BASIC AUTHENTICATION ***

	public SecurityFilterChain basicSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(it -> it.requestMatchers("/api/v1/auth/**")
				.permitAll().requestMatchers("/h2-console/**").permitAll().anyRequest().authenticated());
		return httpSecurity.build();

	}

	// *** JWT AUTHENTICATION ***

	public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).headers(it -> it.frameOptions(it2 -> it2.disable()))
				.authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers("/h2-console/**").permitAll().anyRequest().authenticated())
				.sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
