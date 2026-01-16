@echo off
title CDIPD Automation Framework
color 0A

set PROJECT_DIR=%~dp0
cd %PROJECT_DIR%

echo ========================================
echo CDIPD Automation Framework
echo ========================================

echo Cleaning previous results...
if exist "test-output" rmdir /s /q "test-output"
if exist "logs" rmdir /s /q "logs"

echo Creating directories...
mkdir test-output
mkdir test-output\reports
mkdir test-output\screenshots
mkdir logs

echo Running tests...
call mvn clean test

echo.
echo ========================================
echo Test Execution Completed
echo ========================================

echo Opening latest test report...
timeout /t 2 /nobreak >nul

for /f "delims=" %%i in ('dir /b /od "test-output\reports\TestReport_*.html" 2^>nul') do set "latestReport=%%i"
if exist "test-output\reports\%latestReport%" (
    echo Opening report: %latestReport%
    start "" "test-output\reports\%latestReport%"
)

echo.
echo Logs: logs\test.log
echo Reports: test-output\reports\
pause