@echo off
REM === Stop Tomcat ===
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
set CATALINA_HOME=C:\apache-tomcat-10.1.55
call "%CATALINA_HOME%\bin\shutdown.bat"
