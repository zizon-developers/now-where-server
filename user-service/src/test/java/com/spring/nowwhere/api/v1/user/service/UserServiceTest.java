package com.spring.nowwhere.api.v1.user.service;

import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자의 이메일과 userId가 같은경우 email을 변경할 수 있다.")
    public void updateEmail() {
        // given
        User user = User.builder()
                .userId("same")
                .email("same")
                .name("admin")
                .password("password")
                .build();
        userRepository.save(user);
        // when
        userService.updateEmail(OAuthUserDto.builder()
                .userId("same")
                .email("change")
                .build());

        Optional<User> findUser = userRepository.findByEmail("change");
        // then
        Assertions.assertEquals(findUser.get().getEmail(),"change");
    }

    @Test
    @DisplayName("유저 정보가 DB에 없을 경우 DB에 저장된다.")
    public void checkAndRegisterUser() {
        // when
        OAuthUserDto oAuthUserDto = userService.checkAndRegisterUser(OAuthUserDto.builder()
                .userId("testId")
                .email("test@naver.com")
                .name("test").build());

        User user = userRepository.findByUserId(oAuthUserDto.getUserId()).get();
        // then
        assertThat(oAuthUserDto.getUserId()).isEqualTo(user.getUserId());
        assertThat(oAuthUserDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(oAuthUserDto.getName()).isEqualTo(user.getName());
    }
}