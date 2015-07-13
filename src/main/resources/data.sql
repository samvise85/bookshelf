insert into Book (id, title, year, synopsis, genre, author, stream, creation, lastModification)
values ('libro1', 'titolo', 1999, 'sinossi', 'genere', 'autore', null, null, null);

insert into User (id, username, email, password, admin, firstname, lastname, language)
values ('admin', 'admin', 'admin@fakemail.com', '6279886fde090b3038f267098bcca771a6efa946', true, 'Administrator', '', 'en');
insert into User (id, username, email, password, admin, firstname, lastname, language)
values ('user', 'user', 'user@fakemail.com', '5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8', false, 'Normal', 'User', 'en');