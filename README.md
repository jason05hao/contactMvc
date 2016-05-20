### Instructions

First, compile everything.
```
mvn clean package
```
Second, create the database and tables by running the command line.
```
postgre_db.bat
```
the first password is your database admin password
the second and third password is welcome

Or you can run the sql scripts in the folder src\main\webapp\WEB-INF\sql directly

Then, run the application in a embedded jetty server
```
mvn jetty:run
```

Which should allow you to access http://localhost:8080/index.html

