CREATE TABLE orders (
    id serial PRIMARY KEY,
    price decimal(10, 2) NOT NULL,
    date TIMESTAMP NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);