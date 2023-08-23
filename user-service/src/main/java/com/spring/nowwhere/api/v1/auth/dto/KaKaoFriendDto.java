package com.spring.nowwhere.api.v1.auth.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KaKaoFriendDto {
    private int offset;
    private Optional<Integer> limit;
    private String order;
    private String friend_order;

    @Builder
    private KaKaoFriendDto(int offset, int limit, String order, String friend_order) {
        this.offset = offset;
        this.limit = Optional.ofNullable(limit);
        this.order = order;
        this.friend_order = friend_order;
    }
}
