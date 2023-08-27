package com.spring.nowwhere.api.v1.entity.user.entity;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private boolean isFriend;

    @Builder
    private Friend(User sender, User receiver, boolean isFriend) {
        this.sender = sender;
        this.receiver = receiver;
        this.isFriend = isFriend;
    }
}
