package com.spring.nowwhere.api.v1.user.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String checkId;

    @Column(unique = true)
    private String remittanceId;
    private String password;

    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserRole> roles = new ArrayList<>();
    @Builder
    private User(String email, String name,
                String checkId, String remittanceId,
                String password, List<UserRole> roles) {

        this.email = email;
        this.name = name;
        this.checkId = checkId;
        this.remittanceId = remittanceId;
        this.password = password;
        this.roles = roles;
    }

    public void updateName(String name){
        this.name = name;
    }
    public void remittanceId(String remittanceId){
        this.remittanceId = remittanceId;
    }
    public void updateEmail(String email){
        this.email = email;
    }
    public boolean isUserIdEmailMatching(){
        return email.equals(checkId) ? true : false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }
}
