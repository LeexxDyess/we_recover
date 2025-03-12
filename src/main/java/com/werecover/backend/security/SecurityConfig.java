package com.werecover.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Public authentication endpoints

                        // 🔹 Step Work Permissions (Only Sponsors)
                        .requestMatchers(HttpMethod.GET, "/api/stepwork/sponsee/**").hasAuthority("ROLE_SPONSOR")
                        .requestMatchers(HttpMethod.POST, "/api/stepwork/**").hasAuthority("ROLE_SPONSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/stepwork/**").hasAuthority("ROLE_SPONSOR")

                        // 🔹 Secure User Profile Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/user-profile/me").authenticated() // Any logged-in user can view their profile
                        .requestMatchers(HttpMethod.PUT, "/api/user-profile/me").authenticated() // Only users can update their own profile
                        .requestMatchers(HttpMethod.GET, "/api/user-profile/me/sobriety-counter").hasAnyAuthority("ROLE_SPONSOR", "ROLE_SPONSEE", "ROLE_ADMIN") // ✅ Only authenticated users

                        // 🔹 Sponsor and Sponsee Permissions
                        .requestMatchers("/sponsors/**").hasAuthority("ROLE_SPONSOR")
                        .requestMatchers("/sponsees/**").hasAuthority("ROLE_SPONSEE")

                        // Achievement Permissions
                        .requestMatchers(HttpMethod.GET, "/api/achievements/me").authenticated() // ✅ Only authenticated users can get their achievements
                        .requestMatchers(HttpMethod.GET, "/api/achievements/all").hasAuthority("ROLE_ADMIN") // ✅ Only admins can get all achievements

                        .requestMatchers(HttpMethod.POST, "/api/check-ins").hasAuthority("ROLE_SPONSEE") // Sponsees can submit check-ins
                        .requestMatchers(HttpMethod.GET, "/api/check-ins/me").hasAuthority("ROLE_SPONSEE") // Sponsees can view their own check-ins
                        .requestMatchers(HttpMethod.GET, "/api/check-ins/sponsees").hasAuthority("ROLE_SPONSOR") // Sponsors can view their sponsees' check-ins
                        .anyRequest().authenticated()



                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("❌ ACCESS DENIED! User: " +
                                    (SecurityContextHolder.getContext().getAuthentication() != null
                                            ? SecurityContextHolder.getContext().getAuthentication().getName()
                                            : "Anonymous"));
                            System.out.println("🔍 USER AUTHORITIES: " +
                                    SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}