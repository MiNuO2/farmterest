@echo off
REM === Stop MySQL 8.4.9 (zip install) ===
set MYSQL_HOME=C:\mysql-8.4.9-winx64
"%MYSQL_HOME%\bin\mysqladmin.exe" -u root -p1234 shutdown
echo MySQL stopped.
