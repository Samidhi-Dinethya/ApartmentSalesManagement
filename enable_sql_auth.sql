-- Enable SQL Server Authentication
-- Run this script in SSMS as Administrator

-- Enable SQL Server and Windows Authentication mode
EXEC xp_instance_regwrite 
    N'HKEY_LOCAL_MACHINE', 
    N'Software\Microsoft\MSSQLServer\MSSQLServer', 
    N'LoginMode', 
    REG_DWORD, 
    2;
GO

-- Enable sa account and set password
ALTER LOGIN sa ENABLE;
ALTER LOGIN sa WITH PASSWORD = 200434100283;
GO

PRINT 'SQL Server Authentication enabled successfully!';
PRINT 'SA password set to: YourStrongPassword123';
PRINT 'You will need to restart SQL Server for changes to take effect.';

CREATE TABLE apartments
(
    id          bigint IDENTITY (1, 1) NOT NULL,
    title       varchar(255)           NOT NULL,
    description varchar(max),
    address     varchar(255)           NOT NULL,
    city        varchar(255)           NOT NULL,
    state       varchar(255)           NOT NULL,
    zip_code    varchar(255)           NOT NULL,
    price       decimal(10, 2)         NOT NULL,
    bedrooms    int                    NOT NULL,
    bathrooms   int                    NOT NULL,
    square_feet int                    NOT NULL,
    status      varchar(255)           NOT NULL,
    image_url   varchar(255),
    owner_id    bigint,
    created_at  datetime,
    updated_at  datetime,
    CONSTRAINT pk_apartments PRIMARY KEY (id)
)
GO

CREATE TABLE appointment_requests
(
    id                bigint IDENTITY (1, 1) NOT NULL,
    client_name       varchar(255)           NOT NULL,
    email             varchar(255)           NOT NULL,
    phone_number      varchar(255),
    preferred_date    varchar(255)           NOT NULL,
    preferred_time    varchar(255)           NOT NULL,
    message           varchar(max)           NOT NULL,
    status            varchar(255)           NOT NULL,
    apartment_id      bigint,
    client_id         bigint,
    assigned_agent_id bigint,
    admin_reply       varchar(max),
    created_at        datetime,
    updated_at        datetime,
    replied_at        datetime,
    CONSTRAINT pk_appointment_requests PRIMARY KEY (id)
)
GO

CREATE TABLE appointments
(
    id                   bigint IDENTITY (1, 1) NOT NULL,
    appointment_datetime datetime               NOT NULL,
    notes                varchar(max),
    status               varchar(255)           NOT NULL,
    client_id            bigint                 NOT NULL,
    agent_id             bigint                 NOT NULL,
    apartment_id         bigint                 NOT NULL,
    created_at           datetime,
    updated_at           datetime,
    cancellation_reason  varchar(255),
    feedback_rating      int,
    feedback_comment     varchar(max),
    CONSTRAINT pk_appointments PRIMARY KEY (id)
)
GO

CREATE TABLE parking_spaces
(
    id                   bigint IDENTITY (1, 1) NOT NULL,
    space_number         varchar(255)           NOT NULL,
    location             varchar(255)           NOT NULL,
    monthly_fee          decimal(10, 2)         NOT NULL,
    type                 varchar(255)           NOT NULL,
    status               varchar(255)           NOT NULL,
    is_covered           bit,
    is_electric_charging bit,
    max_vehicle_length   float(53),
    max_vehicle_width    float(53),
    notes                varchar(max),
    assigned_tenant_id   bigint,
    created_at           datetime,
    updated_at           datetime,
    CONSTRAINT pk_parking_spaces PRIMARY KEY (id)
)
GO

CREATE TABLE reviews
(
    id           bigint IDENTITY (1, 1) NOT NULL,
    title        varchar(255)           NOT NULL,
    content      varchar(max)           NOT NULL,
    rating       int                    NOT NULL,
    apartment_id bigint                 NOT NULL,
    user_id      bigint                 NOT NULL,
    is_verified  bit                    NOT NULL,
    is_featured  bit                    NOT NULL,
    is_approved  bit                    NOT NULL,
    created_at   datetime,
    updated_at   datetime,
    approved_at  datetime,
    admin_notes  varchar(max),
    CONSTRAINT pk_reviews PRIMARY KEY (id)
)
GO

CREATE TABLE user_management_roles
(
    user_id         bigint NOT NULL,
    management_role varchar(255)
)
GO

CREATE TABLE users
(
    id           bigint IDENTITY (1, 1) NOT NULL,
    username     varchar(50)            NOT NULL,
    email        varchar(255)           NOT NULL,
    password     varchar(255)           NOT NULL,
    first_name   varchar(255)           NOT NULL,
    last_name    varchar(255)           NOT NULL,
    phone_number varchar(255),
    role         varchar(255)           NOT NULL,
    is_active    bit,
    created_at   datetime,
    updated_at   datetime,
    CONSTRAINT pk_users PRIMARY KEY (id)
)
GO

ALTER TABLE parking_spaces
    ADD CONSTRAINT uc_parking_spaces_space_number UNIQUE (space_number)
GO

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email)
GO

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username)
GO

ALTER TABLE apartments
    ADD CONSTRAINT FK_APARTMENTS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id)
GO

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_AGENT FOREIGN KEY (agent_id) REFERENCES users (id)
GO

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_APARTMENT FOREIGN KEY (apartment_id) REFERENCES apartments (id)
GO

ALTER TABLE appointments
    ADD CONSTRAINT FK_APPOINTMENTS_ON_CLIENT FOREIGN KEY (client_id) REFERENCES users (id)
GO

ALTER TABLE appointment_requests
    ADD CONSTRAINT FK_APPOINTMENT_REQUESTS_ON_APARTMENT FOREIGN KEY (apartment_id) REFERENCES apartments (id)
GO

ALTER TABLE appointment_requests
    ADD CONSTRAINT FK_APPOINTMENT_REQUESTS_ON_ASSIGNED_AGENT FOREIGN KEY (assigned_agent_id) REFERENCES users (id)
GO

ALTER TABLE appointment_requests
    ADD CONSTRAINT FK_APPOINTMENT_REQUESTS_ON_CLIENT FOREIGN KEY (client_id) REFERENCES users (id)
GO

ALTER TABLE parking_spaces
    ADD CONSTRAINT FK_PARKING_SPACES_ON_ASSIGNED_TENANT FOREIGN KEY (assigned_tenant_id) REFERENCES users (id)
GO

ALTER TABLE reviews
    ADD CONSTRAINT FK_REVIEWS_ON_APARTMENT FOREIGN KEY (apartment_id) REFERENCES apartments (id)
GO

ALTER TABLE reviews
    ADD CONSTRAINT FK_REVIEWS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
GO

ALTER TABLE user_management_roles
    ADD CONSTRAINT fk_user_management_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id)
GO