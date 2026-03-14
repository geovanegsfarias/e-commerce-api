CREATE TABLE order_item (
    id serial PRIMARY KEY,
    price decimal(10, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    UNIQUE(order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);