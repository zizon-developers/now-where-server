package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class FriendRepositoryImpl implements FriendQueryRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    public FriendRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Page<Friend> findBySenderAndFriendStatus(User sender, FriendStatus friendStatus, Pageable pageable) {
        queryFactory.select();

        return null;
    }
}
