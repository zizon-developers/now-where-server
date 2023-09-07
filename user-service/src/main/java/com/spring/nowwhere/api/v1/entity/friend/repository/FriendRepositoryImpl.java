package com.spring.nowwhere.api.v1.entity.friend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.friend.Friend;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import com.spring.nowwhere.api.v1.entity.friend.QFriend;
import com.spring.nowwhere.api.v1.entity.friend.dto.FriendQueryDto;
import com.spring.nowwhere.api.v1.entity.friend.dto.QFriendQueryDto;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.spring.nowwhere.api.v1.entity.friend.QFriend.*;
import static com.spring.nowwhere.api.v1.entity.user.QUser.*;

@Repository
@Transactional
public class FriendRepositoryImpl implements FriendQueryRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    public FriendRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<Friend> findByFriendWithReverse(User sender, User receiver){
        return queryFactory.select(friend).distinct()
                .from(friend)
                .join(friend.sender).fetchJoin()
                .join(friend.receiver).fetchJoin()
                .where((friend.sender.eq(sender).and(friend.receiver.eq(receiver)))
                        .or(friend.sender.eq(receiver).and(friend.receiver.eq(sender))))
                .fetch();
    }

    @Override
    public Optional<Friend> findBySenderAndReceiver(User sender, User receiver) {
        return Optional.ofNullable(
                queryFactory.selectFrom(QFriend.friend)
                            .join(QFriend.friend.sender).fetchJoin()
                            .join(QFriend.friend.receiver).fetchJoin()
                            .where(QFriend.friend.sender.eq(sender).and(QFriend.friend.receiver.eq(receiver)))
                            .fetchOne()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FriendQueryDto> findBySenderAndFriendStatus(User sender, FriendStatus friendStatus, Pageable pageable) {
        List<FriendQueryDto> content = queryFactory.select(
                    new QFriendQueryDto(friend.receiver.name, friend.receiver.profileImg, friend.friendStatus))
                .from(friend)
                .leftJoin(friend.sender, user)
                .leftJoin(friend.receiver, user)
                .where(friend.sender.eq(sender).and(friend.friendStatus.eq(friendStatus)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = getCountBySenderAndFriendStatus(sender, friendStatus);

        return new PageImpl<>(content, pageable, count);
    }

    private Long getCountBySenderAndFriendStatus(User sender, FriendStatus friendStatus) {
        return queryFactory.select(friend.count())
                .from(friend)
                .leftJoin(friend.sender, user)
                .leftJoin(friend.receiver, user)
                .where(friend.sender.eq(sender).and(friend.friendStatus.eq(friendStatus)))
                .fetchOne();
    }
}
