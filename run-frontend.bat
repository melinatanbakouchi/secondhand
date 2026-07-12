@echo off
setlocal
cd /d "%~dp0"

echo ========================================
echo   SecondHand - Frontend
echo ========================================
echo.
echo Make sure Backend is running on http://localhost:8083
echo.
echo Starting Frontend JavaFX...
echo Close the app window or press Ctrl+C to stop.
echo.

cd frontend
call mvn javafx:run
set exitcode=%errorlevel%

echo.
if not %exitcode% == 0 (
    echo [ERROR] Frontend exited with code %exitcode%
) else (
    echo Frontend stopped.
)
pause
exit /b %exitcode%
