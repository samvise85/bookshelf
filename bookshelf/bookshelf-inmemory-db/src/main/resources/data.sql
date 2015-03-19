insert into Book (id, title, year, synopsis, genre, author, stream, creation, lastModification)
values ('libro1', 'titolo', 1999, 'sinossi', 'genere', 'autore', null, null, null);

insert into User (id, username, email, password, admin, firstname, lastname, language)
values ('admin', 'admin', 'admin@fakemail.com', 'prova', true, 'Administrator', '', 'en');
insert into User (id, username, email, password, admin, firstname, lastname, language)
values ('user', 'user', 'user@fakemail.com', 'password', false, 'Normal', 'User', 'en');