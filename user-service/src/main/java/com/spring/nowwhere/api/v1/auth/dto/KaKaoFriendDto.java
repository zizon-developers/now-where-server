package com.spring.nowwhere.api.v1.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import java.util.Optional;

@Getter
@Schema(description = "카카오 친구목록 조회를 위한 요청DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KaKaoFriendDto {

    @Schema(description = "친구 목록 시작 지점", defaultValue = "0", nullable = true, minimum = "0")
    private int offset;
    @Schema(description = "한 페이지에 가져올 친구 최대 수", defaultValue = "10",
            nullable = true, minimum = "1", maximum = "100")
    private Optional<Integer> limit;

    @Schema(description = "친구 목록 정렬 순서",
            defaultValue = "asc", nullable = true, allowableValues = {"asc","desc"})
    private String order;

    @Schema(description = "favorite: 즐겨찾기 친구 우선 정렬, nickname: 닉네임 순서 정렬로 기준 설정",
            defaultValue = "favorite", nullable = true, allowableValues = {"favorite","nickname"})
    private String friendOrder;

    @Builder
    private KaKaoFriendDto(int offset, int limit, String order, String friendOrder) {
        this.offset = offset;
        this.limit = Optional.ofNullable(limit);
        this.order = order;
        this.friendOrder = friendOrder;
    }
}
