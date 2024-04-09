CREATE TABLE ad
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    title           VARCHAR(255)          NULL,
    `description`   VARCHAR(255)          NULL,
    price           DOUBLE                NULL,
    local_date_time datetime              NULL,
    zipcode         VARCHAR(255)          NULL,
    category_id     BIGINT                NULL,
    user_id         BINARY(36)            NULL,
    CONSTRAINT pk_ad PRIMARY KEY (id)
);

CREATE TABLE category
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255)          NULL,
    `description` VARCHAR(255)          NULL,
    created_at    datetime              NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE confirmation_token
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    token   VARCHAR(255)          NOT NULL,
    user_id BINARY(36)            NULL,
    CONSTRAINT pk_confirmationtoken PRIMARY KEY (id)
);

CREATE TABLE `role`
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE user_details
(
    id       BINARY(36)   NOT NULL,
    username VARCHAR(255) NULL,
    email    VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    verified BIT(1)       NULL,
    CONSTRAINT pk_user_details PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BIGINT     NOT NULL,
    user_id BINARY(36) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE confirmation_token
    ADD CONSTRAINT uc_confirmationtoken_user UNIQUE (user_id);

ALTER TABLE ad
    ADD CONSTRAINT FK_AD_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE ad
    ADD CONSTRAINT FK_AD_ON_USER FOREIGN KEY (user_id) REFERENCES user_details (id);

ALTER TABLE confirmation_token
    ADD CONSTRAINT FK_CONFIRMATIONTOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES user_details (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES user_details (id);