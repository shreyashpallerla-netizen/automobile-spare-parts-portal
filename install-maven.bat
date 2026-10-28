@echo off
REM ============================================================
REM Maven 3.9.6 Auto-Installer for Windows
REM Run as Administrator
REM ============================================================

echo ============================================================
echo  Installing Apache Maven 3.9.6
echo ============================================================

set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_ZIP=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_DIR=C:\maven

echo Downloading Maven %MAVEN_VERSION%...
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%' -UseBasicParsing"

if not exist %MAVEN_ZIP% (
    echo ERROR: Download failed. Check internet connection.
    pause
    exit /b 1
)

echo Extracting to C:\maven ...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath 'C:\' -Force"
if exist "C:\apache-maven-%MAVEN_VERSION%" (
    if exist "%MAVEN_DIR%" rmdir /s /q "%MAVEN_DIR%"
    rename "C:\apache-maven-%MAVEN_VERSION%" maven
)

echo Setting MAVEN_HOME system variable...
setx MAVEN_HOME "C:\maven" /M
setx PATH "%PATH%;C:\maven\bin" /M

echo.
echo ============================================================
echo  Maven installed! CLOSE AND REOPEN PowerShell then run:
echo    mvn -version
echo ============================================================
pause
