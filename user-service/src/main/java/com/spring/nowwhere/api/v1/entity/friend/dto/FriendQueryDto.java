package com.spring.nowwhere.api.v1.entity.friend.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.spring.nowwhere.api.v1.entity.friend.FriendStatus;
import lombok.Getter;

@Getter
public class FriendQueryDto {
    private String friendName;
    private String friendProfileImg;
    private FriendStatus friendStatus;

    @QueryProjection
    public FriendQueryDto(String friendName, String friendProfileImg, FriendStatus friendStatus) {
        this.friendName = friendName;
        this.friendProfileImg = friendProfileImg;
        this.friendStatus = friendStatus;
    }
}
