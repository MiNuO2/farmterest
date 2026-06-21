@echo off
REM === Farmterest DB setup (needs MySQL root) ===
set MYSQL="C:\mysql-8.4.9-winx64\bin\mysql.exe"
set PW=1234

echo [1/3] create db/user...
%MYSQL% -u root -p%PW% --default-character-set=utf8mb4 < sql\setup_user.sql
if errorlevel 1 goto :err

echo [2/3] schema...
%MYSQL% -u root -p%PW% --default-character-set=utf8mb4 farmterest < sql\schema.sql
if errorlevel 1 goto :err

echo [3/3] seed...
%MYSQL% -u root -p%PW% --default-character-set=utf8mb4 farmterest < sql\seed.sql
if errorlevel 1 goto :err

echo === DB SETUP DONE: farmterest ===
goto :eof
:err
echo [ERROR] db setup failed. check MySQL path/password.
exit /b 1
