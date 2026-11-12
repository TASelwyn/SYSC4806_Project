

INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780134685991',
    'Effective Java',
    'Joshua Bloch',
    'Addison-Wesley',
    'A guide to effective Java programming.',
    'https://covers.openlibrary.org/b/isbn/9780134685991-M.jpg',
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
    'https://covers.openlibrary.org/b/isbn/9780132350884-M.jpg',
    39.99,
    5
    WHERE NOT EXISTS (
    SELECT 1 FROM books
    WHERE isbn = '9780132350884'
);



INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780201616224',
    'The Pragmatic Programmer',
    'Andrew Hunt and David Thomas',
    'Addison-Wesley',
    'Practical approaches to software development.',
    'https://covers.openlibrary.org/b/isbn/9780201616224-M.jpg',
    54.99,
    8
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780201616224'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780201633610',
    'Design Patterns',
    'Erich Gamma, Richard Helm, Ralph Johnson and John Vlissides',
    'Addison-Wesley',
    'Reusable object-oriented software design patterns.',
    'https://covers.openlibrary.org/b/isbn/9780201633610-M.jpg',
    64.99,
    6
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780201633610'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780134494166',
    'Clean Architecture',
    'Robert C. Martin',
    'Prentice Hall',
    'Principles for designing maintainable software systems.',
    'https://covers.openlibrary.org/b/isbn/9780134494166-M.jpg',
    44.99,
    12
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780134494166'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9781491950357',
    'Designing Data-Intensive Applications',
    'Martin Kleppmann',
    'O Reilly Media',
    'Designing reliable and scalable data systems.',
    'https://covers.openlibrary.org/b/isbn/9781491950357-M.jpg',
    69.99,
    4
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9781491950357'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780131103627',
    'The C Programming Language',
    'Brian Kernighan and Dennis Ritchie',
    'Prentice Hall',
    'Introduction to the C programming language.',
    'https://covers.openlibrary.org/b/isbn/9780131103627-M.jpg',
    39.99,
    15
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780131103627'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780596007126',
    'Head First Design Patterns',
    'Eric Freeman and Elisabeth Robson',
    'O Reilly Media',
    'Visual introduction to common software design patterns.',
    'https://covers.openlibrary.org/b/isbn/9780596007126-M.jpg',
    49.99,
    3
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780596007126'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780596009205',
    'Head First Java',
    'Kathy Sierra and Bert Bates',
    'O Reilly Media',
    'An interactive introduction to Java programming.',
    'https://covers.openlibrary.org/b/isbn/9780596009205-M.jpg',
    34.99,
    9
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780596009205'
);


INSERT INTO books
(isbn, title, author, publisher, description,
 image_url, price, stock_quantity)
SELECT
    '9780262033848',
    'Introduction to Algorithms',
    'Thomas Cormen, Charles Leiserson, Ronald Rivest and Clifford Stein',
    'MIT Press',
    'Algorithms, data structures and complexity analysis.',
    'https://covers.openlibrary.org/b/isbn/9780262033848-M.jpg',
    89.99,
    0
    WHERE NOT EXISTS (
    SELECT 1 FROM books WHERE isbn = '9780262033848'
);

