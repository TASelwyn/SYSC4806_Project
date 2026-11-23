-- Sample users for the "pretend to log in" user toggle.

INSERT INTO users (id, first_name, last_name, email, role)
SELECT 'ff471d55-aa94-4301-9476-947f77914498', 'Olivia', 'Brown', 'admin@amazin.com', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT 'e698b2f8-50e1-4e39-9e74-19ac607f75d4', 'Alice', 'Smith', 'alice@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT 'e80a357d-300c-4d3d-a895-18498fabc5a6', 'Bob', 'Jones', 'bob@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@amazin.com');

INSERT INTO users (id, first_name, last_name, email, role)
SELECT '67e5ef06-440c-4981-be34-6d2b431fe245', 'Carol', 'Lee', 'carol@amazin.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carol@amazin.com');
