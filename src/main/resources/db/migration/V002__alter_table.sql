--CREATE TABLE roles (
--    role_id SERIAL PRIMARY KEY,
--    name VARCHAR(50) NOT NULL UNIQUE
--);
--
--ALTER TABLE users
--ADD COLUMN role_id INT REFERENCES roles(role_id);


--CREATE TABLE payload_status_logs (
--    log_id BIGSERIAL PRIMARY KEY,
--    payload_id BIGINT REFERENCES payloads(payload_id),
--    status payload_status NOT NULL,
--    changed_at TIMESTAMP DEFAULT now()
--);

--
--ALTER TABLE payloads
--ADD COLUMN assigned_agent_id BIGINT REFERENCES agents(agent_id);
--
--CREATE INDEX idx_locations_geolocation ON locations USING GIST(geolocation);
--
--CREATE TABLE agent_schedules (
--    agent_id BIGINT REFERENCES agents(agent_id),
--    available_from TIMESTAMP,
--    available_to TIMESTAMP,
--    PRIMARY KEY(agent_id, available_from)
--);
--
--ALTER TABLE collectors
--ADD COLUMN region_id BIGINT REFERENCES regions(region_id);


