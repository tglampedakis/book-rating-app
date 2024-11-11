This application is implemented using Java Spring Boot framework and Java version 21.

Maven is used as the libraries tool.

Jacoco was selected as library for implementing the unit tests.

SQLite has been used as a database, like assessment instructions suggested, for simplicity reasons.

Setting up the Database should not be an issue, the file "**_book_reviews.db_**" contains the db that the application is using when running locally.

You may find additional info in the "**_application.properties_**" file.

For your convenience, a sample JSON request payload has been included, under the path "**_\src\main\resources\samples_**".
You may use this payload to call the add review endpoint of the app "**_/api/v1/books/review_**", in case you want to fill some reviews and ratings in the local db.

You may also find 2 files under the docs folder in the path "**_\src\main\resources\docs_**".
The "**_db_queries_**" file contains the SQL create table statements, in case you want to see anything or execute something.

The "**_High Level Architecture.pdf_**" contains the diagram, as per the last Bonus Objective.

Last but not least, Swagger is also configured, and you may access it through the URL "**_http://localhost:8080/swagger-ui/index.html_**".

Feel free to ask me for more instructions or help anytime! :)

