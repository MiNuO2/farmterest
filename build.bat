@echo off
REM === Farmterest build (javac, no Maven) ===
set TOMCAT=C:\apache-tomcat-10.1.55
set SRC=src\main\java
set WEBAPP=src\main\webapp
set BUILD=build
set CLASSES=%BUILD%\WEB-INF\classes

echo [1/4] Clean previous build...
if exist %BUILD% rmdir /s /q %BUILD%
mkdir %CLASSES%

echo [2/4] Compile Java...
dir /s /b %SRC%\*.java > sources.txt
javac -encoding UTF-8 -cp "%TOMCAT%\lib\servlet-api.jar;lib\gson-2.11.0.jar" -d %CLASSES% @sources.txt
del sources.txt
if errorlevel 1 goto :err

echo [3/4] Copy web resources (JSP/CSS/JS/config)...
xcopy /e /i /y /q "%WEBAPP%\*" "%BUILD%\" >nul

echo [4/4] Copy libraries (JSTL etc.)...
if not exist "%BUILD%\WEB-INF\lib" mkdir "%BUILD%\WEB-INF\lib"
copy /y lib\*.jar "%BUILD%\WEB-INF\lib\" >nul

echo === BUILD DONE: %BUILD% ===
goto :eof
:err
echo [ERROR] compile failed
exit /b 1
