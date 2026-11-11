

INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780134685991',
    'Effective Java',
    'Joshua Bloch',
    'Addison-Wesley',
    'A guide to effective Java programming.',
    NULL,
    49.99,
    10
    WHERE NOT EXISTS (
    SELECT 1 FROM books
    WHERE isbn = '9780134685991'
);

INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780132350884',
    'Clean Code',
    'Robert C. Martin',
    'Prentice Hall',
    'A handbook of agile software craftsmanship.',
    NULL,
    39.99,
    5
    WHERE NOT EXISTS (
    SELECT 1 FROM books
    WHERE isbn = '9780132350884'
);
