package com.spring.nowwhere.api.v1.entity.bet;

import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class BetTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @AfterEach
    void tearDown(){
    }

    @Test
    @DisplayName("내기 상태가 완료상태인 경우 내기의 결과를 업데이트할 수 있다. ")
    public void updateBetResult() {
        // given

        // when

        // then
    }

}