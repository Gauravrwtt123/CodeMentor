@echo off
cd /d "%~dp0"
echo ============================================
echo   CodeMentor Server Starter
echo ============================================
echo.
echo Agar purana server chal raha hai toh use band kar raha hoon...

rem Port 3000 pe purane server process ko kill karo
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":3000" ^| findstr "LISTENING"') do (
    echo   Purana server (PID %%p) band ho raha hai...
    taskkill /F /PID %%p >nul 2>&1
)

ping -n 2 127.0.0.1 >nul
echo.
echo Server start ho raha hai... (band karne ke liye Ctrl+C)
echo.
node server.js
pause
