@echo off
title GameOne Console
:start
java -XX:+UseConcMarkSweepGC -Xmx256m -cp ./libs/*; server.Startup
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin have restarted, please wait.
echo.
goto start
:error
echo.
echo Server have terminated abnormaly.
echo.
:end
echo.
echo Server terminated.
echo.
pause
