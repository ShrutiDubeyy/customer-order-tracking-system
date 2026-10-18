@echo off
REM Runs the complete container lifecycle and writes every command and its output to docker-command-log.txt
REM Prerequisites: Docker Desktop is running and target\order-tracker.war exists (mvn clean package -DskipTests)
set LOG=docker-command-log.txt
echo Docker command log - %DATE% %TIME% > %LOG%

call :step docker --version
call :step docker build -t order-tracker:0.1.0 -t order-tracker:latest .
call :step docker images order-tracker
call :step docker run -d --name order-tracker -p 8083:8080 -e APP_ENV=docker order-tracker:0.1.0
call :step docker ps
echo Waiting 60 seconds for the application to start...
timeout /t 60 /nobreak >nul
call :step docker logs --tail 15 order-tracker
call :step curl -s http://localhost:8083/actuator/health
call :step docker inspect --format "{{.State.Health.Status}}" order-tracker
call :step docker stop order-tracker
call :step docker ps -a
call :step docker start order-tracker
call :step docker restart order-tracker
call :step docker ps
call :step docker stop order-tracker
call :step docker rm order-tracker
call :step docker ps -a

echo.
echo Done. Open %LOG% to see the full command log.
goto :eof

:step
echo. >> %LOG%
echo ^> %* >> %LOG%
%* >> %LOG% 2>&1
goto :eof
