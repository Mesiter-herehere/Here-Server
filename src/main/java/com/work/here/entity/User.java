package com.work.here.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import com.work.here.entity.enums.Role;
import com.work.here.entity.enums.School;
import lombok.Setter;
import lombok.Getter;

@Entity
@Data
@Getter
@Setter

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

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getPassword() {
        return password;
    }

//    public void setPassword(String password) {
//        this.password = password;
//    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public School getSchool() {
        return school;
    }

//    public void setSchool(School school) {
//        this.school = school;
//    }

    public Role getRole() {
        return role;
    }

//    public void setRole(Role role) {
//        this.role = role;
//    }


}
