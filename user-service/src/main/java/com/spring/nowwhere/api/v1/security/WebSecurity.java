package com.spring.nowwhere.api.v1.security;

import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import com.spring.nowwhere.api.v1.redis.logout.LogoutAccessTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CorsConfig corsConfig;
    private final Environment env;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable() //h2-console 화면 깨짐
                .and()
                .apply(new MyCustomDsl());


        http.authorizeRequests(authorize -> authorize
                        .antMatchers("api/v1/auth/login", "api/v1//auth/join").permitAll()
                        .antMatchers("api/v1/auth/**").access("hasRole('ROLE_USER')")
                        .antMatchers("api/v1/user/**").access("hasRole('ROLE_USER')")
                        .anyRequest().authenticated())
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint());

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(getAuthenticationFiler())
                    .addFilter(getAuthorizationFilter());
        }
    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter getAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManager(), userRepository, tokenProvider(), logoutAccessTokenRedisRepository);
    }

    @Bean
    public JwtAuthenticationFilter getAuthenticationFiler() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(), env, tokenProvider());
        return jwtAuthenticationFilter;
    }


    //password를 encoding해줌 successfulAuthentication에서 breakPoint 찍어보면 암호화됨
    @Bean
    AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
        return builder.userDetailsService(new PrincipalDetailsService(userRepository))
                      .passwordEncoder(passwordEncoder).and().build();
    }

    @Bean
    TokenProvider tokenProvider(){
        return new TokenProvider(env);
    }

    @Bean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(){
        return new JwtAuthenticationEntryPoint();
    }
}