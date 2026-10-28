@echo off
set "JAVA_HOME=%TEMP%\tools\jdk-17-full\jdk-17.0.13+11"
set "MAVEN_HOME=%TEMP%\tools\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"
call "%MAVEN_HOME%\bin\mvn.cmd" %*
