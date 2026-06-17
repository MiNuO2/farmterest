@echo off
REM === Start Tomcat ===
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set CATALINA_HOME=C:\apache-tomcat-10.1.55

echo Starting Tomcat...
echo Open http://localhost:8080/farmterest  (stop: stop.bat)
call "%CATALINA_HOME%\bin\startup.bat"
