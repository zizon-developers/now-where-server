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
@IdClass(FriendID.class)
public class Friend extends BaseDate {
    @Id
    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;
    @Id
    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendStatus friendStatus;

//    private double recommendationProbability;
    @Builder
    public Friend(User sender, User receiver, FriendStatus friendStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendStatus = friendStatus;
    }
    public void updateFriendStatus(FriendStatus friendStatus){
        this.friendStatus = friendStatus;
    }
//    public void updateRecommendationProbability(){
//    }
}
