# Bookshelf

================ What's this ===============

JSON/RESTful website to publish and read books with token authentication.

Bookshelf is a work in progress.

It uses the following technologies/frameworks:
- Spring
- Spring-web
- Spring-data
- Spring-security
- JQuery
- BackboneJs
- Hibernate
- more...

============== What does it do =============

Overview:

This webapp allows to publish, view and read books online.

The application uses several web services that allow to save and get a set of books, chapters, users and comments.

The saving services reqest the user to login and verify wether he/she is authorized to do so (e.g. only admin users can publish books and chapters).


Security:

Security implementation is simple: the client generates and send a token (remember-me-like) through this function:

  SHA-1(username + ":" + expiration date + ":" + SHA-1(password) + ":" + salt)

NOTE: the expiration date is always -1 and the salt is the phrase "bookshelf by Samvise85!"
NOTE 2: to support old version, it can work even with passwords stored unencrypted.

When the sever receives the username and the token can verify the token (it can recalculate the token because it knows all the parts that compose it).

The web services are marked with @Secured annotation that indicated wich role the user must possess (NOTE: roles are required to start with the prefix "ROLE_").

Is it safe? Of course not! Stealing the token you steal the identity like you are stealing the password. Without https the token can be stolen by anyone sniffing the messages otherwise you can always stole it from the cookies with different techniques.

Can be better? Yes, using an RSA public key for example to encode the token and in other hundreds ways.


Database:

Bookshelf now runs with both MySQL and H2 datasources.
If you want to use MySQL you just have to configure a file db.properties in your user home as follows:
db.url=jdbc:mysql://<your-ip>:3306/<database-name>
db.user=<username>
db.password=<password>
db.driver=com.mysql.jdbc.Driver

If the file is not configured the Bookshelf H2 database is created/connected in your user home.


Internationalization:

A chain of ResourceResolver is configured in WebMvcConfig.java. It permits to template-ize all text resources (html, js, css).
When a resource is requested:
- TimestampResourceResolver parse the requested path to find a version number into the file name and erase it (this version is needed to bypass server and client caches, see index.html to understand how is used).
- InternationalizationResourceResolver append to the requested path: language code and version o language (everytime a label is updated, language version is incremented)
- CachingResourceResolver (Spring standard) caches all the requests resolved by InternationalizationResourceResolver
- InternationalizationTemplateResourceResolver gets the requested path and retrieve the right resource then it passes the resource to InternationalizationTransformer that gets the user language and substitutes all the keys in the files (surrounded by double brackets "{{key}}") with the label. If the language is not supported labels are chosen by the default language and if a label of a supported language does not exists it creates a new empty one and returns the missing key surrounded by question marks.
 
For example, if /tpl/HeaderView_1434550070203.html is requested, the request path is manipulated as follows:
- TimestampResourceResolver transform it to /tpl/HeaderView.html and passes forward
- InternationalizationResourceResolver transform it to /tpl/HeaderView_en_102.html and passes forward
- CachingResourceResolver checks if the respource is in the cache, if not passes forward
- InternationalizationTemplateResourceResolver transforms the path again to /tpl/HeaderView.html, gets the resource and translate it then returns it back.

A view in the newly administration menu permits to view all the labels stored into the application and modify them. After the modification it's simply necessarly to refresh the page (maybe Ctrl+Shift+R).

TODO:
- manage languages and default language (services are ready)
- manage label each language in a single page with default language label as example


User management:

Now a user can register (recaptcha is included into the page).
A user can edit its information (not username or mail).
An admin can see the user list (but not the informations) and can change user type (eg. make a user an admin and viceversa).

TODO:
- add other informations
- add user setting (where a user can decide which information to display)
- add new profiles (eg. author)

========== Installation and usage ==========

How to install:
- checkout bookshelf project and build it with maven (you can also import it into an IDE with a maven importer).

How to start it:
- Deploy on a Tomcat server
- Open a browser and go to http://localhost:8080/bookshelf

Login:

There are two users:
- admin/prova
- user/password

================ Frameworks =================

REST webservices (http://www.html.it/pag/19596/i-principi-dellarchitettura-restful/):
- Spring 4 (https://spring.io/guides/gs/rest-service/)
- Spring Boot (http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot)
- JSON (http://it.wikipedia.org/wiki/JSON)

Security:
- Spring security (http://stackoverflow.com/questions/10826293/restful-authentication-via-spring / http://automateddeveloper.blogspot.co.uk/2014/03/securing-your-mobile-api-spring-security.html)

Frontend:
- jQuery
- Backbonejs (https://github.com/thomasdavis/backbonetutorials/blob/gh-pages/videos/beginner/index.html / https://github.com/clintberry/backbone-directory-auth)
- CryptoJS (https://code.google.com/p/crypto-js/#SHA-1)

================ What's left ================

- OAuth2 support (FB, Google, Twitter)
- Separate the views of anonimous user, logged user and administrator (similarly to the internationalization).
- Security based on RSA keys to prevent the stealing of the token to be fatal (or simply hash the content of the request).
- Web services of the classes: Moderation, Section.

================ One Last Note ==============

I'm so sorry but there are so few comments and the code (expecially js) isn't really readable!

I'm here if someone need a help.
