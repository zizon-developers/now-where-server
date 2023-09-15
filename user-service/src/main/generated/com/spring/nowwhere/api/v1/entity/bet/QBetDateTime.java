package com.spring.nowwhere.api.v1.entity.bet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBetDateTime is a Querydsl query type for BetDateTime
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QBetDateTime extends BeanPath<BetDateTime> {

    private static final long serialVersionUID = -1746112710L;

    public static final QBetDateTime betDateTime = new QBetDateTime("betDateTime");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public QBetDateTime(String variable) {
        super(BetDateTime.class, forVariable(variable));
    }

    public QBetDateTime(Path<? extends BetDateTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBetDateTime(PathMetadata metadata) {
        super(BetDateTime.class, metadata);
    }

}

