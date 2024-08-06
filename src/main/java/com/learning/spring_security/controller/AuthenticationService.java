package com.learning.spring_security.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.spring_security.config.JwtService;
import com.learning.spring_security.entity.Role;
import com.learning.spring_security.entity.User;
import com.learning.spring_security.repository.UserRepository;

import dto.AuthenticationRequest;
import dto.AuthenticationResponse;
import dto.ReqisterRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(ReqisterRequest request) {
		var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
				.email(request.getEmail()).pass(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();

		userRepository.save(user);
		var jwtToken = jwtService.generateJwtToken(user);

		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return AuthenticationResponse.builder().token("Success").build();

	}

	public AuthenticationResponse authenticateUsingJwt(AuthenticationRequest request) {

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

		var jwtToken = jwtService.generateJwtToken(user);
		var body = AuthenticationResponse.builder().token(jwtToken).build();
		System.out.println("User = " + body.toString());
		return body;
	}

}
