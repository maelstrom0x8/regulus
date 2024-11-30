CREATE SEQUENCE users_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE agents_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE profiles_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE tokens_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE locations_id_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE operators_id_seq START WITH 1000 INCREMENT BY 1;


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
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE CASCADE
);

CREATE TABLE profile_attributes (
    profile_id BIGINT REFERENCES profiles(profile_id) ON DELETE CASCADE,
    attr_key VARCHAR(50),
    attr_value JSONB,
    PRIMARY KEY(profile_id, attr_key)
);


CREATE TABLE generators (
    user_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE landfill_operators (
    user_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE recyclers (
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

CREATE TABLE operators (
    operator_id INTEGER DEFAULT nextval('operators_id_seq') PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    capacity INTEGER,
    name VARCHAR(100) NOT NULL UNIQUE
);


CREATE TYPE payload_type AS ENUM (
    'RECYCLABLE',
    'NON_RECYCLABLE'
);

CREATE TABLE operator_payload_types (
    operator_id INTEGER NOT NULL,
    p_type payload_type NOT NULL,
    PRIMARY KEY (operator_id, p_type),
    FOREIGN KEY (operator_id) REFERENCES operators(operator_id) ON DELETE CASCADE
);


CREATE TABLE collectors (
    collector_id SERIAL NOT NULL,
    name VARCHAR(100),
    user_id BIGINT REFERENCES users(user_id),
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
