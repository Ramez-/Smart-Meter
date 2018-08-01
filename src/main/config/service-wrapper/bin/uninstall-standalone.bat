@echo off

@rem return with errorlevel == 0 only if installation was completely successfull
set ERROR_LEVEL=0

set COMPONENT_HOME=%~dp0..
@rem provide absolute paths without any indirection
pushd .
cd /D %COMPONENT_HOME%
set COMPONENT_HOME=%CD%
popd

@rem output file must not be in component home because during the installation the permissions will be adjusted which results in access denied
set OUTPUT_FILE=
set DEBUG=true

copy "%~dp0uninstall.bat" "%TEMP%" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1
call "%TEMP%\uninstall.bat" "%COMPONENT_HOME%" "%DEBUG%" "%OUTPUT_FILE%"
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1
del "%TEMP%\uninstall.bat" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

if not "%ERROR_LEVEL%"=="0" echo Error while uninstalling GMS Dlms Cosem Engine

pause