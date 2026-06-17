package com.mes.admin.common.entity;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private static final ThreadLocal<String> USERNAME_HOLDER = new ThreadLocal<>();

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() != null
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of(authentication.getName());
        }
        String threadUser = USERNAME_HOLDER.get();
        if (threadUser != null && !threadUser.isEmpty()) {
            return Optional.of(threadUser);
        }
        return Optional.of("system");
    }

    public static void setCurrentAuditor(String username) {
        USERNAME_HOLDER.set(username);
    }

    public static void clearCurrentAuditor() {
        USERNAME_HOLDER.remove();
    }
}
