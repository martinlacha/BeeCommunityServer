package cz.zcu.kiv.server.beecommunity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application properties
 */

@Configuration
@Data
@ConfigurationProperties(prefix = "beecommunity")
public class PropertiesConfiguration {

    /**
     * Base url of server
     */
    private String baseUrl;

    /**
     * Time in seconds when token is valid from creation time
     */
    private int tokenExpirationSeconds;

    /**
     * Month time for token validity
     */
    private int tokenMonthExpiration;

    /**
     * Secret key for encode tokens
     */
    private String secretKey;

    /**
     * Enable if tokens are validate by expiration date
     */
    private boolean enableTokenExpiration;


}
