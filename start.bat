@echo off
chcp 65001 >nul
title AIKB 产品管理系统

echo ================================
echo   AIKB 产品管理系统
echo ================================
echo.

cd /d "%~dp0"

echo [1/2] 启动后端 (Spring Boot)...
cd backend
start "AIKB Backend" cmd /c "mvn spring-boot:run -q"
cd ..

echo [2/2] 等待后端就绪...
timeout /t 8 /nobreak >nul

echo [2/2] 启动前端 (Vite)...
cd frontend
start "AIKB Frontend" cmd /c "npm run dev -- --host 0.0.0.0"
cd ..

echo.
echo ================================
echo   后端服务: http://localhost:8080
echo   前端页面: http://localhost:5173
echo   API文档:  http://localhost:8080/doc.html
echo ================================
echo.
echo 按任意键停止所有服务...
pause >nul

taskkill /fi "WINDOWTITLE eq AIKB Backend*" /f >nul 2>&1
taskkill /fi "WINDOWTITLE eq AIKB Frontend*" /f >nul 2>&1
echo 服务已停止。
pause >nul
