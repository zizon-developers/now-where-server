package com.spring.nowwhere.api.v1.entity.bet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBetInfo is a Querydsl query type for BetInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QBetInfo extends BeanPath<BetInfo> {

    private static final long serialVersionUID = -1613149235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBetInfo betInfo = new QBetInfo("betInfo");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final QLocation appointmentLocation;

    public final QBetDateTime betDateTime;

    public QBetInfo(String variable) {
        this(BetInfo.class, forVariable(variable), INITS);
    }

    public QBetInfo(Path<? extends BetInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBetInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBetInfo(PathMetadata metadata, PathInits inits) {
        this(BetInfo.class, metadata, inits);
    }

    public QBetInfo(Class<? extends BetInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.appointmentLocation = inits.isInitialized("appointmentLocation") ? new QLocation(forProperty("appointmentLocation")) : null;
        this.betDateTime = inits.isInitialized("betDateTime") ? new QBetDateTime(forProperty("betDateTime")) : null;
    }

}

