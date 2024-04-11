CREATE TABLE IF NOT EXISTS responsible(
id SERIAL PRIMARY KEY ,
name VARCHAR UNIQUE ,
phone_number BIGINT,
chat_id BIGINT
);
INSERT INTO responsible(name,phone_number)
VALUES
    ('Имя ответственного 1',9991115566),
    ('Имя ответственного 2',9991115567),
    ('Тестовый супервайзер тг',66994854013);

CREATE TABLE IF NOT EXISTS equipment_type(
id SERIAL PRIMARY KEY ,
name VARCHAR UNIQUE
);
INSERT INTO equipment_type (name)
VALUES ('химия'),('оборудование'),('иное');

CREATE TABLE IF NOT EXISTS brigadier(
id SERIAL PRIMARY KEY ,
name VARCHAR UNIQUE ,
phone_number BIGINT,
wage_rate FLOAT,
income_rate FLOAT,
is_hourly BOOLEAN,
chat_id BIGINT,
ready_to_send_photo BOOLEAN
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
 chat_id BIGINT,
 ready_to_send_photo BOOLEAN
);

CREATE TABLE if NOT EXISTS address(
id SERIAL PRIMARY KEY ,
short_name VARCHAR UNIQUE ,
full_name VARCHAR,
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
total_hours FLOAT,
first_photo_sent BOOLEAN,
folder_id VARCHAR,
folder_link VARCHAR
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

INSERT INTO admin (phone_number) VALUES (79661919669);

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

INSERT INTO service (name, price, category, minimal_price, promo_code, promo_code_discount)
VALUES ('cleaning','50.5',0,'100.4','Skidka','50'),
       ('window cleaning','60.5',0,'150.4','Skidka','50'),
       ('vacuum cleaning','50.5',0,'100.4','Skidka','50'),
       ('deep cleaning','50.5',0,'100.4','Skidka','50'),
       ('mopping','50.5',0,'100.4','Skidka','50'),
       ('dish washing','50.5',0,'100.4','Skidka','50'),
       ('balcony cleaning','50.5',0,'100.4','Skidka','50'),
       ('relocation cleaning','50.5',0,'100.4','Skidka','50'),
       ('extra1','50.5',1,'100.4','Skidka','50'),
       ('extra2','60.5',1,'150.4','Skidka','50'),
       ('extra3','50.5',1,'100.4','Skidka','50'),
       ('extra4','50.5',1,'100.4','Skidka','50'),
       ('extra5','50.5',1,'100.4','Skidka','50'),
       ('extra6','50.5',1,'100.4','Skidka','50'),
       ('extra7','50.5',1,'100.4','Skidka','50'),
       ('extra8','50.5',1,'100.4','Skidka','50'),
       ('extra9','50.5',1,'100.4','Skidka','50');


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
INSERT INTO "order"(total,sub_total,promo_total,order_date_time,order_offset,date_time,area,phone_number,client_name,email,address,promo_code)
VALUES (500,500,0,'2024-03-11 17:00:00',0,'2024-03-11 17:00:00',20,79661919669,'tos',null,null,null);
CREATE TABLE IF NOT EXISTS order_service(
    id BIGSERIAL PRIMARY KEY ,
    order_id BIGINT references "order"(id) ON DELETE CASCADE,
    service_id INTEGER references service("id") ON DELETE SET NULL
);
insert into order_service (order_id, service_id)
VALUES(1,1);



INSERT INTO equipment(naming,type_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hyper Tough Hyper Tough 1.5 Amp Corded Rotary Tool, Variable Speed with 105 Rotary Accessories & Storage Case, 120 Volts',1,1,22.97,22.97,22.97,1,'шт.',0,0,'https://www.walmart.com/ip/Hyper-Tough-1-5-Amp-Corded-Rotary-Tool-Variable-Speed-with-105-Rotary-Accessories-Storage-Case-120-Volts/388836290?athbdg=L1200&from=/search','Walmart',TO_DATE('13.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Super Sliders Super Sliders 1" Round Rubber Tip Chair Leg Caps Floor Protection Pad White, 4 Pack',1,4,3.44,0.86,3.44,4,'шт.',0,0,'https://www.walmart.com/ip/Super-Sliders-1-Round-Rubber-Tip-Chair-Leg-Caps-Floor-Protection-Pad-White-4-Pack/16782106?from=/search','Walmart',TO_DATE('13.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('DIABLO 4-1/2 in. x 24-Tooth Framing Circular Saw Blade',1,1,15.77,15.77,15.77,1,'шт.',0,0,'https://www.homedepot.com/p/DIABLO-4-1-2-in-x-24-Tooth-Framing-Circular-Saw-Blade-D0424X/309508784','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));

