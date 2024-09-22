package com.work.here.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@AllArgsConstructor
public enum Role implements GrantedAuthority {
    ROLE_STUDENT("STUDENT"),
    ROLE_ADMIN("ADMIN");

    private String role;

    @Override
    public String getAuthority() {
        return role;
    }
}