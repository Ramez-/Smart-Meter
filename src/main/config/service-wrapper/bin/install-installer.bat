@echo off

@rem return with errorlevel == 0 only if installation was completely successfull
set ERROR_LEVEL=0

set COMPONENT_HOME=%~dp0..
@rem provide absolute paths without any indirection
pushd .
cd /D %COMPONENT_HOME%
set COMPONENT_HOME=%CD%
popd

copy "%~dp0install.bat" "%TEMP%" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 
call "%TEMP%\install.bat" "%COMPONENT_HOME%" %*
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1
del "%TEMP%\install.bat" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

if not "%ERROR_LEVEL%"=="0" echo Error while installing GMS Dlms Cosem Engine. Check the logFile.

exit /b %ERROR_LEVEL%
