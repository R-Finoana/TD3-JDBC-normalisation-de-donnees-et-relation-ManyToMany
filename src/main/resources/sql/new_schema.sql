create type unit_type as enum('PCS', 'KG', 'L');

create table dish_ingredient(
    id serial primary key,
    id_dish int references dish (id),
    id_ingredient int references ingredient (id),
    quantity_required numeric,
    unit unit_type
);

ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;
ALTER TABLE ingredient DROP COLUMN IF EXISTS required_quantity;

ALTER TABLE dish RENAME COLUMN price TO selling_price;