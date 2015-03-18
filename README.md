# bookshelf

================ What's this ===============

JSON/RESTful website to publish and read books with token authentication.

Bookshelf is a work in progress.

It uses the following technologies/frameworks:
- Spring
- Spring-web
- Spring-security
- JQuery
- BackboneJs
- more...

============== What does it do =============

Overview:

This webapp allows to publish, view and read books online.

The application uses several web services that allow to save and get a set of books, chapters, users and comments.

The saving services reqest the user to login and verify wether he/she is authorized to do so (e.g. only admin users can publich books and chapters).


Security:

Security implementation is simple: the client generates and send a token (remember-me-like) through this function:

  SHA-1(username + ":" + expiration date + ":" + password + ":" + salt)
  
NOTE: the expiration date is always -1 and the salt is the phrase "bookshelf by Samvise85!"

When the sever receives the username and the token can verify the token (it can recalculate the token because it knows all the parts that compose it).

The web services are marked with @Secured annotation that indicated wich role the user must possess (NOTE: roles are required to start with the prefix "ROLE_").


Is it safe? No, of course! Stealing the token you steal the identity like you are stealing the password. Without https the token can be stolen by anyone sniffing the messages otherwise you can always stole it from the cookies with different techniques.

Can be better? Yes, using an RSA public key for example to encode the token and in other hundreds ways.


Persistence:

Actually the persistence is on file. Objects are serialized with JSON and saved in a directory tree structure:
- data
- - Book
- - Chapter
- - User
- - Etc...

Operation like edit or delete archive older version of the files in a twin directory structure adding a timestamp suffix to the name of the file.

=============== Installation ================

How to install:

1 - unzip data.zip into the folder C:\bookshelf

2 - checkout bookshelf project and build it with maven (you can also import it into an IDE with a maven importer).

How to start it:

1 - Run the runnable jar bookshelf-standalone (in an IDE run Application.java as Java Application)

2 - Open a browser and go to http://localhost:8080/

Login:

There are two users:
- admin/prova
- user/password

================ Frameworks =================

REST webservices (http://www.html.it/pag/19596/i-principi-dellarchitettura-restful/):
- Spring 4 (https://spring.io/guides/gs/rest-service/)
- JSON (http://it.wikipedia.org/wiki/JSON)
Security:
- Spring security (http://stackoverflow.com/questions/10826293/restful-authentication-via-spring / http://automateddeveloper.blogspot.co.uk/2014/03/securing-your-mobile-api-spring-security.html)

Frontend:
- jQuery
- Backbonejs (https://github.com/thomasdavis/backbonetutorials/blob/gh-pages/videos/beginner/index.html / https://github.com/clintberry/backbone-directory-auth)
- CryptoJS (https://code.google.com/p/crypto-js/#SHA-1)

================ What's left ================

- Separate the views of anonimous user, logged user and administrator.
- User Management is just a draft.
- Password is saved uncoded.
- Security based on RSA keys to prevent the stealing of the token to be fatal.
- Web services of the classes: Comment, Stream, Moderation, Section.

================ One Last Note ==============

I'm so sorry but there are so few comments and the code (expecially js) isn't really readable!

I'm here if someone need a help.
