alter table jogos add column disponivel tinyint;
update jogos set disponivel = 1;