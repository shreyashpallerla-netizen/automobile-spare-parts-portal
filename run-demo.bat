@echo off
echo ============================================================
echo   Launching AutoParts Portal Demo (Standalone Mode)
echo ============================================================
echo.
echo   URL:      http://localhost:8080
echo   Admin:    admin / Admin@123
echo   User:     user1 / User@123
echo   H2 DB:    http://localhost:8080/h2-console
echo.
echo   Starting Spring Boot application...
echo   Press Ctrl+C to stop the server when done.
echo ============================================================
echo.

set "JAVA_HOME=%TEMP%\tools\jdk-17-full\jdk-17.0.13+11"
set "MAVEN_HOME=%TEMP%\tools\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0"
"%JAVA_HOME%\bin\java.exe" -jar target\autoparts-portal-1.0.0.jar --spring.profiles.active=demo
pause
