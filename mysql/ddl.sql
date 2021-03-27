create database if not exists next;

use next;

drop table if exists daily_table;
drop table if exists ticker_table;

create table if not exists ticker_table(
	ticker varchar(6) unique not null,
    constraint pk_ticker primary key(ticker)
);

create table if not exists daily_table(
	ticker varchar(6) not null,
	date Date not null,
	open double not null,
    high double not null,
    low double not null,
    close double not null,
    volume bigint unsigned not null,
    constraint pk_ticker_date primary key(ticker, date),
    constraint fk_ticker foreign key(ticker) references ticker_table(ticker) on update cascade on delete cascade
);

select * from ticker_table;
select * from daily_table;
select count(*) from daily_table;