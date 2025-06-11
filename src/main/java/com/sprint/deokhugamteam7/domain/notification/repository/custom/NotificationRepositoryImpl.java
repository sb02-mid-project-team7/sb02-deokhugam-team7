package com.sprint.deokhugamteam7.domain.notification.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationCursorRequest;
import com.sprint.deokhugamteam7.domain.notification.dto.NotificationDto;
import com.sprint.deokhugamteam7.domain.notification.entity.Notification;
import com.sprint.deokhugamteam7.domain.notification.entity.QNotification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNotification notification = QNotification.notification;

    @Override
    public Slice<NotificationDto> findAllByCursor(NotificationCursorRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        // where
        builder.and(notification.user.id.eq(request.userId()));
        builder.and(notification.isDelete.eq(false));

        if (request.cursor() != null && !request.cursor().isEmpty() && request.after() != null) {
            LocalDateTime cursor = request.parsedCursor();

            if ("ASC".equalsIgnoreCase(request.direction())) {
                builder.and(notification.created_at.gt(cursor));
            } else {
                builder.and(notification.created_at.lt(cursor));
            }
        }

        // orderBy
        OrderSpecifier<?>[] orderSpecifiers = "ASC".equalsIgnoreCase(request.direction())
            ? new OrderSpecifier[]{notification.created_at.asc()}
            : new OrderSpecifier[]{notification.created_at.desc()};

        List<Notification> notificationList = queryFactory
            .selectFrom(notification)
            .where(builder)
            .orderBy(orderSpecifiers)
            .limit(request.limit() + 1)
            .fetch();

        boolean hasNext = notificationList.size() > request.limit();
        List<Notification> content = hasNext ? notificationList.subList(0, request.limit()) : notificationList;

        List<NotificationDto> notificationDtoList = content.stream()
            .map(NotificationDto::fromEntity)
            .toList();

        return new SliceImpl<>(notificationDtoList, PageRequest.of(0, request.limit()), hasNext);
    }

    @Override
    public long countAllById(UUID userId) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(notification.user.id.eq(userId));
        builder.and(notification.isDelete.eq(false));

        return queryFactory.selectFrom(notification)
            .where(builder)
            .stream().count();
    }
}
