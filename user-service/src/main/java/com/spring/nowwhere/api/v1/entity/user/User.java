package com.spring.nowwhere.api.v1.entity.user;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity //회원 탈퇴시 따로 친구목록 다 지워야된다. cascade 옵션 사용안하니까
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50, unique = true)
    private String name;
    @Column
    private String profileImg;
    @Column(nullable = false, unique = true)
    private String checkId;
    @Column(unique = true)
    private String remittanceId;
    private String password;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id")
//    private List<User> friends;


    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<UserRole> roles = new ArrayList<>();
    @Builder
    private User(String email, String name, String profileImg,
                String checkId, String remittanceId,
                String password, List<UserRole> roles) {

        this.email = email;
        this.name = name;
        this.profileImg = profileImg;
        this.checkId = checkId;
        this.remittanceId = remittanceId;
        this.password = password;
        this.roles = roles;
    }

    public void updateName(String name){
        this.name = name;
    }
    public void updateRemittanceId(String remittanceId){
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
        return Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(checkId, user.checkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, checkId);
    }
}
