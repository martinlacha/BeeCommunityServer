package cz.zcu.kiv.server.beecommunity.config;

import cz.zcu.kiv.server.beecommunity.enums.UserEnums;
import cz.zcu.kiv.server.beecommunity.filters.JwtAuthenticationFilter;
import cz.zcu.kiv.server.beecommunity.handlers.ApiAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static cz.zcu.kiv.server.beecommunity.enums.UserEnums.ERoles.ADMIN;
import static cz.zcu.kiv.server.beecommunity.enums.UserEnums.ERoles.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {

    private final AuthenticationFailureHandler authenticationUserFailureHandler;

    private final AuthenticationSuccessHandler authenticationUserSuccessHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ApiAccessDeniedHandler apiAccessDeniedHandler;

    /**
     * Security filter chain to check every http request
     * First
     * More info about CSRF on <a href="https://www.baeldung.com/spring-security-csrf"/>
     * @param http Http request received to filter
     * @return Filtered http request
     * @throws Exception exception while request process
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // These paths have not to be authenticated for access and there are permit to all
            .authorizeHttpRequests(request -> request
                .requestMatchers(
                        "/api-docs",
                        "/api/v1/server/test-connection",
                        "/api/v1/user/sign-up",
                        "/api/v1/user/all",
                        "/api/v1/user/update-password",
                        "/api/v1/user/reset-password"
                )
                .permitAll()
                .requestMatchers("/api/v1/user/info").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/api/v1/friends/*").hasAnyAuthority("USER")
                .requestMatchers("/api/v1/community-post/*").hasAnyAuthority("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/user/roles-info").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user/admin").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/user/admin").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/detail").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/news", "/api/v1/news/detail").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/news", "/api/v1/news/detail").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/news", "/api/v1/news/detail").hasAuthority("ADMIN")
                // any other url paths must be authenticated
                .anyRequest()
                .authenticated()
            )
            .formLogin(form -> form
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/v1/user/login")
                .successHandler(authenticationUserSuccessHandler)
                .failureHandler(authenticationUserFailureHandler)
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(
                    httpSecuritySessionManagementConfigurer ->
                            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(configurer -> configurer.accessDeniedHandler(apiAccessDeniedHandler))
            .logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    /**
     * Bean for Authentication manager
     * @param userDetailsService service for user details
     * @param passwordEncoder password encoder
     * @return instance of authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}