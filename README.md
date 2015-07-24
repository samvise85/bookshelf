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

```JavaScript
SHA1(username + ":" + expiration_date + ":" + SHA1(password) + ":" + salt)
```
  
NOTE: the expiration date is always -1 and the salt is the phrase "bookshelf by Samvise85!"

When the sever receives the username and the token can verify the token (it can recalculate the token because it knows all the parts that compose it).

The web services are marked with ```@Secured``` annotation that indicated wich role the user must possess (NOTE: roles are required to start with the prefix "ROLE_").

Is it safe? Of course not! Nothing is secure without SSL (maybe even with)

Can be better? Sure, now steling the token is like stealing the password. Expiring token and hashing the request can improve security.


Database:

Bookshelf now runs with both MySQL and H2 datasources.
If you want to use MySQL you just have to configure a file db.properties in your user home as follows:
```ini
db.url=jdbc:mysql://<ip>:<port>/<databasename>
db.user=<username>
db.password=<password>
db.driver=com.mysql.jdbc.Driver
```

If the file is not configured the Bookshelf H2 database is created/connected in your user home.


E-mail:

Bookshelf can connect to any smtp server (with password authorization). just configure a file mail.properties in your user home as follows:
```ini
smtp.host=<your smtp server>
smtp.port=<your smtp port>
smtp.protocol=<smtp or smtps>
smtp.username=<your username>
smtp.password=<your password>
support.email=<your email>
```

If the file is not configured Bookshelf will send no mail.


Internationalization:

A chain of ```ResourceResolver``` is configured in ```WebMvcConfig.java```. It permits to template-ize all text resources (html, js, css).
When a resource is requested:
- ```TimestampResourceResolver``` parse the requested path to find a version number into the file name and erase it (this version is needed to bypass server and client caches, see index.html to understand how is used).
- ```InternationalizationResourceResolver``` append to the requested path: language code and version o language (everytime a label is updated, language version is incremented)
- ```CachingResourceResolver``` (Spring standard) caches all the requests resolved by InternationalizationResourceResolver
- ```InternationalizationTemplateResourceResolver``` gets the requested path and retrieve the right resource then it passes the resource to ```InternationalizationTransformer``` that gets the user language and substitutes all the keys in the files (surrounded by double brackets "```{{key}}```") with the label. If the language is not supported labels are chosen by the default language and if a label of a supported language does not exists it creates a new empty one and returns the missing key surrounded by question marks.
 
For example, if /tpl/HeaderView_1434550070203.html is requested, the request path is manipulated as follows:
- ```TimestampResourceResolver``` transform it to /tpl/HeaderView.html and passes forward
- ```InternationalizationResourceResolver``` transform it to /tpl/HeaderView_en_102.html and passes forward
- ```CachingResourceResolver``` checks if the respource is in the cache, if not passes forward
- ```InternationalizationTemplateResourceResolver``` transforms the path again to /tpl/HeaderView.html, gets the resource and translate it then returns it back.

A view in the newly administration menu permits to view all the labels stored into the application and modify them. After the modification it's simply necessarly to refresh the page (maybe Ctrl+Shift+R).


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
- JSON (http://it.wikipedia.org/wiki/JSON)

Security:
- Spring security (http://stackoverflow.com/questions/10826293/restful-authentication-via-spring / http://automateddeveloper.blogspot.co.uk/2014/03/securing-your-mobile-api-spring-security.html)

Frontend:
- jQuery
- Backbonejs (https://github.com/thomasdavis/backbonetutorials/blob/gh-pages/videos/beginner/index.html / https://github.com/clintberry/backbone-directory-auth)
- CryptoJS (https://code.google.com/p/crypto-js/#SHA-1)

================ What's left ================

In this version:
- ~~Settings~~
- Users can add more informations
- Users can decide which information to display
- Users have new profiles (eg. author, moderator)
- (NTH) Separate the views of anonimous user, logged user and administrator (similarly to the internationalization)
- Book types (some types don't have chapters)
- Drafts
- Route in analytics
- Better URLs (tiny URLs)
- External css
- ~~Response wrapping to manage errors~~
- Mobile!

In the future:
- OAuth2 support (FB, Google, Twitter)
- Web services of the classes: Moderation, Section.

================ One Last Note ==============

I'm so sorry but there are so few comments and the code (expecially js) isn't really readable!

I'm here if someone need a help.
