ALTER TABLE product DROP CONSTRAINT product_category_id_fkey;
ALTER TABLE cart DROP CONSTRAINT cart_user_id_fkey;
ALTER TABLE cart_item DROP CONSTRAINT cart_item_cart_id_fkey;
ALTER TABLE cart_item DROP CONSTRAINT cart_item_product_id_fkey;
ALTER TABLE orders DROP CONSTRAINT orders_user_id_fkey;
ALTER TABLE order_item DROP CONSTRAINT order_item_order_id_fkey;
ALTER TABLE order_item DROP CONSTRAINT order_item_product_id_fkey;

ALTER TABLE users ALTER COLUMN id TYPE BIGINT;
ALTER TABLE category ALTER COLUMN id TYPE BIGINT;
ALTER TABLE product
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN category_id TYPE BIGINT;
ALTER TABLE cart
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE cart_item
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN cart_id TYPE BIGINT,
    ALTER COLUMN product_id TYPE BIGINT;
ALTER TABLE orders
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE order_item
    ALTER COLUMN id TYPE BIGINT,
    ALTER COLUMN order_id TYPE BIGINT,
    ALTER COLUMN product_id TYPE BIGINT;

ALTER SEQUENCE users_id_seq AS BIGINT;
ALTER SEQUENCE category_id_seq AS BIGINT;
ALTER SEQUENCE product_id_seq AS BIGINT;
ALTER SEQUENCE cart_id_seq AS BIGINT;
ALTER SEQUENCE cart_item_id_seq AS BIGINT;
ALTER SEQUENCE orders_id_seq AS BIGINT;
ALTER SEQUENCE order_item_id_seq AS BIGINT;

ALTER TABLE product
    ADD CONSTRAINT product_category_id_fkey
        FOREIGN KEY (category_id) REFERENCES category(id);
ALTER TABLE cart
    ADD CONSTRAINT cart_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE cart_item
    ADD CONSTRAINT cart_item_cart_id_fkey
        FOREIGN KEY (cart_id) REFERENCES cart(id),
    ADD CONSTRAINT cart_item_product_id_fkey
        FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE orders
    ADD CONSTRAINT orders_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE order_item
    ADD CONSTRAINT order_item_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES orders(id),
    ADD CONSTRAINT order_item_product_id_fkey
        FOREIGN KEY (product_id) REFERENCES product(id);
