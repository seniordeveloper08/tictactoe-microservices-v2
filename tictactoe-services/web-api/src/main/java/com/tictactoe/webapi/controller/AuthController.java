package com.tictactoe.webapi.controller;

import com.tictactoe.authmodule.auth.JwtService;
import com.tictactoe.domain.JwtAuthResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping(path = "auth", produces = {APPLICATION_JSON_UTF8_VALUE})
public class AuthController {

  private final MapReactiveUserDetailsService userDetailsRepository;
  private final JwtService jwtService;

  public AuthController(@Qualifier("userDetailsRepositoryInMemory") MapReactiveUserDetailsService userDetailsRepository, JwtService jwtService) {
    this.userDetailsRepository = userDetailsRepository;
    this.jwtService = jwtService;
  }

  @PostMapping("token")
  @CrossOrigin("*")
  public Mono<ResponseEntity<JwtAuthResponse>> token(Authentication authentication)
      throws AuthenticationException {
    return userDetailsRepository.findByUsername(authentication.getName())
        .map(user -> {
          String token = jwtService.getTokenFromAuthentication(authentication);
          JwtAuthResponse authResponse = new JwtAuthResponse(token, user.getUsername());
          return ResponseEntity.ok(authResponse);
        })
        .defaultIfEmpty(notFound().build());
  }
}
