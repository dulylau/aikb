#!/bin/bash

BACKEND_DIR="backend"
FRONTEND_DIR="frontend"

# 启动后端服务
echo "Starting backend..."
cd "$BACKEND_DIR" && mvn spring-boot:run -q &
BACKEND_PID=$!

# 等待后端启动
sleep 5

# 启动前端开发服务器
echo "Starting frontend..."
cd "$FRONTEND_DIR" && npm run dev -- --host 0.0.0.0 &
FRONTEND_PID=$!

# 清理函数
cleanup() {
    echo "Stopping services..."
    kill $FRONTEND_PID 2>/dev/null
    kill $BACKEND_PID 2>/dev/null
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "Backend running at http://localhost:8080"
echo "Frontend running at http://localhost:5173"
echo "Press Ctrl+C to stop"

wait
