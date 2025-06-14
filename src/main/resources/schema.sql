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
    updated_at       TIMESTAMP,
    score            DOUBLE PRECISION NOT NULL,
    review_score_sum DOUBLE PRECISION,
    like_count       BIGINT,
    comment_count    BIGINT,
    date             DATE             NOT NULL,
    rank             BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_period_date UNIQUE (user_id, period)
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
    CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
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

CREATE INDEX IF NOT EXISTS idx_comments_for_infinite_scroll ON comments (review_id, is_deleted, created_at DESC, id DESC);

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

CREATE TABLE if not exists BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE if not exists BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT    NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE if not exists BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE if not exists BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE if not exists BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE if not exists BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE SEQUENCE if not exists BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE if not exists BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE if not exists BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
