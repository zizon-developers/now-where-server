package com.spring.nowwhere.api.v1.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false)
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserRole> roles = new ArrayList<>();

    @Builder
    private User(Long id, String email, String name, String userId, String password, List<UserRole> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.roles = roles;
    }

    public void updateEmail(String email){
        this.email = email;
    }
    public boolean isUserIdEmailMatching(){
        return email.equals(userId) ? true : false;
    }
}
