package com.beshton.shopping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 返回当前用户的用户名，示例中返回固定的用户名
        return Optional.of("system");
    }
}
