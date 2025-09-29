package com.ragchat.config.auth;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Component
@ConfigurationProperties(prefix = "security.api")
public class ApiKeyProperties {

    private String keys;

    public Set<String> getKeySet() {
        if (keys == null || keys.isBlank()) return Set.of();
        return Arrays.stream(keys.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
    public String getKeys() { return keys; }
    public void setKeys(String keys) { this.keys = keys; }
}
