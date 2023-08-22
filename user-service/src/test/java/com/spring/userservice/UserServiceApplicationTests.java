package com.spring.userservice;

import com.spring.userservice.v1.api.auth.OAuthUserDto;
import com.spring.userservice.v1.api.entity.User;
import com.spring.userservice.v1.api.entity.UserRepository;
import com.spring.userservice.v1.api.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest

class UserServiceApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("사용자는 이메일을 변경할 수 있다.")
    public void UserServiceApplicationTests() {
        // given
        User user = User.builder()
                .userId("1")
                .email("test")
                .name("admin")
                .password("password")
                .build();
        userRepository.save(user);
        // when
        userService.updateEmail(OAuthUserDto.builder()
                                            .userId("1")
                                            .email("change")
                                            .build());

        User findUser = userRepository.findByEmail("change").get();
        // then
        Assertions.assertEquals(findUser.getEmail(),"change");
    }
}
