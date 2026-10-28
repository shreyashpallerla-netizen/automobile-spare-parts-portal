# ============================================================
# Auto-setup JDK 17 and Maven 3.9 into $env:TEMP\tools
# ============================================================
$ErrorActionPreference = "Stop"

$toolsDir = "$env:TEMP\tools"
$jdkZip = "$toolsDir\openjdk17.zip"
$mavenZip = "$toolsDir\maven.zip"

Write-Host "Creating tools directory at $toolsDir..."
New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null

# 1. Download & Extract Eclipse Temurin JDK 17
$jdkUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_windows_hotspot_17.0.13_11.zip"
Write-Host "Downloading OpenJDK 17 (~180MB)..."
Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip -UseBasicParsing

Write-Host "Extracting OpenJDK 17..."
Expand-Archive -Path $jdkZip -DestinationPath "$toolsDir" -Force
$jdkExtracted = Get-ChildItem -Path $toolsDir -Filter "jdk-17*" -Directory | Select-Object -First 1
Rename-Item -Path $jdkExtracted.FullName -NewName "jdk-17-active" -Force -ErrorAction SilentlyContinue

# 2. Download & Extract Apache Maven 3.9.6
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
Write-Host "Downloading Maven 3.9.6 (~9MB)..."
Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing

Write-Host "Extracting Maven..."
Expand-Archive -Path $mavenZip -DestinationPath "$toolsDir" -Force
$mvnExtracted = Get-ChildItem -Path $toolsDir -Filter "apache-maven-3.9*" -Directory | Select-Object -First 1
Rename-Item -Path $mvnExtracted.FullName -NewName "maven-active" -Force -ErrorAction SilentlyContinue

# Clean up zip files
Remove-Item -Path $jdkZip, $mavenZip -Force -ErrorAction SilentlyContinue

Write-Host "JDK and Maven successfully installed to $toolsDir!"
