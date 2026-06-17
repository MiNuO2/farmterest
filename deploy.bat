@echo off
REM === Farmterest deploy (build -> Tomcat webapps) ===
set TOMCAT=C:\apache-tomcat-10.1.55
set APP=farmterest

if not exist build (
  echo [ERROR] build folder not found. Run build.bat first.
  exit /b 1
)

echo [1/3] Place MySQL driver into Tomcat\lib (for container DBCP)...
if not exist "%TOMCAT%\lib\mysql-connector-j-8.4.0.jar" copy /y lib\mysql-connector-j-8.4.0.jar "%TOMCAT%\lib\" >nul

echo [2/3] Remove old deployment...
if exist "%TOMCAT%\webapps\%APP%" rmdir /s /q "%TOMCAT%\webapps\%APP%"

echo [3/3] Copy deployment...
xcopy /e /i /y /q build "%TOMCAT%\webapps\%APP%" >nul

echo === DEPLOY DONE: http://localhost:8080/%APP% ===
