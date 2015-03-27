@echo off

if defined JAVA_HOME goto getJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto sender

echo Sorry but you need to set the JAVA_HOME environment variable to yout Java installation path to make the script work

goto fail

:getJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto sender

echo Sorry but your JAVA_HOME variable is malformed, can't launch Sender

goto end

:sender

set BASEDIR=%~dp0
if "%BASEDIR%" == "" set BASEDIR=.
"%JAVA_EXE%" -jar %BASEDIR%\sender.jar %*

:end
