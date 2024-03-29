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
                                        chat_id BIGINT
);
INSERT INTO brigadier (name, phone_number,wage_rate,income_rate,is_hourly)
VALUES ('Бригадир Иван',88005553535,150.5,200.0,true),
       ('Бригадир Пётр',13337272729,150.5,200.0,true),
       ('Тестовый бригадир тг',84792547313,150.5,200.0,true);

CREATE TABLE IF NOT EXISTS job (
    id SERIAL PRIMARY KEY ,
    name VARCHAR UNIQUE,
    wage_rate FLOAT,
    income_rate FLOAT,
    is_hourly BOOLEAN
);

INSERT INTO job (name,wage_rate,income_rate,is_hourly) VALUES
('клинер',50.0,100.0,true),
('механик',25.55,55.55,true),
('космонавт',1000.0,5000.0,false),
('плотник',50.0,100.0,true),
('слесарь',50.0,100.0,true),
('водитель',50.0,100.0,false);

CREATE TABLE IF NOT EXISTS worker(
                                     id SERIAL PRIMARY KEY ,
                                     name VARCHAR UNIQUE ,
                                     job_id INTEGER references job(id),
                                     phone_number BIGINT,
                                     chat_id BIGINT
);
INSERT INTO worker (name, job_id, phone_number)
VALUES ('Работник Елена', 1, 19568004545),('работник Александр', 2,19002332323),
       ('тестовый тг',3,27817913239);

CREATE TABLE if NOT EXISTS address(
                                      id SERIAL PRIMARY KEY ,
                                      short_name VARCHAR UNIQUE ,
                                      full_name VARCHAR UNIQUE ,
                                      zone VARCHAR
);
INSERT INTO address (short_name, full_name, zone)
VALUES ('123 Main St', '123 Main Street', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('456 Elm St', '456 Elm Street', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('789 Oak St', '789 Oak Street', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('10 Pine Ave', '10 Pine Avenue', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('321 Maple Dr', '321 Maple Drive', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('555 Cedar Ln', '555 Cedar Lane', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('777 Birch Rd', '777 Birch Road', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('999 Walnut Blvd', '999 Walnut Boulevard', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('111 Willow Ct', '111 Willow Court', 'US');

INSERT INTO address (short_name, full_name, zone)
VALUES ('888 Spruce Pl', '888 Spruce Place', 'US');

CREATE TABLE IF NOT EXISTS address_job(
    id SERIAL primary key ,
    address_id INTEGER references address(id) ON DELETE CASCADE ,
    job_id INTEGER references job(id) ON DELETE CASCADE
);

INSERT INTO address_job (address_id, job_id)
VALUES (1,1),(1,2),(2,1),(3,1),(3,2);


CREATE TABLE If NOT EXISTS brigadier_address(
                                                id SERIAL PRIMARY KEY ,
                                                brigadier_id INTEGER references brigadier(id) ON DELETE CASCADE ,
                                                address_id INTEGER references address(id) ON DELETE CASCADE
);
INSERT INTO brigadier_address (brigadier_id, address_id)
VALUES (1,1),(1,2),(2,1),(3,1),(3,2);

CREATE TABLE IF NOT EXISTS worker_address(
                                             id SERIAL PRIMARY KEY ,
                                             worker_id INTEGER references worker(id) ON DELETE CASCADE ,
                                             address_id INTEGER references address(id) ON DELETE CASCADE
);
INSERT INTO worker_address (worker_id, address_id)
VALUES (1,1),(1,2),(2,1),(3,1),(3,2);

CREATE TABLE IF NOT EXISTS responsible_brigadier(
                                                    id SERIAL PRIMARY KEY ,
                                                    responsible_id INTEGER references responsible(id) ON DELETE CASCADE,
                                                    brigadier_id INTEGER references brigadier(id) ON DELETE CASCADE
);
INSERT INTO responsible_brigadier (responsible_id, brigadier_id)
VALUES (1,1),(1,2),(2,1),(3,3),(3,1);


CREATE TABLE IF NOT EXISTS equipment(
                                        id BIGSERIAL PRIMARY KEY ,
                                        naming VARCHAR ,
                                        type_id INTEGER references equipment_type(id) ON DELETE SET NULL ,
                                        responsible_id INTEGER references responsible(id) ON DELETE SET NULL,
                                        amount INTEGER ,
                                        total FLOAT ,
                                        price4each FLOAT ,
                                        total_left FLOAT ,
                                        amount_left INTEGER ,
                                        unit VARCHAR ,
                                        given_amount INTEGER ,
                                        given_total FLOAT ,
                                        link VARCHAR ,
                                        source VARCHAR ,
                                        supply_date DATE
);

CREATE TABLE IF NOT EXISTS shift(
                                    id SERIAL PRIMARY KEY ,
                                    short_info VARCHAR,
                                    start_date_time TIMESTAMP,
                                    end_date_time TIMESTAMP,
                                    status VARCHAR,
                                    type VARCHAR,
                                    address_id INTEGER references address(id) ON DELETE SET NULL,
                                    worker_id INTEGER references worker(id) ON DELETE SET NULL ,
                                    job_id INTEGER references job(id) ON DELETE SET NULL ,
                                    brigadier_id INTEGER references brigadier(id) ON DELETE SET NULL ,
                                    total_hours FLOAT
);
INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 1', '2024-03-06 09:00:00', '2024-03-06 17:00:00', 'Completed', 1, 1, 1, 1, 8.0);

INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 2', '2024-03-07 10:00:00', '2024-03-07 18:00:00', 'In progress', 2, 2, 2, 2, 8.0);

INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 3', '2024-03-08 08:00:00', '2024-03-08 16:00:00', 'Completed', 1, 2, 3, 1, 8.0);

INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 4', '2024-03-09 07:00:00', '2024-03-09 15:00:00', 'In progress', 2, 1, 4, 2, 8.0);

INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 5', '2024-03-10 08:00:00', '2024-03-10 16:00:00', 'Completed', 1, 1, 5, 1, 8.0);

INSERT INTO shift (short_info, start_date_time, end_date_time, status, address_id, worker_id, job_id, brigadier_id, total_hours)
VALUES ('Short info 6', '2024-03-11 09:00:00', '2024-03-11 17:00:00', 'In progress', 2, 2, 6, 2, 8.0);


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

INSERT INTO expense (short_info, total_sum, type, status, date_time, address_id, worker_id, shift_id)
VALUES
    ('Expense 1', 100.01, 'Type 1', 'Status 1','2024-03-11 17:00:00', 1, 1, 1),
    ('Expense 2', 150.20, 'Type 2', 'Status 2', '2024-03-11 17:00:00', 2, 2, 2),
    ('Expense 3', 200.00, 'Type 1', 'Status 1', '2024-03-11 17:00:00', 1, 1, 2),
    ('Expense 4', 250.55, 'Type 2', 'Status 2', '2024-03-11 17:00:00', 2, 2, 1);

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




INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hyper Tough Hyper Tough 1.5 Amp Corded Rotary Tool, Variable Speed with 105 Rotary Accessories & Storage Case, 120 Volts',1,1,1,22.97,22.97,22.97,1,'шт.',NULL,0,'https://www.walmart.com/ip/Hyper-Tough-1-5-Amp-Corded-Rotary-Tool-Variable-Speed-with-105-Rotary-Accessories-Storage-Case-120-Volts/388836290?athbdg=L1200&from=/search','Walmart',TO_DATE('13.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Super Sliders Super Sliders 1" Round Rubber Tip Chair Leg Caps Floor Protection Pad White, 4 Pack',1,1,4,3.44,0.86,3.44,4,'шт.',NULL,0,'https://www.walmart.com/ip/Super-Sliders-1-Round-Rubber-Tip-Chair-Leg-Caps-Floor-Protection-Pad-White-4-Pack/16782106?from=/search','Walmart',TO_DATE('13.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('DIABLO 4-1/2 in. x 24-Tooth Framing Circular Saw Blade',1,1,1,15.77,15.77,15.77,1,'шт.',NULL,0,'https://www.homedepot.com/p/DIABLO-4-1-2-in-x-24-Tooth-Framing-Circular-Saw-Blade-D0424X/309508784','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('DIABLO Steel Demon 4-1/2 in. x 0.045 in. x 7/8 in. Metal Cut Off Type 1',1,1,1,4.47,4.47,4.47,1,'шт.',NULL,0,'https://www.homedepot.com/p/DIABLO-Steel-Demon-4-1-2-in-x-0-045-in-x-7-8-in-Metal-Cut-Off-Type-1-DBDS45045101F/310832755','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('DIABLO 4-1/2 in. x 1/4 in. x 7/8 in. Masonry Grinding Disc with Type 27 Depressed Center',1,1,1,3.97,3.97,3.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/DIABLO-4-1-2-in-x-1-4-in-x-7-8-in-Masonry-Grinding-Disc-with-Type-27-Depressed-Center-DBD045250701C/202831032','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3M 0.94 in. x 60.1 Yds. Multi-Surface Contractor Grade Tan Masking Tape (1 Roll)',1,1,1,2.48,2.48,2.48,1,'шт.',NULL,0,'https://www.homedepot.com/p/3M-0-94-in-x-60-1-Yds-Multi-Surface-Contractor-Grade-Tan-Masking-Tape-1-Roll-2020-24AP/100207133','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Charlotte Pipe 3/4 in. PVC Schedule. 40 90° S x S Elbow Fitting',1,1,1,0.79,0.79,0.79,1,'шт.',NULL,0,'https://www.homedepot.com/p/Charlotte-Pipe-3-4-in-PVC-Schedule-40-90-S-x-S-Elbow-Fitting-PVC023000800HD/203812123','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Carlon 3/4 in. 90-Degree Schedule 40 Standard Radius Belled End Elbow',1,1,1,1.31,1.31,1.31,1,'шт.',NULL,0,'https://www.homedepot.com/p/Carlon-3-4-in-90-Degree-Schedule-40-Standard-Radius-Belled-End-Elbow-UA9AEB-CTN/202304074','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('RYOBI ONE+ 18V Cordless 4-1/2 in. Angle Grinder (Tool Only)',1,1,1,59.97,59.97,59.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/RYOBI-ONE-18V-Cordless-4-1-2-in-Angle-Grinder-Tool-Only-PCL445B/318696913','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Husky 12 ft. Tape Measure',1,1,1,5.97,5.97,5.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-16-ft-Tape-Measure-90647/320510417','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Commercial Electric 3/4 in. Electrical Metallic Tubing (EMT) Set-Screw Coupling (25-Pack)',1,1,5,2.84,0.57,2.84,5,'шт.',NULL,0,'https://www.homedepot.com/p/Commercial-Electric-3-4-in-Electrical-Metallic-Tubing-EMT-Set-Screw-Coupling-25-Pack-FEPSS-75-25/316098066','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Halex (Brand Rating: 4.5/5) 3/4 in. Electrical Metallic Tube (EMT) Pull Elbow',1,1,2,9.12,4.56,9.12,2,'шт.',NULL,0,'https://www.homedepot.com/p/Halex-3-4-in-Electrical-Metallic-Tube-EMT-Pull-Elbow-14407/100111131','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Milwaukee INKZALL Blue Fine Point Jobsite Permanent Marker',1,1,1,0.98,0.98,0.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Milwaukee-INKZALL-Blue-Fine-Point-Jobsite-Permanent-Marker-48-22-3180/205984569','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Empire 36 in. Aluminum Straight Edge Ruler',1,1,1,6.97,6.97,6.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/Empire-36-in-Aluminum-Straight-Edge-Ruler-403/100185157','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Anvil 18 mm and 9 mm Snap-Off Knife Set (2-Piece)',1,1,2,2.97,1.49,2.97,2,'шт.',NULL,0,'https://www.homedepot.com/p/Anvil-18-mm-and-9-mm-Snap-Off-Knife-Set-2-Piece-86-212-0111/303711777','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('30 in. W x 36 in. H Rectangular Frameless Beveled Edge Wall Bathroom Vanity Mirror in Silver',1,1,2,15.2,7.6,15.2,2,'шт.',NULL,0,'https://www.homedepot.com/p/Glacier-Bay-30-in-W-x-36-in-H-Rectangular-Frameless-Beveled-Edge-Wall-Bathroom-Vanity-Mirror-in-Silver-81176/316331996','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('FOAMULAR 1/2 in. x 4 ft. x 8 ft. R-3 Square Edge Rigid Foam Board Insulation Sheathing',1,1,2,41,20.5,41,2,'шт.',NULL,0,'https://www.homedepot.com/p/Owens-Corning-FOAMULAR-1-2-in-x-4-ft-x-8-ft-R-3-Square-Edge-Rigid-Foam-Board-Insulation-Sheathing-36L/100320356','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/16 in. x 1-1/4 in. Zinc-Plated Fender Washer',1,1,30,5.4,0.18,5.4,30,'шт.',NULL,0,'https://www.homedepot.com/p/Everbilt-3-16-in-x-1-1-4-in-Zinc-Plated-Fender-Washer-100-Pack-804780/204276326','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('LSTA 1-1/4 in. x 9 in. 20-Gauge Galvanized Strap Tie',1,1,4,3.52,0.88,3.52,4,'шт.',NULL,0,'https://www.homedepot.com/p/Simpson-Strong-Tie-LSTA-1-1-4-in-x-9-in-20-Gauge-Galvanized-Strap-Tie-LSTA9/202255804','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('7 in. Polycast Rafter Square',1,1,1,5.47,5.47,5.47,1,'шт.',NULL,0,'https://www.homedepot.com/p/Empire-7-in-Polycast-Rafter-Square-296/100154430','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/4 in. x 10 ft. Electric Metallic Tube (EMT) Conduit',1,1,2,19.96,9.98,19.96,2,'шт.',NULL,0,'https://www.homedepot.com/p/3-4-in-x-10-ft-Electric-Metallic-Tube-EMT-Conduit-853429/100400406','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('1 in. x 3 in. x 8 ft. Furring Strip Board',1,1,7,13,1.86,13,7,'шт.',NULL,0,'https://www.homedepot.com/p/1-in-x-3-in-x-8-ft-Furring-Strip-Board-164704/100094214','The Home Depot',TO_DATE('12.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Parent Grip White Door Knob Covers (4-Pack)',1,1,4,4.48,1.12,2.24,2,'шт.',-2,-2.24,'https://www.homedepot.com/p/Safety-1st-Parent-Grip-White-Door-Knob-Covers-4-Pack-HS326/309495997','The Home Depot',TO_DATE('10.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Brandywine Stainless Steel Hall/Closet Door Knob',1,1,1,8,8,0,NULL,'шт.',NULL,0,'https://www.homedepot.com/p/Defiant-Brandywine-Stainless-Steel-Hall-Closet-Door-Knob-32T8630B/323700939','The Home Depot',TO_DATE('10.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('WD-40 Multi-Use Product with Smart Straw Sprays 2 Ways, 8 OZ [6-Pack]',1,1,6,29.12,4.85,24.27,5,'шт.',1,4.85,'https://www.amazon.com/gp/product/B01ECA64U8/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1','Amazon',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Clorox No-Splash Regular Liquid Bleach',1,1,117,7.52,0.06,7.52,117,'fl. OZ',NULL,0,'https://www.heb.com/product-detail/clorox-no-splash-regular-liquid-bleach-117-oz/3569028','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Febreze Fabric Refresher Spray - Downy April Fresh Scent',1,1,1,5.48,5.48,5.48,1,'шт.',NULL,0,'https://www.heb.com/product-detail/febreze-fabric-refresher-spray-downy-april-fresh-nbsp-scent-23-6-oz/1807764','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('H‑E‑B Tru Grit Toilet Bowl Cleaner with Bleach',1,1,2,3.96,1.98,3.96,2,'шт.',NULL,0,'https://www.heb.com/product-detail/h-e-b-tru-grit-toilet-bowl-cleaner-with-bleach-24-oz/2161615','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Lysol Power Clinging Gel Toilet Bowl Cleaner Value Pack',1,1,2,4.97,2.48,4.97,2,'шт.',NULL,0,'https://www.heb.com/product-detail/lysol-power-clinging-gel-toilet-bowl-cleaner-value-pack-48-oz/1358484','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Scrub Daddy Scratch Free Cleaning Tool',1,1,1,3.98,3.98,3.98,1,'шт.',NULL,0,'https://www.heb.com/product-detail/scrub-daddy-scratch-free-cleaning-tool-each/1924930','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Comet Scratch Free Powder Cleanser with Bleach',1,1,5,5.4,1.08,5.4,5,'шт.',NULL,0,'https://www.heb.com/product-detail/comet-scratch-free-powder-cleanser-with-bleach-21-oz/612095','HEB',TO_DATE('31.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('LSTA 1-1/4 in. x 24 in. 20-Gauge Galvanized Strap Tie',1,1,6,14.28,2.38,2.38,1,'шт.',5,11.9,'https://www.homedepot.com/p/Simpson-Strong-Tie-LSTA-1-1-4-in-x-24-in-20-Gauge-Galvanized-Strap-Tie-LSTA24/100375306','The Home Depot',TO_DATE('19.10.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Home Defense Max Insect Killer Ready-to-Use Spray',1,1,1,5.97,5.97,5.97,NULL,'шт.',1,5.97,'https://www.homedepot.com/p/Ortho-24-oz-Home-Defense-Max-Insect-Killer-Ready-to-Use-Spray-0195610/203687638','The Home Depot',TO_DATE('04.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('32 oz. 3,500 sq. ft. All Season Ready-to-Spray Concentrate Lawn Fertilizer',1,1,32,10.47,0.33,10.47,32,'fl. OZ',NULL,0,'https://www.homedepot.com/p/Vigoro-32-oz-3-500-sq-ft-All-Season-Ready-to-Spray-Concentrate-Lawn-Fertilizer-Hg-52512-3/204706227','The Home Depot',TO_DATE('04.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Smart Seed Texas Bermudagrass 1.75 lb. 1,000 sq. ft. Grass Seed and Lawn Fertilizer',1,1,1,14.97,14.97,0,NULL,'шт.',NULL,14.97,'https://www.homedepot.com/p/Pennington-Smart-Seed-Texas-Bermudagrass-1-75-lb-1-000-sq-ft-Grass-Seed-and-Lawn-Fertilizer-100543736/314933305','The Home Depot',TO_DATE('04.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('10 lbs. Picture Hanger Value Pack (50-Pack)',1,1,50,8.93,0.18,-0.07,NULL,'шт.',50,0,'https://www.homedepot.com/p/OOK-30-lbs-Picture-Hanger-Value-Pack-40-Pack-534281/301942179','The Home Depot',TO_DATE('02.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Zinc Plated Steel Round Bend Screw Hooks (15-Pack)',1,1,15,7.68,0.02,7.56,9,'шт.',6,-0.12,'https://www.homedepot.com/p/Prime-Line-5-16-in-x-4-1-2-in-Zinc-Plated-Steel-Round-Bend-Screw-Hooks-10-Pack-9068625/311949895','The Home Depot',TO_DATE('02.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Wooden Rod 48 in. x 0.75 in. Sanded Hardwood Ready for Finishing',1,1,3,11.01,3.67,0,NULL,'шт.',3,11.01,'https://www.homedepot.com/p/Everbilt-Multi-Color-Push-Pin-Assortment-120-Pack-810742/204769015','The Home Depot',TO_DATE('02.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Multi-Color Push Pin Assortment (120-Pack)',1,1,120,2.73,0.02,2.73,120,'шт.',NULL,0,'https://www.homedepot.com/p/Everbilt-Multi-Color-Push-Pin-Assortment-120-Pack-810742/204769015','The Home Depot',TO_DATE('02.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('#18 x 5/8 in. Zinc-Plated Wire Nails',1,1,50,2.18,0.04,2.18,50,'шт.',NULL,0,'https://www.homedepot.com/p/18-x-5-8-in-Zinc-Plated-Wire-Nails-801194/204274053','The Home Depot',TO_DATE('02.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Spring Clamp Set (22-Piece) Anvil',1,1,23,9.97,0.43,9.97,23,'шт.',NULL,0,'https://www.homedepot.com/p/Anvil-Spring-Clamp-Set-22-Piece-TGS0199A/302755768','The Home Depot',TO_DATE('01.02.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Vertical Louver (8 Pack) 3.5 in W x 84 in. L (Actual Size 3.5 in W x 82.5 in. L )',1,1,8,22.48,2.81,22.48,8,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-Folding-Lock-Back-Utility-Knife-Red-with-10-Blades-99979/311920676','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Folding Lock-Back Utility Knife, Red with 10-Blades',1,1,1,8.97,8,8,1,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-Folding-Lock-Back-Utility-Knife-Red-with-10-Blades-99979/311920676','The Home Depot',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('24 in. 13-Pocket Black Canvas Tool Work Apron',1,1,1,11.98,11.98,11.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-24-in-13-Pocket-Black-Canvas-Tool-Work-Apron-HD00121-TH/310338131','The Home Depot',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Ferris Single Hole Single-Handle Bathroom Faucet in Spot Defense Brushed Nickel',1,1,1,44,44,44,1,'шт.',NULL,0,NULL,'The Home Depot',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/8 in. Compression x 1/2 in. FIP x 16 in. Braided Polymer Faucet Supply Line',1,1,2,13.94,6.97,13.94,2,'шт.',NULL,0,'https://www.homedepot.com/p/BrassCraft-3-8-in-Compression-x-1-2-in-FIP-x-16-in-Braided-Polymer-Faucet-Supply-Line-B1-16A-F/100056596','The Home Depot',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Tire gauge sli 20036',1,1,1,6.29,6.29,6.29,1,'шт.',NULL,0,NULL,'OReilly',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Silicone ver 78009',1,1,1,7.99,7.99,7.99,1,'шт.',NULL,0,NULL,'OReilly',TO_DATE('30.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('1/2 in. MNPT x 3/4 in. FHT PVC Swivel',1,1,1,2.56,2.56,2.56,1,'шт.',NULL,0,'https://www.homedepot.com/p/Orbit-1-2-in-MNPT-x-3-4-in-FHT-PVC-Swivel-53365/202257138','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('ONE+ 18V Cordless Wet/Dry Hand Vacuum (Tool Only)',1,1,1,49.97,49.97,49.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/RYOBI-ONE-18V-Cordless-Wet-Dry-Hand-Vacuum-Tool-Only-PCL702B/319962861','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('1/2 in. x 260 in. Thread Sealing PTFE Plumber''s Tape',1,1,1,0.98,0.98,0.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Harvey-1-2-in-x-260-in-Thread-Sealing-PTFE-Plumber-s-Tape-0178503/202280370','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/4 in. MIP Brass No-Kink Hose Bibb Valve',1,1,1,10.65,10.65,10.65,1,'шт.',NULL,0,'https://www.homedepot.com/p/Everbilt-3-4-in-MIP-Brass-No-Kink-Hose-Bibb-Valve-102-304EB/205821746','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('1/2 in. x 3/4 in. Brass Sweat or FTG Hose Bibb',1,1,1,6.25,6.25,6.25,1,'шт.',NULL,0,'https://www.homedepot.com/p/Everbilt-1-2-in-x-3-4-in-Brass-Sweat-or-FTG-Hose-Bibb-102-534EB/205821848','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/8 in. Compression x 7/8 in. Ballcock Nut x 12 in. Braided Polymer Toilet Supply Line',1,1,1,4.68,4.68,4.68,1,'шт.',NULL,0,'https://www.homedepot.com/p/BrassCraft-3-8-in-Compression-x-7-8-in-Ballcock-Nut-x-12-in-Braided-Polymer-Toilet-Supply-Line-B1-12DL-F/100094502','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('60-Watt Equivalent A19 Dimmable CEC Title 20 ENERGY STAR 90+ CRI E26 Medium Base LED Light Bulb, Daylight 5000K (4-Pack)',1,1,4,11.48,2.87,11.48,4,'шт.',NULL,0,'https://www.homedepot.com/p/Feit-Electric-60-Watt-Equivalent-A19-Dimmable-CEC-Title-20-ENERGY-STAR-90-CRI-E26-Medium-Base-LED-Light-Bulb-Daylight-5000K-4-Pack-OM60DM-950CA-4/304116331','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3/4 in. Plastic Water Pressure Test Gauge',1,1,1,12.74,12.74,12.74,1,'шт.',NULL,0,'https://www.homedepot.com/p/Watts-3-4-in-Plastic-Water-Pressure-Test-Gauge-DP-IWTG/100175467','The Home Depot',TO_DATE('29.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('5/8 in. Dia. x 100 ft. Heavy-Duty Commercial Grade Water Hose',1,1,1,69.98,69.98,69.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/fitt-5-8-in-Dia-x-100-ft-Heavy-Duty-Commercial-Grade-Water-Hose-FFP55800/314440026','The Home Depot',TO_DATE('28.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('150 ft. Hose Hangout',1,1,2,13.96,6.98,13.96,2,'шт.',NULL,0,'https://www.homedepot.com/p/Suncast-150-ft-Hose-Hangout-CPLHH15000/100121089','The Home Depot',TO_DATE('28.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Melnor  Rear-Trigger 7-Pattern Nozzle',1,1,1,6.98,6.98,6.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Melnor-Rear-Trigger-7-Pattern-Nozzle-67000HD/314333719','The Home Depot',TO_DATE('28.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('FOLEX CARPET SPOT REMOVER 36OZ',1,1,36,7,0.19,7,36,'fl. OZ',NULL,0,NULL,'The Home Depot',TO_DATE('11.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('ZEP PREMIUM CARPET CLEANER 128OZ',1,1,128,13,0.1,13,128,'fl. OZ',NULL,0,NULL,'The Home Depot',TO_DATE('11.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('1200gsm 600gsm microfiber auto quick drying thickened car cleaning towel 40*40',1,1,50,30,0.6,30,50,'шт.',NULL,0,'https://x.alibaba.com/AvYfHO?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Custom Microfiber Cloth 30x30 Grey',1,1,150,55.1,0.37,54.73,149,'шт.',-1,-0.37,'https://x.alibaba.com/AvYfD6?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Custom Microfiber Cloth 30x30 Blu',1,1,150,55.1,0.37,54.73,149,'шт.',-1,-0.37,'https://x.alibaba.com/AvYfD6?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Custom Microfiber Cloth 30x30 Yellow',1,1,150,55.1,0.37,54.73,149,'шт.',-1,-0.37,'https://x.alibaba.com/AvYfD6?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Custom Microfiber Cloth 30x30 Green',1,1,150,55.1,0.37,54.73,149,'шт.',-1,-0.37,'https://x.alibaba.com/AvYfD6?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Custom Microfiber Cloth 30x30 Red',1,1,150,55.1,0.37,54.73,149,'шт.',-1,-0.37,'https://x.alibaba.com/AvYfD6?ck=pdp','Alibaba',TO_DATE('23.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Drain Weasel Hair Clog Tool Starter Kit for Drain Cleaning (3-Piece)',1,1,1,3.98,3.98,3.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/FlexiSnake-Drain-Weasel-Hair-Clog-Tool-Starter-Kit-for-Drain-Cleaning-3-Piece-DWPSK2/203483754','The Home Depot',TO_DATE('21.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('4-1/2 lbs. Splitting maul 35 in. Fiberglass Handle',1,1,1,39.98,39.98,39.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-4-1-2-lbs-Splitting-maul-35-in-Fiberglass-Handle-35297/323070140','The Home Depot',TO_DATE('21.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Brinks Zinc Diecast 40mm Combination Sport Padlock with 1 3/16in Shackle',1,1,1,9.97,9.97,9.97,1,'шт.',NULL,0,'https://www.walmart.com/ip/Brinks-Zinc-Diecast-40mm-Combination-Sport-Padlock-with-1-3-16in-Shackle/16783286?from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hyper Tough 67402-26 Leather Palm Glove, Large, Multi-Use Work Glove',1,1,2,4.88,2.44,4.88,2,'шт.',NULL,0,'https://www.walmart.com/ip/Hyper-Tough-67402-26-Leather-Palm-Glove-Large-Multi-Use-Work-Glove/187118078?athbdg=L1102&from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hyper Tough Assorted 4 inch and 8 inch Cable Ties 650Pcs',1,1,650,10.17,0.02,10.17,650,'шт.',1,0.02,'https://www.walmart.com/ip/Hyper-Tough-Assorted-4-inch-and-8-inch-Cable-Ties-650Pcs/460543628?from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HT 3 OL 2FT PWR STRP FABRIC',1,1,1,4.98,4.98,4.98,1,'шт.',NULL,0,'https://www.walmart.com/ip/HT-3-OL-2FT-PWR-STRP-FABRIC/931782089?athbdg=L1600&from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hyper Tough 500-Piece Zinc Plated Steel Screw and Anchor',1,1,500,11.97,0.02,11.97,500,'шт.',NULL,0,'https://www.walmart.com/ip/Hyper-Tough-500-Piece-Zinc-Plated-Steel-Screw-and-Anchor-Fastener-Assortment-with-Case-5532/417034609?athbdg=L1600&from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Mega Clamp Yellow/black, MC 0903',1,1,1,4.63,4.63,4.63,1,'шт.',NULL,0,'https://www.walmart.com/ip/Mega-Clamp-Yellow-black-MC-0903/972296565?athbdg=L1600&from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HART 24in Rolling Tool Box, Portable Black Resin Toolbox',1,1,1,47.88,47.88,47.88,1,'шт.',NULL,0,'https://www.walmart.com/ip/HART-24in-Rolling-Tool-Box-Portable-Black-Resin-Toolbox/735517326?athbdg=L1600&from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('White Vertical Blinds Head Rail for Sliding Doors or Windows - 78 in. W',1,1,1,50.86,50.86,50.86,1,'шт.',NULL,0,'https://www.homedepot.com/p/Hampton-Bay-White-Vertical-Blinds-Head-Rail-for-Sliding-Doors-or-Windows-78-in-W-10793478808564/300147149','The Home Depot',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Husky 6lb FG DHL SLEDGE HAMMER',1,1,1,32.45,32.45,32.45,1,'шт.',NULL,0,'https://www.homedepot.com/p/Husky-6-lbs-FiberGlass-hdl-Sledge-Hammer-MD-6F-HD/322786042','The Home Depot',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Captain Jack''s 16 oz Rose Rx Ready-to-Use Spray for Insect, Fungus, Mite and Nematode Control',1,1,32,5.24,0.16,5.24,32,'fl. OZ',NULL,0,'https://www.walmart.com/ip/Captain-Jack-s-16-oz-Rose-Rx-Ready-to-Use-Spray-for-Insect-Fungus-Mite-and-Nematode-Control/528635768?from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Libman Whisk Broom with Dust Pan -Red',1,1,1,5.97,5.97,5.97,1,'шт.',NULL,0,'https://www.walmart.com/ip/Libman-Whisk-Broom-with-Dust-Pan-Red/750854492?from=/search','Walmart',TO_DATE('07.01.2024', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Roman Scraper with Roller',1,1,1,11.98,11.98,11.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Roman-Scraper-with-Roller-202233/323871558','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HDX 33-39 Gal. Black Heavy Duty Drawstring Trash Bags',1,1,50,15.97,0.3,15.97,50,'шт.',NULL,0,'https://www.homedepot.com/p/HDX-33-39-Gal-Black-Heavy-Duty-Drawstring-Trash-Bags-50-Count-For-Outdoor-and-Yard-Waste-HDX3339/306713149','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('RYOBI 6 in. Buffing Wheel Set (3-Piece)',1,1,1,19.97,19.97,19.97,1,'шт.',NULL,0,'https://www.homedepot.com/p/RYOBI-6-in-Buffing-Wheel-Set-3-Piece-A92301/319700937','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Goof Off 128 fl. oz.',1,1,128,25.96,0.2,25.96,128,'fl. OZ',NULL,0,'https://www.homedepot.com/p/Goof-Off-128-fl-oz-Professional-Strength-Latex-Paint-and-Adhesive-Remover-FG657/100671754','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Fabuloso Lavender 2x Concentrated All-Purpose Cleaner',1,1,128,9.98,0.08,8.06,104,'fl. OZ',-24,-1.92,'https://www.homedepot.com/p/Fabuloso-128-oz-Fabuloso-Lavender-2x-Concentrated-All-Purpose-Cleaner-61037884/325338360','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HDX 32oz. Empty Spray Bottle',1,1,1,2.68,2.68,2.68,1,'шт.',NULL,0,'https://www.homedepot.com/p/HDX-32oz-Empty-Spray-Bottle-HDX32102/320063601','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('RYOBI 2.6 Amp Corded 5 in. Random Orbital Sander',1,1,1,69,69,69,1,'шт.',NULL,0,'https://www.homedepot.com/p/RYOBI-2-6-Amp-Corded-5-in-Random-Orbital-Sander-RS290G/205105594','The Home Depot',TO_DATE('27.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Husky  42 Gal. Contractor Bags (50-Count)',1,1,50,29.97,0.6,29.97,50,'шт.',25,15,'https://www.homedepot.com/p/Husky-42-Gal-Contractor-Bags-50-Count-HK42WC050B/202973825','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HDX  Yellow and Blue 11 mil Reusable Latex Kitchen and Bath Gloves (2-Pair)',1,1,4,7.96,1.99,5.97,3,'шт.',NULL,0,'https://www.homedepot.com/p/HDX-Yellow-and-Blue-11-mil-Reusable-Latex-Kitchen-and-Bath-Gloves-2-Pair-24125-012/319285480','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('BEHR DECKplus 1 gal',1,1,1,36.98,36.98,36.98,1,'Gal.',NULL,0,'https://www.homedepot.com/p/BEHR-DECKplus-1-gal-Cedar-Naturaltone-Transparent-Waterproofing-Exterior-Wood-Finish-40101/100125017','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('4 in. Flat Chip Brush',1,1,1,4.27,4.27,4.27,1,'шт.',NULL,0,'https://www.homedepot.com/p/2-in-Flat-Chip-Brush-1500-2/100626098','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Anvil  47 in. L Wood Handle 14-Tines Garden Bow Rake',1,1,1,14.98,14.98,14.98,1,'шт.',NULL,0,'https://www.homedepot.com/p/Anvil-47-in-L-Wood-Handle-14-Tines-Garden-Bow-Rake-77105-943/314816653','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Husky d-handle poly scoop',1,1,1,34.98,34.98,34.98,1,'шт.',1,34.98,'https://www.homedepot.com/p/Husky-31-in-Wood-Handle-D-Grip-Plastic-Scoop-Shovel-77540-945/315067729','The Home Depot',TO_DATE('26.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('H‑E‑B Microfiber Extendable Duster',1,1,1,11,11,0,NULL,'шт.',NULL,0,'https://www.heb.com/product-detail/h-e-b-microfiber-extendable-duster-each/2751026','HEB',TO_DATE('13.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Febreze Fabric Refresher Spray - Sweet Peony',1,1,27,5,0.19,-0.13,NULL,'fl. OZ',NULL,0,'https://www.heb.com/product-detail/febreze-fabric-refresher-spray-sweet-peony-27-oz/9760508','HEB',TO_DATE('13.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Lysol Power Clinging Gel Toilet Bowl Cleaner Value Pack',1,1,96,6,0.06,6,96,'fl. OZ',NULL,0,'https://www.heb.com/product-detail/lysol-power-clinging-gel-toilet-bowl-cleaner-value-pack-48-oz/1358484','HEB',TO_DATE('13.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Fabuloso Lavender Complete Multi-Purpose Cleaner',1,1,48,5,0.1,0.2,NULL,'fl. OZ',NULL,0,'https://www.heb.com/product-detail/fabuloso-lavender-complete-multi-purpose-cleaner-48-oz/5650069','HEB',TO_DATE('13.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Premium Gloves',1,1,2,7.25,3.62,3.63,1,'шт.',NULL,0,'https://www.heb.com/product-detail/h-e-b-premium-reuseable-gloves-medium/2094578','HEB',TO_DATE('13.12.2023', 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Brasso-2660089334 Multi-Purpose Metal Polish, 8 oz',1,1,1,4.98,4.98,0,NULL,'шт.',NULL,0,'https://www.amazon.com/dp/B00D600PLA?ref_=cm_sw_r_apin_dp_S84S7EQ6W161QF7WNSJE&peakEvent=5&dealEvent=1&language=en-US','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('CLARK’S Natural Stone Wax',1,1,1,21.95,21.95,0,NULL,'шт.',NULL,0,'https://www.amazon.com/dp/B07N4ZDRRW?ref_=cm_sw_r_apin_dp_VV4WK8RFAWZJMVNEZYS1&peakEvent=5&dealEvent=1&language=en-US','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('APULITO Home Mold Stain Cleaning Gel Mildew Cleaner Gel for Bathroom Kitchen Household (5 Fl Oz (Pack of 3))',1,1,3,25.98,8.66,25.98,3,'шт.',1,8.66,'https://www.amazon.com/dp/B088BJXYHT?ref_=cm_sw_r_apin_dp_AN855AX6ESGW29KKQ3XA&peakEvent=5&dealEvent=1&language=en-US','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Method All-Purpose Cleaner Refill',1,1,408,49.74,0.12,40.02,327,'fl. OZ',64,7.68,'https://www.amazon.com/gp/product/B07KYG8NZR/ref=asin_title?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Clorox Toilet Bowl Cleaner with Bleach',1,1,8,56.9,7.11,56.9,8,'шт.',NULL,0,'https://www.amazon.com/dp/B0B1GVFVZM?ref_=cm_sw_r_apin_dp_0T67ZHHCHVX1AGJV7YRH&language=en-US','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('CLR PRO Calcium, Lime and Rust Remover',1,1,128,28.03,0.22,23.63,108,'fl. OZ',60,13.2,'https://www.amazon.com/dp/B000SKX0N6?ref_=cm_sw_r_apin_dp_6GBZNWG2C21SVAFPPCBQ&language=en-US&th=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Parkway Distributors Fabuloso Purple Lavender Scent Floor Cleaner',1,1,128,29.8,0.23,18.3,78,'fl. OZ',84,19.32,'https://www.amazon.com/dp/B0C8221VKD?ref_=cm_sw_r_apin_dp_9X73KVKBAYDCPJG3EN37&language=en-US','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Bravo Low Splash Regular Bleach',1,1,121,6.67,0.06,3.01,60,'fl. OZ',40,2.4,'https://www.heb.com/product-detail/h-e-b-bravo-low-splash-regular-bleach-121-oz/5848013','HEB',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Comet Scratch Free Powder Cleanser',1,1,8,8.96,1.12,-1.12,-1,'шт.',-2,-2.24,'https://www.heb.com/product-detail/comet-scratch-free-powder-cleanser-with-bleach-21-oz/612095','HEB',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Hill Country Fare Glass Cleaner',1,1,406,18.6,0.05,14.55,325,'fl. OZ',40,2,'https://www.heb.com/product-detail/hill-country-fare-glass-cleaner-with-ammonia-refill-67-6-oz/3394003','HEB',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Febreze Fabric Refresher Spray',1,1,7,39.9,5.7,39.9,7,'шт.',3,17.1,'https://www.heb.com/product-detail/febreze-fabric-refresher-spray-sweet-peony-27-oz/9760508','HEB',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Black Diamond Stoneworks MARBLE & TILE FLOOR CLEANER',1,1,128,34.98,0.27,18.51,67,'fl. OZ',40,10.8,'https://www.amazon.com/dp/B00J75IU6A?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('CleanSmart Toy Disinfectant Spray',1,1,2,15.28,7.64,15.28,2,'шт.',NULL,0,'https://www.amazon.com/gp/product/B011ANDC78/ref=asin_title?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('ANECO Compostable Trash Bags',1,1,100,17.6,0.18,17.6,100,'шт.',NULL,0,'https://www.amazon.com/gp/product/B095CGWKWW/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('75 Inch Wall Mop Wall Cleaning Mop',1,1,2,18.99,9.49,18.99,2,'шт.',1,9.49,'https://www.amazon.com/gp/product/B09K3FHSSZ/ref=ppx_yo_dt_b_asin_title_o00_s03?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Easy-Off Professional Oven & Grill Cleaner',1,1,6,44.98,7.5,37.48,5,'шт.',3,22.5,'https://www.amazon.com/gp/product/B0040ZORB4/ref=ppx_yo_dt_b_asin_title_o01_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('2Pack Pumice Stone Toilet Bowl Cleaning',1,1,2,6.99,3.5,6.99,2,'шт.',NULL,0,'https://www.amazon.com/gp/product/B0B7WZQ6ZJ/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('BETTER LIFE Bathroom Cleaner - Tea Tree Bathtub & Shower Cleaner',1,1,64,22.99,0.36,22.99,64,'fl. OZ',NULL,0,'https://www.amazon.com/gp/product/B07L4H21T6/ref=ppx_yo_dt_b_asin_title_o02_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Lysol Bleach Free Hydrogen Peroxide Toilet Bowl Cleaner',1,1,144,28.89,0.2,14.49,72,'fl. OZ',24,4.8,'https://www.amazon.com/gp/product/B0B9HYG6V8/ref=ppx_yo_dt_b_asin_title_o03_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('16.5FT Telescoping Ladder',1,1,1,329.99,329.99,329.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0CBPTY82H?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Ecover Toilet Bowl Cleaner',1,1,25,7,0.28,7,25,'fl. OZ',NULL,0,'https://www.amazon.com/gp/product/B004T33D3A/ref=ppx_yo_dt_b_asin_title_o03_s02?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Harper Live.Love.Clean. Household Cleaning Kit',1,1,2,67.81,33.91,67.81,2,'шт.',NULL,0,'https://www.amazon.com/gp/product/B09HJFWT5M/ref=ppx_yo_dt_b_asin_title_o03_s01?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HALOMOUNT Monitor Laptop Mount',1,1,1,44.29,44.29,44.29,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0C9GPF434?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('NPET K32 Wireless Gaming Keyboard',1,1,1,24.99,24.99,24.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B07GBSGTKV?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Monitoracer EK240Y C - 23.8" Monitor',1,1,1,100,100,100,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0BNHXRXLN?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('EZPRO USA Plastic Spray Bottle 24 Oz',1,1,3,11,3.67,11,3,'шт.',NULL,0,'https://www.amazon.com/dp/B08GZX1J27?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('MOUYAT 23 Inch Large Pick Mattock Hoe',1,1,1,27.99,27.99,27.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B09YXTYPSJ?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HANTOP Garden Shovel',1,1,1,28.99,28.99,28.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0C3VDDB5F?ref=ppx_yo2ov_dt_b_product_details&th=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('WD-40 Original Forumla',1,1,3,18.83,6.28,18.83,3,'шт.',3,18.84,'https://www.amazon.com/dp/B084CQ4R8L?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Eaasty 12 Pack Plastic Spray Bottles 25 oz',1,1,12,41.99,3.5,41.99,12,'шт.',NULL,0,'https://www.amazon.com/dp/B0CGV5TW6W?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('30 Inch Pre Streched Braiding Hair 3 Packs Synthetic Colored Braiding Hair',1,1,3,13.66,4.55,13.66,3,'шт.',NULL,0,'https://www.amazon.com/dp/B0C6M7T95Y?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Mytium Window Cleaning Squeegee Kit',1,1,1,17.99,17.99,17.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0BNB5NY87?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('2 Pcs Shower Squeegee for Shower Glass Doors',1,1,2,6.99,3.5,6.99,2,'шт.',NULL,0,'https://www.amazon.com/dp/B0BXB88KY6?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('NIIMBOT Label Maker Tape',1,1,1,11.99,11.99,11.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B09D744PTJ?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('PrinterNIIMBOT B21 Inkless Label Maker',1,1,1,65.99,65.99,65.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B09C1VWT2N?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('HTVRONT Vinyl Scraper',1,1,2,5.99,3,5.99,2,'шт.',NULL,0,'https://www.amazon.com/dp/B095VRHHP6?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('100 Pack Heavy Duty Cleaning Scrub Sponges for Kitchen Abrasive Scrubber Sponge',1,1,100,28.99,0.29,28.99,100,'шт.',NULL,0,'https://www.amazon.com/dp/B0BNBZPMPT?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Housmile Mesh File Folder Organizer',1,1,2,35.99,18,35.99,2,'шт.',NULL,0,'https://www.amazon.com/dp/B0C9C2Q13B?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Baseboard Buddy – Baseboard & Molding Cleaning Tool',1,1,2,24.99,12.49,24.99,2,'шт.',2,24.98,'https://www.amazon.com/dp/B01C6X37Q2?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('MR.SIGA Microfiber Cleaning Cloth',1,1,50,24.99,0.5,24.99,50,'шт.',NULL,0,'https://www.amazon.com/dp/B07XT288Y5?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('MR.SIGA Ultra Fine Microfiber Cloths for Glass',1,1,12,19.99,1.67,19.99,12,'шт.',NULL,0,'https://www.amazon.com/dp/B071JLQ387?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Simple Deluxe Heavy Duty 5-Shelf Shelving Unit 1250Lb Capacity',1,1,5,312,62.4,312,5,'шт.',NULL,0,'https://www.amazon.com/dp/B0BCP5MM6K?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Reli. SuperValue 33 Gallon Trash Bags',1,1,50,13.99,0.28,13.99,50,'шт.',NULL,0,'https://www.amazon.com/dp/B0B7Q8BPZL?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Electric Car Jack Kit 5Ton 12V Hydraulic Car Jack Lift',1,1,1,102.99,102.99,102.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0CHYMDBCS?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('3 Inch Car Headlight Restoration Kit',1,1,3,16.99,5.66,16.99,3,'шт.',NULL,0,'https://www.amazon.com/dp/B09B3BKHF1?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Meguiar''s PlastX Clear Plastic Polish',1,1,10,7.64,0.76,7.64,10,'fl. OZ',NULL,0,'https://www.amazon.com/dp/B0000AY3SR?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Gonhom Par38 LED Flood Light Bulbs Outdoor 2 Pack',1,1,2,16.99,8.49,16.99,2,'шт.',NULL,0,'https://www.amazon.com/dp/B09WZRR67F?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Trowbridge''s Grafting Wax 8 oz. WALTER E. CLARK & SON',1,1,8,4.24,0.53,4.24,8,'шт.',NULL,0,'https://www.amazon.com/dp/B000NCTJCU?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('KURUI Heavy Duty Hollow Wall Anchors for Drywall',1,1,1,9.99,9.99,9.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B09V32CKNJ?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('LED Light Bulbs, 100 Watt Equivalent A19',1,1,4,9.99,2.5,9.99,4,'шт.',NULL,0,'https://www.amazon.com/dp/B09YQ4197R?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Campfun 899PCS Tool Set, Household Tool Kit with Rolling Tool Bo',1,1,1,89.99,89.99,89.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0C5RY13FZ?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('MZK 20V Pole Pruning Shear',1,1,1,129.99,129.99,129.99,1,'шт.',NULL,0,'https://www.amazon.com/dp/B0C2CK7Y3T?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('PowerSmart 40V MAX Cordless Leaf Blower with 4.0Ah Battery and Charger',1,1,2,119.98,59.99,119.98,2,'шт.',NULL,0,'https://www.amazon.com/dp/B09YPW6K8K?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('PowerSmart 40V MAX 17-Inch Cordless Lawn Mower & Leaf Blower Combo Kit',1,1,1,329.99,329.99,329.99,1,'шт.',NULL,0,'https://www.amazon.com/gp/product/B0BJTYWZ9Q/ref=ppx_yo_dt_b_asin_title_o09_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Maryton Pumice Stone for Toilet Cleaning',1,1,16,16.99,1.06,16.99,16,'шт.',NULL,0,'https://www.amazon.com/gp/product/B07XDDQGLP/ref=ppx_yo_dt_b_asin_title_o00_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('naturally It''s clean Floor Cleaner (Makes 24-Gallons) for All Floor Types (Plant Based Enzymes) pH Neutral',1,1,24,9.99,0.42,9.99,24,'fl. OZ',NULL,0,'https://www.amazon.com/gp/product/B007P5YZ36/ref=ppx_yo_dt_b_asin_title_o00_s00?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Rubber Broom for Pet Hair Carpet Rake with Squeegee',1,1,1,13.59,13.59,13.59,1,'шт.',NULL,0,'https://www.amazon.com/gp/product/B0BWN3S52D/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Aunt Fannie''s Glass & Window Cleaning Vinegar Wash',1,1,3,22.99,7.66,22.99,3,'шт.',NULL,0,'https://www.amazon.com/gp/product/B08YP7CM52/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Multi-Purpose Antibacterial Spray',1,1,2,11.99,6,11.99,2,'шт.',NULL,0,'https://www.amazon.com/gp/product/B0CBVZW81W/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Therapy Stove Top Cleaner and Cooktop Cleaner Kit - Glass Stove Top Cleaner and Polish for Ceramic, Electric, Induction and Flat Top Surfaces',1,1,32,19.95,0.62,19.95,32,'fl. OZ',NULL,0,'https://www.amazon.com/gp/product/B098G5M4ZS/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Baseboard Cleaner Tool with Long Handle',1,1,2,22.48,11.24,22.48,2,'шт.',NULL,0,'https://www.amazon.com/gp/product/B0C7Q2XXMN/ref=ppx_yo_dt_b_asin_title_o00_s02?ie=UTF8&psc=1','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));
INSERT INTO equipment(naming,type_id,responsible_id,amount,total,price4each,total_left,amount_left,unit,given_amount,given_total,link,source,supply_date) VALUES ('Zep Foaming Wall Cleaner',1,1,6,20.21,3.37,16.84,5,'шт.',2,6.74,'https://www.amazon.com/dp/B07DX79815?psc=1&ref=ppx_yo2ov_dt_b_product_details','Amazon',TO_DATE(NULL, 'DD.MM.YYYY'));

