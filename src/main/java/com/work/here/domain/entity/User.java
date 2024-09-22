package com.work.here.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.entity.enums.School;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length =10 )
    private String name;

    @Enumerated(EnumType.STRING)
    private School school;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    protected User() {
    }

    @Builder
    public User(Long id,String email, String name, School school, String password, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.school = school;
        this.password = password;
        this.role = role;
    }



}
