@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ========================================
echo   SecondHand - Backend
echo ========================================
echo.

where docker >nul 2>&1
if errorlevel 1 goto no_docker

echo [1/2] Starting PostgreSQL...
docker compose up -d postgres
if errorlevel 1 (
    echo [ERROR] Failed to start PostgreSQL.
    pause
    exit /b 1
)

echo Waiting for database...
set retries=0

:wait_postgres
for /f "usebackq delims=" %%s in (`docker inspect -f "{{.State.Health.Status}}" secondhand-postgres 2^>nul`) do set health=%%s
if /i "!health!"=="healthy" goto db_ready

set /a retries+=1
if !retries! GEQ 40 (
    echo [ERROR] Database not ready after 40 seconds.
    echo Check: docker compose ps
    pause
    exit /b 1
)
timeout /t 1 /nobreak >nul
goto wait_postgres

:db_ready
echo PostgreSQL is ready.
goto start_backend

:no_docker
echo [WARN] Docker not found. Make sure PostgreSQL is running on port 5432.
goto start_backend

:start_backend
echo.
echo [2/2] Starting Backend on http://localhost:8083 ...
echo Press Ctrl+C to stop.
echo.
cd backend
call mvn spring-boot:run
set exitcode=!errorlevel!

echo.
if not !exitcode! == 0 (
    echo [ERROR] Backend exited with code !exitcode!
) else (
    echo Backend stopped.
)
pause
exit /b !exitcode!
