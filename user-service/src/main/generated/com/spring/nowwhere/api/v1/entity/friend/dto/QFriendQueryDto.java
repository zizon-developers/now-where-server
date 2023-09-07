package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.spring.nowwhere.api.v1.entity.friend.dto.QFriendQueryDto is a Querydsl Projection type for FriendQueryDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QFriendQueryDto extends ConstructorExpression<FriendQueryDto> {

    private static final long serialVersionUID = -2098776513L;

    public QFriendQueryDto(com.querydsl.core.types.Expression<String> friendName, com.querydsl.core.types.Expression<String> friendProfileImg, com.querydsl.core.types.Expression<com.spring.nowwhere.api.v1.entity.friend.FriendStatus> friendStatus) {
        super(FriendQueryDto.class, new Class<?>[]{String.class, String.class, com.spring.nowwhere.api.v1.entity.friend.FriendStatus.class}, friendName, friendProfileImg, friendStatus);
    }

}

