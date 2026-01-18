create database "mini_dish_db";

create user "mini_dish_db_manager" with password '123456';

-- Grant all privileges
GRANT CONNECT ON DATABASE mini_dish_db TO mini_dish_db_manager;

--Authorize tables creation
    GRANT CREATE ON SCHEMA public TO mini_dish_db_manager;

--Authorize CRUD operations on all future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO mini_dish_db_manager ;

--Auto-increment usage
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO mini_dish_db_manager ;
