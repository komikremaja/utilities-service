CREATE TABLE utilities_npwp_data(
	id_npwp INT AUTO_INCREMENT,
    npwp_number varchar(16) not null,
    npwp_status varchar(20) not null,
    created_date timestamp not null,
    PRIMARY KEY(id_npwp)
);

CREATE TABLE utilities_account_data(
	id_account INT AUTO_INCREMENT,
    account_number varchar(20) not null,
    account_status varchar(20) not null,
    amount DOUBLE not null,
    currency_type varchar(20) not null,
    bank_name varchar(50) not null,
    created_date timestamp not null,
    last_update timestamp not null,
    PRIMARY KEY(id_account)
);  