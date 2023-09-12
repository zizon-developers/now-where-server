package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.spring.nowwhere.api.v1.entity.bet.dto.QBetSummaryDto is a Querydsl Projection type for BetSummaryDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QBetSummaryDto extends ConstructorExpression<BetSummaryDto> {

    private static final long serialVersionUID = 1087104873L;

    public QBetSummaryDto(com.querydsl.core.types.Expression<? extends UserInfoDto> userInfoDto, com.querydsl.core.types.Expression<Integer> totalBetCount, com.querydsl.core.types.Expression<Integer> totalBetAmount) {
        super(BetSummaryDto.class, new Class<?>[]{UserInfoDto.class, int.class, int.class}, userInfoDto, totalBetCount, totalBetAmount);
    }

}

