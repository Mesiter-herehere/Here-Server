package com.work.here.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.entity.enums.School;

@Entity
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    private School school;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 기본 생성자 추가
    public User() {
    }

    @Builder
    public User(Long id, String email, String name, School school, String password, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.school = school;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name()); // 권한을 반환합니다. 필요에 따라 수정 가능
    }

    @Override
    public String getUsername() {
        return email; // 사용자 이름은 이메일로 설정합니다.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true: 잠금되지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true: 활성화됨)
    }
}