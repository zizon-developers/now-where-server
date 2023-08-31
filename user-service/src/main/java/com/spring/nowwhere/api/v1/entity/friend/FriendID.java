package com.spring.nowwhere.api.v1.entity.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendID implements Serializable {
    private Long sender;
    private Long receiver;
}
