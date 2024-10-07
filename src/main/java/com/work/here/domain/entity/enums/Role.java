package com.work.here.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@AllArgsConstructor
@Getter // Getter를 추가하여 role에 접근할 수 있도록
public enum Role implements GrantedAuthority {
    ROLE_STUDENT("STUDENT"),
    ROLE_ADMIN("ADMIN");

    private String role;

    @Override
    public String getAuthority() {
        return role;
    }
}
