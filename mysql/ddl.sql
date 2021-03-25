use nextDB;

drop table if exists daily_table;

create table if not exists daily_table(
	date Date unique not null,
	open double not null,
    high double not null,
    low double not null,
    close double not null,
    volume int not null,
    constraint pk_date primary key(date)
);



