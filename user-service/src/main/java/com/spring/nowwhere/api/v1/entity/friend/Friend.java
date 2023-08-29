package com.spring.nowwhere.api.v1.entity.friend;

import com.spring.nowwhere.api.v1.entity.BaseDate;
import com.spring.nowwhere.api.v1.entity.user.User;
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

    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus;

    @Builder
    public Friend(User sender, User receiver, FriendStatus friendStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendStatus = friendStatus;
    }
    public void updateFriendStatus(FriendStatus friendStatus){
        if (FriendStatus.PENDING.equals(this.friendStatus)){
            this.friendStatus = friendStatus;
        }
    }
}
