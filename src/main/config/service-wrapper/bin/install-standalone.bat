@echo off

@rem return with errorlevel == 0 only if installation was completely successfull
set ERROR_LEVEL=0

set COMPONENT_HOME=%~dp0..
@rem provide absolute paths without any indirection
pushd .
cd /D %COMPONENT_HOME%
set COMPONENT_HOME=%CD%
popd

@rem JAVA_HOME has to be set to a valid JRE8 x64 root directory
@rem set JAVA_HOME=C:\Program Files\Java\jre8

@rem output file must not be in component home because during the installation the permissions will be adjusted which results in access denied
set OUTPUT_FILE=
set DEBUG=true

set SERVICE_USER_NAME=
set SERVICE_USER_PASSWORD=

set GMS_CORE_DB_HOST=localhost
set GMS_CORE_DB_PORT=1433
set GMS_CORE_DB_INSTANCE=
set GMS_CORE_DB_NAME=GMS_Core
set GMS_CORE_DB_USER_NAME=gms
set GMS_CORE_DB_USER_PASSWORD=gms

set GMS_MANAGEMENT_DB_HOST=localhost
set GMS_MANAGEMENT_DB_PORT=1433
set GMS_MANAGEMENT_DB_INSTANCE=
set GMS_MANAGEMENT_DB_NAME=GMS_Management
set GMS_MANAGEMENT_DB_USER_NAME=gms_management
set GMS_MANAGEMENT_DB_PASSWORD=gms

copy "%~dp0install.bat" "%TEMP%" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 
call "%TEMP%\install.bat" "%COMPONENT_HOME%" "%DEBUG%" "%OUTPUT_FILE%" "%JAVA_HOME%" "%SERVICE_USER_NAME%" "%SERVICE_USER_PASSWORD%" "%GMS_CORE_DB_HOST%" "%GMS_CORE_DB_PORT%" "%GMS_CORE_DB_INSTANCE%" "%GMS_CORE_DB_NAME%" "%GMS_CORE_DB_USER_NAME%" "%GMS_CORE_DB_USER_PASSWORD%" "%GMS_MANAGEMENT_DB_HOST%" "%GMS_MANAGEMENT_DB_PORT%" "%GMS_MANAGEMENT_DB_INSTANCE%" "%GMS_MANAGEMENT_DB_NAME%" "%GMS_MANAGEMENT_DB_USER_NAME%" "%GMS_MANAGEMENT_DB_PASSWORD%"
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 
del "%TEMP%\install.bat" >nul 2>&1
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 

if not "%ERROR_LEVEL%"=="0" echo Error while installing GMS Dlms Cosem Engine

pause
