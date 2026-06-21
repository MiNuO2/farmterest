@echo off
REM === Start MySQL 8.4.9 (zip install, no service) ===
set MYSQL_HOME=C:\mysql-8.4.9-winx64
echo Starting MySQL on port 3306...
start "" /B "%MYSQL_HOME%\bin\mysqld.exe" --datadir=%MYSQL_HOME%\data --basedir=%MYSQL_HOME% --port=3306
echo MySQL launched. (stop: mysql-stop.bat)
