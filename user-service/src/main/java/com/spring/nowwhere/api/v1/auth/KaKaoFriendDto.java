package com.spring.nowwhere.api.v1.auth;


import lombok.Builder;

public class KaKaoFriendDto {
    String offset; //Integer	친구 목록 시작 지점(기본값: 0)
    String limit; //Integer	한 페이지에 가져올 친구 최대 수(기본값: 10, 최대: 100)
    String order; //String	친구 목록 정렬 순서 오름차순(asc) 또는 내림차순(desc)(기본값 asc)
    String friend_order;
    /**
     * String	친구 목록 정렬 시 기준 설정
     *     favorite: 즐겨찾기 친구 우선 정렬
     *     nickname: 닉네임 순서 정렬로 기준 설정
     *             (기본값 favorite)
     */
    @Builder
    private KaKaoFriendDto(String offset, String limit, String order, String friend_order) {
        this.offset = offset;
        this.limit = limit;
        this.order = order;
        this.friend_order = friend_order;
    }
}
