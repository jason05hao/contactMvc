DROP DATABASE IF EXISTS "contactmvc_db";
DROP USER IF EXISTS "contactmvc_user";
CREATE USER "contactmvc_user" PASSWORD 'welcome';
CREATE DATABASE "contactmvc_db" owner "contactmvc_user" ENCODING = 'UTF-8';