--liquibase formatted sql

--changeSet Anton D.:1

CREATE TABLE IF NOT EXISTS responsible(
                                          id SERIAL PRIMARY KEY ,
                                          name VARCHAR UNIQUE ,
                                          phone_number BIGINT,
                                          chat_id BIGINT
);

CREATE TABLE IF NOT EXISTS equipment_type(
                                             id SERIAL PRIMARY KEY ,
                                             name VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS brigadier(
                                        id SERIAL PRIMARY KEY ,
                                        name VARCHAR UNIQUE ,
                                        phone_number BIGINT,
                                        wage_rate FLOAT,
                                        income_rate FLOAT,
                                        is_hourly BOOLEAN,
                                        chat_id BIGINT
);

CREATE TABLE IF NOT EXISTS job (
                                   id SERIAL PRIMARY KEY ,
                                   name VARCHAR UNIQUE,
                                   wage_rate FLOAT,
                                   income_rate FLOAT,
                                   is_hourly BOOLEAN
);

CREATE TABLE IF NOT EXISTS worker(
                                     id SERIAL PRIMARY KEY ,
                                     name VARCHAR UNIQUE ,
                                     job_id INTEGER references job(id),
                                     phone_number BIGINT,
                                     chat_id BIGINT
);

CREATE TABLE if NOT EXISTS address(
                                      id SERIAL PRIMARY KEY ,
                                      short_name VARCHAR UNIQUE ,
                                      full_name VARCHAR UNIQUE ,
                                      zone VARCHAR
);

CREATE TABLE IF NOT EXISTS address_job(
                                          id SERIAL primary key ,
                                          address_id INTEGER references address(id) ON DELETE CASCADE ,
                                          job_id INTEGER references job(id) ON DELETE CASCADE
);


CREATE TABLE If NOT EXISTS brigadier_address(
id SERIAL PRIMARY KEY ,
brigadier_id INTEGER references brigadier(id) ON DELETE CASCADE ,
address_id INTEGER references address(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS worker_address(
id SERIAL PRIMARY KEY ,
worker_id INTEGER references worker(id) ON DELETE CASCADE ,
address_id INTEGER references address(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS responsible_brigadier(
id SERIAL PRIMARY KEY ,
responsible_id INTEGER references responsible(id) ON DELETE CASCADE,
brigadier_id INTEGER references brigadier(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS equipment(
id BIGSERIAL PRIMARY KEY ,
naming VARCHAR ,
type_id INTEGER references equipment_type(id) ON DELETE SET NULL ,
amount FLOAT ,
total FLOAT ,
price4each FLOAT ,
total_left FLOAT ,
amount_left FLOAT ,
unit VARCHAR ,
given_amount FLOAT ,
given_total FLOAT ,
link VARCHAR ,
source VARCHAR ,
supply_date DATE
);

CREATE TABLE IF NOT EXISTS assignment_equip(
                                               id BIGSERIAL PRIMARY KEY ,
                                               naming VARCHAR,
                                               worker_id INTEGER references worker(id) ON DELETE SET NULL ,
                                               equipment_id BIGINT references equipment(id) ON DELETE SET NULL ,
                                               amount FLOAT,
                                               total Float,
                                               start_date_time TIMESTAMP,
                                               end_date_time TIMESTAMP,
                                               status INTEGER
);

CREATE TABLE IF NOT EXISTS shift(
                                    id SERIAL PRIMARY KEY ,
                                    short_info VARCHAR,
                                    start_date_time TIMESTAMP,
                                    end_date_time TIMESTAMP,
                                    status INTEGER,
                                    type INTEGER,
                                    address_id INTEGER references address(id) ON DELETE SET NULL,
                                    worker_id INTEGER references worker(id) ON DELETE SET NULL ,
                                    job_id INTEGER references job(id) ON DELETE SET NULL ,
                                    brigadier_id INTEGER references brigadier(id) ON DELETE SET NULL ,
                                    total_hours FLOAT
);


CREATE TABLE IF NOT EXISTS expense(
                                      id SERIAL PRIMARY KEY ,
                                      short_info VARCHAR,
                                      total_sum FLOAT,
                                      type VARCHAR,
                                      status VARCHAR,
                                      date_time TIMESTAMP,
                                      address_id INTEGER references address(id) ON DELETE SET NULL ,
                                      worker_id INTEGER references worker(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS contact(
                                      id SERIAL PRIMARY KEY ,
                                      name VARCHAR
);

CREATE TABLE IF NOT EXISTS income(
                                     id SERIAL PRIMARY KEY ,
                                     short_info VARCHAR,
                                     total_sum FLOAT,
                                     type VARCHAR,
                                     status VARCHAR,
                                     date_time TIMESTAMP,
                                     address_id INTEGER references address(id) ON DELETE SET NULL ,
                                     worker_id INTEGER references worker(id) ON DELETE SET NULL,
                                     contact_id INTEGER references contact(id) ON DELETE SET NULL

);

ALTER TABLE shift ADD COLUMN income_id INTEGER references income(id) ON DELETE SET NULL;
ALTER TABLE income ADD COLUMN shift_id INTEGER  references shift(id) ON DELETE SET NULL;

ALTER TABLE shift ADD COLUMN expense_id INTEGER references expense(id) ON DELETE SET NULL;
ALTER TABLE expense ADD COLUMN shift_id INTEGER  references shift(id) ON DELETE SET NULL;


CREATE TABLE IF NOT EXISTS admin(
                                    id SERIAL PRIMARY KEY ,
                                    phone_number BIGINT,
                                    chat_id BIGINT
);


CREATE TABLE IF NOT EXISTS users(
                                    id SERIAL PRIMARY KEY ,
                                    name VARCHAR,
                                    password VARCHAR,
                                    role VARCHAR
);

CREATE TABLE IF NOT EXISTS service(
                                      id SERIAL PRIMARY KEY ,
                                      name VARCHAR,
                                      price FLOAT,
                                      category INTEGER,
                                      minimal_price FLOAT,
                                      promo_code VARCHAR,
                                      promo_code_discount INTEGER
);


CREATE TABLE IF NOT EXISTS "order"(
                                      id BIGSERIAL PRIMARY KEY ,
                                      total FLOAT,
                                      sub_total FLOAT,
                                      promo_total FLOAT,
                                      order_date_time TIMESTAMP,
                                      order_offset INTEGER,
                                      date_time TIMESTAMP,
                                      area FLOAT,
                                      phone_number BIGINT,
                                      client_name VARCHAR,
                                      email VARCHAR,
                                      address VARCHAR,
                                      promo_code VARCHAR
);

CREATE TABLE IF NOT EXISTS order_service(
                                            id BIGSERIAL PRIMARY KEY ,
                                            order_id BIGINT references "order"(id) ON DELETE CASCADE,
                                            service_id INTEGER references service("id") ON DELETE SET NULL
);