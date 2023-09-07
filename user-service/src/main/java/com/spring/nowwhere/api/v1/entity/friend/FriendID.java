package com.spring.nowwhere.api.v1.entity.friend;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendID implements Serializable {
    private Long sender;
    private Long receiver;

    public FriendID(Long sender, Long receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
