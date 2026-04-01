package project.booteco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "gemini.api")
public record GeminiConfigurationProperties(String key, String url) {
}
