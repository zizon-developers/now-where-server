package com.spring.nowwhere.api.v1.entity.bet.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.nowwhere.api.v1.entity.bet.Bet;
import com.spring.nowwhere.api.v1.entity.bet.BetDateTime;
import com.spring.nowwhere.api.v1.entity.bet.BetResult;
import com.spring.nowwhere.api.v1.entity.bet.dto.BetSummaryDto;
import com.spring.nowwhere.api.v1.entity.bet.dto.QBetSummaryDto;
import com.spring.nowwhere.api.v1.entity.bet.dto.QUserInfoDto;
import com.spring.nowwhere.api.v1.entity.user.QUser;
import com.spring.nowwhere.api.v1.entity.user.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.spring.nowwhere.api.v1.entity.bet.QBet.*;

@Repository
@Transactional
public class BetRepositoryImpl implements BetQueryRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BetRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Bet> findBetInTimeRange(User bettor, User receiver, BetDateTime betDateTime) {
        return Optional.ofNullable(
                queryFactory.selectFrom(bet)
                    .join(bet.bettor).fetchJoin()
                    .join(bet.receiver).fetchJoin()
                    .where(inTimeRange(betDateTime.getStartTime(), betDateTime.getEndTime())
                            .and(bettorAndReceiverEq(bettor).or(bettorAndReceiverEq(receiver))))
                    .fetchFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public BetSummaryDto getUserBettingSummary(User user) {
        Expression<Integer> totalBetAmount = getTotalBetAmount(user);
        BetSummaryDto betSummaryDto = queryFactory.select(
                        new QBetSummaryDto(new QUserInfoDto(
                                bet.bettor.email.coalesce(bet.receiver.email),
                                bet.bettor.name.coalesce(bet.receiver.name),
                                bet.bettor.profileImg.coalesce(bet.receiver.profileImg))
                                , bet.count().intValue().as("totalBetCount")
                                ,totalBetAmount))
                .from(bet)
                .leftJoin(bet.bettor, QUser.user).on(bet.bettor.eq(user))
                .leftJoin(bet.receiver, QUser.user).on(bet.receiver.eq(user))
                .where(resultIsNotNullAndBettorEq(user).or(resultIsNotNullAndReceiverEq(user)))
                .fetchOne();
        return betSummaryDto;
    }

    @Override
    public List<Bet> findBetsByStartTime(LocalDateTime startTime) {
        return queryFactory.selectFrom(bet)
                        .join(bet.bettor).fetchJoin()
                        .join(bet.receiver).fetchJoin()
                        .where(startTimeEq(startTime))
                        .fetch();
    }

    @Override
    public List<Bet> findBetsByEndTime(LocalDateTime endTime) {
        return queryFactory.selectFrom(bet)
                .join(bet.bettor).fetchJoin()
                .join(bet.receiver).fetchJoin()
                .where(endTimeEq(endTime))
                .fetch();
    }

    private Expression<Integer> getTotalBetAmount(User user) {
        return ExpressionUtils.as(
                JPAExpressions.select(bet.betInfo.amount.sum())
                        .from(bet)
                        .leftJoin(bet.bettor, QUser.user)
                        .leftJoin(bet.receiver, QUser.user)
                        .where(bettorAndResultWinEq(user).or(receiverAndResultWinEq(user))),
                "totalBetAmount");
    }
    private BooleanBuilder bettorAndResultWinEq(User user){
        return new BooleanBuilder(bet.bettor.eq(user)
                                    .and(bet.betResult.eq(BetResult.BETTOR_WIN)));
    }
    private BooleanBuilder receiverAndResultWinEq(User user) {
        return new BooleanBuilder(bet.receiver.eq(user)
                                    .and(bet.betResult.eq(BetResult.RECEIVER_WIN)));
    }
    private BooleanBuilder resultIsNotNullAndBettorEq(User user) {
        return new BooleanBuilder(bet.bettor.eq(user).and(bet.betResult.isNotNull()));
    }
    private BooleanBuilder resultIsNotNullAndReceiverEq(User user) {
        return new BooleanBuilder(bet.receiver.eq(user).and(bet.betResult.isNotNull()));
    }
    private BooleanBuilder inTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return new BooleanBuilder(bet.betInfo.betDateTime.startTime.loe(endTime)
                                        .and(bet.betInfo.betDateTime.endTime.goe(startTime)));
    }
    private BooleanBuilder startTimeEq(LocalDateTime startTime) {
        return new BooleanBuilder(bet.betInfo.betDateTime.startTime.eq(startTime));
    }
    private BooleanBuilder endTimeEq(LocalDateTime endTime) {
        return new BooleanBuilder(bet.betInfo.betDateTime.endTime.eq(endTime));
    }
    private BooleanBuilder bettorAndReceiverEq(User user) {
        return new BooleanBuilder(bet.bettor.eq(user).or(bet.receiver.eq(user)));
    }
}
