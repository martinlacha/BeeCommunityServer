package cz.zcu.kiv.server.beecommunity.config;

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

/**
 * Security configuration
 */

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
                        "/api/v1/user/update-password",
                        "/api/v1/user/reset-password",
                        "/api/v1/hive/sensors"
                )
                .permitAll()
                // Paths which has to be authenticated and which roles user must have
                .requestMatchers("/api/v1/user/info").hasAnyAuthority(USER.name(), ADMIN.name())
                .requestMatchers("/api/v1/user/friend-info").hasAnyAuthority(USER.name(), ADMIN.name())
                .requestMatchers("/api/v1/friends/*").hasAnyAuthority(USER.name())
                .requestMatchers("/api/v1/community-post/*").hasAnyAuthority(USER.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/user/roles-info").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/user/admin").hasAuthority(USER.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user/admin").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/user/admin").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/detail").hasAnyAuthority(USER.name(), ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/news", "/api/v1/news/detail").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/news", "/api/v1/news/detail").hasAuthority(ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/news", "/api/v1/news/detail").hasAuthority(ADMIN.name())
                .requestMatchers("/api/v1/apiary/*").hasAuthority(USER.name())
                .requestMatchers("/api/v1/event").hasAuthority(USER.name())
                .requestMatchers("/api/v1/hive/*").hasAuthority(USER.name())
                .requestMatchers("/api/v1/queen/*").hasAuthority(USER.name())
                .requestMatchers("/api/v1/inspection/*").hasAuthority(USER.name())
                .requestMatchers("/api/v1/stats/*").hasAuthority(USER.name())
                // Any other url paths must be authenticated
                .anyRequest()
                .authenticated()
            )
            // Login form
            .formLogin(form -> form
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/v1/user/login")
                .successHandler(authenticationUserSuccessHandler)
                .failureHandler(authenticationUserFailureHandler)
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            // Stateless session policy
            .sessionManagement(
                    httpSecuritySessionManagementConfigurer ->
                            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Filter incoming request to valid and check JWT token
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            // Set up exception handler
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