CREATE TABLE jogos (
	id integer not null auto_increment,
	nome varchar(100) not null,
	preco decimal(10, 2) not null,
	dev varchar(100) not null,
	pub varchar(100) not null,
	genero varchar(100) not null,
	data varchar(100) not null,
	
	
	primary key (id)
);