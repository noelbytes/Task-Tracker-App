-- Flyway migration V1: initial schema for Task Tracker
-- Creates users and tasks tables to match JPA entities

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tasks (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(50) NOT NULL DEFAULT 'TODO',
  priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  completed_at TIMESTAMP WITHOUT TIME ZONE,
  user_id BIGINT NOT NULL,
  CONSTRAINT fk_tasks_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);

