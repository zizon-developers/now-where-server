package com.spring.nowwhere;

import com.spring.nowwhere.api.v1.auth.dto.OAuthUserDto;
import com.spring.nowwhere.api.v1.user.entity.User;
import com.spring.nowwhere.api.v1.user.repository.UserRepository;
import com.spring.nowwhere.api.v1.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

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
}
