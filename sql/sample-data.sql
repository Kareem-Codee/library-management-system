USE library_db;

INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES
('Clean Code', 'Robert C. Martin', '9780132350884', 3, 3),
('Effective Java', 'Joshua Bloch', '9780134685991', 2, 2),
('Head First Java', 'Kathy Sierra', '9780596009205', 4, 4);

INSERT INTO members (full_name, email, joined_at) VALUES
('Ahmad Ali', 'ahmad@example.com', '2026-01-10'),
('Sara Omar', 'sara@example.com', '2026-02-15');
