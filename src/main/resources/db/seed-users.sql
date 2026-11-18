-- Sample users for the "pretend to log in" user toggle.
-- Fixed UUIDs keep the same ids on every restart, so a user saved
-- in the browser's localStorage still matches after the app restarts.

INSERT INTO users (id, first_name, last_name, email, role)
SELECT '00000000-0000-0000-0000-000000000001', 'Olivia', 'Brown', 'admin@amazin.com', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT '00000000-0000-0000-0000-000000000002', 'Alice', 'Smith', 'alice@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT '00000000-0000-0000-0000-000000000003', 'Bob', 'Jones', 'bob@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT '00000000-0000-0000-0000-000000000004', 'Carol', 'Lee', 'carol@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carol@amazin.com');
