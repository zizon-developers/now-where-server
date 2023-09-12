package com.spring.nowwhere.api.v1.entity.bet.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.spring.nowwhere.api.v1.entity.bet.dto.QUserInfoDto is a Querydsl Projection type for UserInfoDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QUserInfoDto extends ConstructorExpression<UserInfoDto> {

    private static final long serialVersionUID = 1563317093L;

    public QUserInfoDto(com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> profileImg) {
        super(UserInfoDto.class, new Class<?>[]{String.class, String.class, String.class}, email, name, profileImg);
    }

}

