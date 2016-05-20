"C:\Program Files\PostgreSQL\9.5\bin\psql.exe" -h localhost -d postgres -U postgres -p 5432 -a -W -f src\main\webapp\WEB-INF\sql\01_create-db.sql
"C:\Program Files\PostgreSQL\9.5\bin\psql.exe" -h localhost -d contactmvc_db -U contactmvc_user -p 5432 -a -W -f src\main\webapp\WEB-INF\sql\02_create-tables.sql
