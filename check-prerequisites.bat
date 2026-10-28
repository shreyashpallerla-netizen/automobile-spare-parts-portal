@echo off
REM ============================================================
REM AutoParts Portal - Prerequisites Installer Guide
REM Run this script to check and download required tools
REM ============================================================

echo ============================================================
echo  AutoParts Portal - Prerequisites Check
echo ============================================================
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel%==0 (
    echo [OK] Java is installed
    java -version
) else (
    echo [MISSING] Java JDK 17 - Download from:
    echo   https://adoptium.net/temurin/releases/?version=17
    echo   Choose: Windows x64 .msi installer
    echo.
)

REM Check Maven
mvn -version >nul 2>&1
if %errorlevel%==0 (
    echo [OK] Maven is installed
    mvn -version
) else (
    echo [MISSING] Apache Maven 3.9 - Download from:
    echo   https://maven.apache.org/download.cgi
    echo   Extract to C:\maven and add C:\maven\bin to PATH
    echo.
)

REM Check Docker
docker --version >nul 2>&1
if %errorlevel%==0 (
    echo [OK] Docker is installed
    docker --version
) else (
    echo [MISSING] Docker Desktop - Download from:
    echo   https://www.docker.com/products/docker-desktop/
    echo   Choose: Docker Desktop for Windows
    echo.
)

REM Check Git
git --version >nul 2>&1
if %errorlevel%==0 (
    echo [OK] Git is installed
    git --version
) else (
    echo [MISSING] Git - Download from: https://git-scm.com/
    echo.
)

echo.
echo ============================================================
echo  After installing Java + Maven, run the app with:
echo    cd %TEMP%\autoparts
echo    mvn test          ^(run all unit + integration tests^)
echo    mvn spring-boot:run  ^(start application^)
echo  Then open: http://localhost:8080
echo ============================================================
pause
