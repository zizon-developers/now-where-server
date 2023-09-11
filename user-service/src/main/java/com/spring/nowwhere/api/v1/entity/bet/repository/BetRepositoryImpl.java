package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.user.QUser;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.spring.nowwhere.api.v1.entity.bet.QBet.*;

@Repository
public class BetRepositoryImpl implements BetQueryRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BetRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Bet> findBetsInTimeRange(User user, LocalDateTime startTime, LocalDateTime endTime) {
        return Optional.ofNullable(
                queryFactory.selectFrom(bet)
                    .join(bet.bettor).fetchJoin()
                    .join(bet.receiver).fetchJoin()
                    .where(inTimeRange(startTime, endTime).and(bettorAndReceiverEq(user)))
                    .fetchFirst());
    }

    private BooleanBuilder bettorAndReceiverEq(User user) {
        return new BooleanBuilder(bet.bettor.eq(user).or(bet.receiver.eq(user)));
    }
    private BooleanBuilder inTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return new BooleanBuilder(bet.betInfo.startTime.loe(endTime)
                                        .and(bet.betInfo.endTime.goe(startTime)));
    }
    private BooleanBuilder userEq(User user){
        return new BooleanBuilder(QUser.user.eq(user));
    }
}
