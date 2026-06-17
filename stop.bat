@echo off
REM === Stop Tomcat ===
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set CATALINA_HOME=C:\apache-tomcat-10.1.55
call "%CATALINA_HOME%\bin\shutdown.bat"
