delete from Reservation_Cabin;
delete from Reservation_Person;

delete from Reservation;

delete from Hailing_Frequency;
delete from Person;
delete from Address;
delete from Cabin;
delete from Ship;
delete from Voyage;

insert into Planet(id, name) values (1, 'Earth');
insert into Planet(id, name) values (2, 'Vulcan');
insert into Planet(id, name) values (3, 'Planet 9');
insert into Planet(id, name) values (4, 'Mars');

insert into Ship(id, name, ship_class) values (1, 'USS Enterprise', 'Constitution');
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (1, 1, 2, 29900, 'USD', 1);
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (2, 99, 1, 5900, 'USD', 1);

insert into Ship(id, name, ship_class) values (2, 'USS Billings', 'Constellation');
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (3, 1, 2, 2900, 'USD', 2);
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (4, 9, 1, 1900, 'USD', 2);

insert into Ship(id, name, ship_class) values (3, 'Starship', 'First');
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (5, 1, 1, 9999, 'USD', 3);
insert into Cabin(id, deck_level, bed_count, price, currency, ship_id) values (6, 1, 1, 9999, 'USD', 3);

insert into Address(id, street, city, planet_id) values (1, 'Market Street', 'San Francisco', 1);
insert into Address(id, street, city, planet_id) values (2, 'Maple Road', 'Mayberry', 1);
insert into Address(id, street, city, planet_id) values (3, 'Pennsylvania Ave', 'Washington DC', 1);

insert into Person(id, name, type, home_address_id, ship_id) values (1, 'James T. Kirk', 'CAPTAIN', 1, 1);
insert into Hailing_Frequency(frequency, person_id) values ('Kirk', 1);
insert into Hailing_Frequency(frequency, person_id) values ('Captain', 1);

insert into Person(id, name, type, home_address_id) values (2, 'Aunt B', 'PASSENGER', 2);

insert into Voyage(id, departure_date, destination_date, departure_planet_id, destination_planet_id, ship_id) values (1, '2019-09-27', '2019-09-30', 1, 2, 1);
insert into Voyage(id, departure_date, destination_date, departure_planet_id, destination_planet_id, ship_id) values (2, '2019-10-03', '2019-10-07', 2, 1, 1);

insert into Reservation(id, voyage_id) values (1, 1);
insert into Reservation(id, voyage_id) values (2, 2);
insert into Reservation_Person(reservation_id, person_id) values (1, 2);
insert into Reservation_Person(reservation_id, person_id) values (2, 2);

insert into Voyage(id, departure_date, destination_date, departure_planet_id, destination_planet_id, ship_id) values (3, '2020-09-27', '2020-09-30', 1, 4, 3);
insert into Voyage(id, departure_date, destination_date, departure_planet_id, destination_planet_id, ship_id) values (4, '2020-10-03', '2020-10-07', 4, 1, 3);

insert into Reservation(id, voyage_id) values (3, 3);
insert into Reservation(id, voyage_id) values (4, 4);
insert into Reservation_Person(reservation_id, person_id) values (3, 2);
insert into Reservation_Person(reservation_id, person_id) values (4, 2);