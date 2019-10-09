
-- No sales on the weekends
insert into dim_date(id, fiscal_date) values (1, '2020-01-06');
insert into dim_date(id, fiscal_date) values (2, '2020-01-07');
insert into dim_date(id, fiscal_date) values (3, '2020-01-08');
insert into dim_date(id, fiscal_date) values (4, '2020-01-09');
insert into dim_date(id, fiscal_date) values (5, '2020-01-10');
insert into dim_date(id, fiscal_date) values (6, '2020-01-13');
insert into dim_date(id, fiscal_date) values (7, '2020-01-14');
insert into dim_date(id, fiscal_date) values (8, '2020-01-15');
insert into dim_date(id, fiscal_date) values (9, '2020-01-16');
insert into dim_date(id, fiscal_date) values (10, '2020-01-17');

insert into dim_store(id, store_number) values (1, '140');
insert into dim_store(id, store_number) values (2, '283');
insert into dim_store(id, store_number) values (3, '350');
insert into dim_store(id, store_number) values (4, '454');
insert into dim_store(id, store_number) values (5, '500');

insert into dim_product(id, product_name) values (1, 'Widget');
insert into dim_product(id, product_name) values (2, 'Gadget');
insert into dim_product(id, product_name) values (3, 'Stuff');
insert into dim_product(id, product_name) values (4, 'Thingamajig');

--Monday
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 1, 1, 10);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 1, 2, 10);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 1, 3, 10);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 1, 4, 10);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 2, 1, 20);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 2, 2, 20);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 2, 3, 20);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 2, 4, 20);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 3, 1, 30);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 3, 2, 30);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 3, 3, 30);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 3, 4, 30);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 4, 1, 40);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 4, 2, 40);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 4, 3, 40);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 4, 4, 40);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 5, 1, 50);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 5, 2, 50);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 5, 3, 50);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (1, 5, 4, 50);

-- Tuesday
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 1, 1, 110);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 1, 2, 110);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 1, 3, 110);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 1, 4, 110);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 2, 1, 120);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 2, 2, 120);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 2, 3, 120);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 2, 4, 120);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 3, 1, 130);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 3, 2, 130);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 3, 3, 130);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 3, 4, 130);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 4, 1, 140);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 4, 2, 140);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 4, 3, 140);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 4, 4, 140);

insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 5, 1, 150);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 5, 2, 150);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 5, 3, 150);
insert into fact_sales(date_id, store_id, product_id, units_sold) values (2, 5, 4, 150);
