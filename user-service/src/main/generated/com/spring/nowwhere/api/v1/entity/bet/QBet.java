package com.spring.nowwhere.api.v1.entity.bet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBet is a Querydsl query type for Bet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBet extends EntityPathBase<Bet> {

    private static final long serialVersionUID = -1733440897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBet bet = new QBet("bet");

    public final com.spring.nowwhere.api.v1.entity.QBaseDate _super = new com.spring.nowwhere.api.v1.entity.QBaseDate(this);

    public final QBetInfo betInfo;

    public final EnumPath<BetResult> betResult = createEnum("betResult", BetResult.class);

    public final EnumPath<BetStatus> betStatus = createEnum("betStatus", BetStatus.class);

    public final com.spring.nowwhere.api.v1.entity.user.QUser bettor;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final com.spring.nowwhere.api.v1.entity.user.QUser receiver;

    public QBet(String variable) {
        this(Bet.class, forVariable(variable), INITS);
    }

    public QBet(Path<? extends Bet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBet(PathMetadata metadata, PathInits inits) {
        this(Bet.class, metadata, inits);
    }

    public QBet(Class<? extends Bet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.betInfo = inits.isInitialized("betInfo") ? new QBetInfo(forProperty("betInfo"), inits.get("betInfo")) : null;
        this.bettor = inits.isInitialized("bettor") ? new com.spring.nowwhere.api.v1.entity.user.QUser(forProperty("bettor")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.spring.nowwhere.api.v1.entity.user.QUser(forProperty("receiver")) : null;
    }

}

