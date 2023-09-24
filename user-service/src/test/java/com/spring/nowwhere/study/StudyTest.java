package com.spring.nowwhere.study;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.friend.repository.FriendRepository;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.persistence.EntityManager;

@SpringBootTest
public class StudyTest {
    @Autowired
    EntityManager em;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown(){
        friendRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("test")
    public void test() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    }
}
