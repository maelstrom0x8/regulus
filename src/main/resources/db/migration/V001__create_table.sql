CREATE SEQUENCE users_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE agents_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE profiles_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE tokens_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE locations_id_seq START WITH 1000 INCREMENT BY 1;


CREATE TABLE users (
    user_id BIGINT DEFAULT nextval('users_id_seq') PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    verified BOOLEAN DEFAULT FALSE,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE locations (
    location_id BIGINT DEFAULT nextval('locations_id_seq') PRIMARY KEY,
    street_no VARCHAR(5) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(8) NOT NULL,
    country VARCHAR(2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NOT NULL,
    geolocation geography(Point, 4326)
);

CREATE TABLE profiles (
    profile_id BIGINT DEFAULT nextval('profiles_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    location_id BIGINT,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE CASCADE
);

CREATE TABLE disposers (
    user_id BIGINT REFERENCES users(user_id)
);

CREATE TYPE payload_status AS ENUM (
    'PENDING',
    'DISPOSED',
    'CANCELLED',
    'NO_AVAILABLE_AGENTS'
);

CREATE TYPE label AS ENUM (
    'BIO_WASTE',
    'HAZARDOUS',
    'MEDICAL',
    'RADIOACTIVE',
    'MSW'
);

CREATE TYPE priority AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'URGENT'
);


CREATE TABLE payloads (
    payload_id BIGSERIAL PRIMARY KEY,
    lbl label NOT NULL,
    status payload_status NOT NULL DEFAULT 'PENDING',
    weight INTEGER NOT NULL,
    pty priority DEFAULT 'MEDIUM',
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NOT NULL,
    location_id BIGINT REFERENCES locations(location_id)
);

CREATE TABLE collectors (
    collector_id SERIAL NOT NULL,
    user_id BIGINT REFERENCES users(user_id),
    name VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY(collector_id)
);

CREATE TABLE agents (
    agent_id BIGSERIAL NOT NULL,
    collector_id BIGINT NOT NULL,
    location_id BIGINT REFERENCES locations(location_id),
    available BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(agent_id),
    FOREIGN KEY(collector_id) REFERENCES collectors(collector_id) ON DELETE CASCADE
);

CREATE INDEX idx_users_user_id ON users(user_id);
