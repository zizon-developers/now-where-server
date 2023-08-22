package com.spring.nowwhere;

import com.spring.nowwhere.api.v1.auth.OAuthUserDto;
import com.spring.nowwhere.api.v1.entity.User;
import com.spring.nowwhere.api.v1.entity.UserRepository;
import com.spring.nowwhere.api.v1.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
