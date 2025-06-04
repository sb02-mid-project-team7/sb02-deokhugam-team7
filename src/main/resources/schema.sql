create table if not exists users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(30)  NOT NULL UNIQUE,
    nickname   VARCHAR(20)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP
);

create table if not exists user_score
(
    id               UUID PRIMARY KEY,
    user_id          UUID             NOT NULL,
    period           VARCHAR(20)      NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')),
    created_at       TIMESTAMP        NOT NULL,
    score            DOUBLE PRECISION NOT NULL,
    review_score_sum DOUBLE PRECISION,
    like_count       BIGINT,
    comment_count    BIGINT,
    date             DATE             NOT NULL,
    rank             BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_period_date UNIQUE (user_id, period, date)
);

create table if not exists books
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP,
    is_deleted     BOOLEAN     NOT NULL DEFAULT FALSE,
    title          VARCHAR(50) NOT NULL,
    author         VARCHAR(50) NOT NULL,
    publisher      VARCHAR(50) NOT NULL,
    published_date TIMESTAMP   NOT NULL,
    description    TEXT,
    isbn           VARCHAR(50) UNIQUE,
    thumbnail_url  VARCHAR(255)
);

create table if not exists ranking_books
(
    id           UUID PRIMARY KEY,
    book_id      UUID,
    rank         BIGINT,
    period       VARCHAR(255) NOT NULL,
    score        DOUBLE PRECISION,
    total_rating INTEGER,
    review_count BIGINT,
    rating       DOUBLE PRECISION,
    CONSTRAINT uk_book_period UNIQUE (book_id, period),
    constraint fk_ranking_book foreign key (book_id) references books (id) on delete cascade
);

create table if not exists reviews
(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL,
    book_id    UUID      NOT NULL,
    content    TEXT      NOT NULL,
    rating     INT       NOT NULL CHECK (rating BETWEEN 0 AND 5),
    is_deleted BOOLEAN   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT uk_reviews_user_book UNIQUE (user_id, book_id)
);

create table if not exists review_likes
(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL,
    review_id  UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_reviewlikes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviewlikes_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT uk_reviewlikes_user_review UNIQUE (user_id, review_id)
);

create table if not exists ranking_reviews
(
    id                UUID PRIMARY KEY,
    review_id         UUID             NOT NULL,
    score             DOUBLE PRECISION NOT NULL,
    period            VARCHAR(20)      NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')),
    review_created_at TIMESTAMP        NOT NULL,

    CONSTRAINT fk_rankingreviews_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT uk_rankingreviews_review_period UNIQUE (review_id, period)
);

create table if not exists comments
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    review_id  UUID         NOT NULL,
    content    VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comments_for_infinite_scroll ON comments (review_id, is_deleted, created_at DESC, id DESC)

create table if not exists notifications
(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL,
    review_id  UUID      NOT NULL,
    content    TEXT,
    confirmed  BOOLEAN   NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) references users (id),
    CONSTRAINT fk_notification_review FOREIGN KEY (review_id) references reviews (id)
);