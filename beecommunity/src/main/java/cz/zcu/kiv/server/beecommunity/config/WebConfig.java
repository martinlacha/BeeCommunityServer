package cz.zcu.kiv.server.beecommunity.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of class for beans in whole project
 */

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Bean of password encoder
     * @return BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
    }

    /**
     * Bean of model mapper
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return modelMapper;
    }

    /**
     * Enable Cross origin requests for all controlers
     * More info on <a href="https://www.baeldung.com/spring-cors"/>
     * @param registry - registry with mappings for cross-origin requests
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

}
