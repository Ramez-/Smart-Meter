@echo off

@rem parse the parameters 
setlocal enabledelayedexpansion
set /a cnt=0
for %%g in (%*) do (
  set /a cnt=!cnt!+1
  if !cnt!==1 set COMPONENT_HOME=%%g
  if !cnt!==2 set DEBUG=%%g
  if !cnt!==3 set OUTPUT_FILE=%%g
  if !cnt!==4 set JAVA_HOME=%%g
  if !cnt!==5 set SERVICE_USER_NAME=%%g
  if !cnt!==6 set SERVICE_USER_PASSWORD=%%g
  if !cnt!==7 set GMS_CORE_DB_HOST=%%g
  if !cnt!==8 set GMS_CORE_DB_PORT=%%g
  if !cnt!==9 set GMS_CORE_DB_INSTANCE=%%g
  if !cnt!==10 set GMS_CORE_DB_NAME=%%g
  if !cnt!==11 set GMS_CORE_DB_USER_NAME=%%g
  if !cnt!==12 set GMS_CORE_DB_USER_PASSWORD=%%g
  if !cnt!==13 set GMS_MANAGEMENT_DB_HOST=%%g
  if !cnt!==14 set GMS_MANAGEMENT_DB_PORT=%%g
  if !cnt!==15 set GMS_MANAGEMENT_DB_INSTANCE=%%g
  if !cnt!==16 set GMS_MANAGEMENT_DB_NAME=%%g
  if !cnt!==17 set GMS_MANAGEMENT_DB_USER_NAME=%%g
  if !cnt!==18 set GMS_MANAGEMENT_DB_PASSWORD=%%g
)

@rem remove leading and trailing quotes
set "COMPONENT_HOME=%COMPONENT_HOME:"=%"
set "DEBUG=%DEBUG:"=%"
set "OUTPUT_FILE=%OUTPUT_FILE:"=%"
set "JAVA_HOME=%JAVA_HOME:"=%"
set "SERVICE_USER_NAME=%SERVICE_USER_NAME:"=%"
set "SERVICE_USER_PASSWORD=%SERVICE_USER_PASSWORD:"=%"
set "GMS_CORE_DB_HOST=%GMS_CORE_DB_HOST:"=%"
set "GMS_CORE_DB_PORT=%GMS_CORE_DB_PORT:"=%"
set "GMS_CORE_DB_INSTANCE=%GMS_CORE_DB_INSTANCE:"=%"
set "GMS_CORE_DB_NAME=%GMS_CORE_DB_NAME:"=%"
set "GMS_CORE_DB_USER_NAME=%GMS_CORE_DB_USER_NAME:"=%"
set "GMS_CORE_DB_USER_PASSWORD=%GMS_CORE_DB_USER_PASSWORD:"=%"
set "GMS_MANAGEMENT_DB_HOST=%GMS_MANAGEMENT_DB_HOST:"=%" 
set "GMS_MANAGEMENT_DB_PORT=%GMS_MANAGEMENT_DB_PORT:"=%"
set "GMS_MANAGEMENT_DB_INSTANCE=%GMS_MANAGEMENT_DB_INSTANCE:"=%"
set "GMS_MANAGEMENT_DB_NAME=%GMS_MANAGEMENT_DB_NAME:"=%"
set "GMS_MANAGEMENT_DB_USER_NAME=%GMS_MANAGEMENT_DB_USER_NAME:"=%"
set "GMS_MANAGEMENT_DB_PASSWORD=%GMS_MANAGEMENT_DB_PASSWORD:"=%"

if not %cnt%==18 echo "Wrong number of parameters" & exit /b 1

@rem echo on if debug is true
if "%DEBUG%"=="true" @echo on

@rem check if output file is writeable
if "%OUTPUT_FILE%"=="" goto withoutlogfile 
type nul >> "%OUTPUT_FILE%"
if %ERRORLEVEL% equ 0 set REDIRECT_OUTPUT=^>^>"%OUTPUT_FILE%" 2^>^&1
:withoutlogfile

echo [GMS Dlms Cosem Engine] Installing GMS Dlms Cosem Engine %REDIRECT_OUTPUT%
echo [GMS Dlms Cosem Engine] Begin: %date% %time% %REDIRECT_OUTPUT%
echo [GMS Dlms Cosem Engine] Creating empty directories %REDIRECT_OUTPUT%
mkdir "%COMPONENT_HOME%\logs" %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 2 set ERROR_LEVEL=1

mkdir "%COMPONENT_HOME%\temp" %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 2 set ERROR_LEVEL=1

if "%JAVA_HOME%"=="" (
  echo [Directory Server] JAVA_HOME must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
)

echo [GMS Dlms Cosem Engine] Substituting variables %REDIRECT_OUTPUT%
@rem reset errorlevel
type nul > nul

if "%GMS_CORE_DB_HOST%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_CORE_DB_HOST must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.host}" --replace "%GMS_CORE_DB_HOST%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_CORE_DB_PORT%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_CORE_DB_PORT must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.port}" --replace "%GMS_CORE_DB_PORT%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

"%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.instance}" --replace "%GMS_CORE_DB_INSTANCE%" %REDIRECT_OUTPUT%
if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  

if "%GMS_CORE_DB_NAME%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_CORE_DB_NAME must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.dbname}" --replace "%GMS_CORE_DB_NAME%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_CORE_DB_USER_NAME%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_CORE_DB_USER_NAME must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.username}" --replace "%GMS_CORE_DB_USER_NAME%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_CORE_DB_USER_PASSWORD%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_CORE_DB_USER_PASSWORD must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "jdbc.properties" --find "${filter.gms.db.password}" --replace "%GMS_CORE_DB_USER_PASSWORD%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_MANAGEMENT_DB_HOST%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_MANAGEMENT_DB_HOST must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.host}" --replace "%GMS_MANAGEMENT_DB_HOST%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_MANAGEMENT_DB_PORT%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_MANAGEMENT_DB_PORT must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.port}" --replace "%GMS_MANAGEMENT_DB_PORT%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

"%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.instance}" --replace "%GMS_MANAGEMENT_DB_INSTANCE%" %REDIRECT_OUTPUT%
if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  

if "%GMS_MANAGEMENT_DB_NAME%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_MANAGEMENT_DB_NAME must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.dbname}" --replace "%GMS_MANAGEMENT_DB_NAME%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_MANAGEMENT_DB_USER_NAME%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_MANAGEMENT_DB_USER_NAME must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.username}" --replace "%GMS_MANAGEMENT_DB_USER_NAME%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

if "%GMS_MANAGEMENT_DB_PASSWORD%"=="" (
  echo [GMS Dlms Cosem Engine] GMS_MANAGEMENT_DB_PASSWORD must not be empty %REDIRECT_OUTPUT%
  set ERROR_LEVEL=1
) else (
  "%COMPONENT_HOME%\bin\fnr.exe" --silent --setErrorLevelIfAnyFileErrors --cl --dir "%COMPONENT_HOME%\lib\classes" --fileMask "log4j.properties" --find "${filter.gms.db.management.password}" --replace "%GMS_MANAGEMENT_DB_PASSWORD%" %REDIRECT_OUTPUT%
  if %ERRORLEVEL% neq 0 set ERROR_LEVEL=1  
)

echo [GMS Dlms Cosem Engine] Installing the service GMSDlmsCosemEngine %REDIRECT_OUTPUT%
"%COMPONENT_HOME%\bin\GMSDlmsCosemEngine.exe" //IS//GMSDlmsCosemEngine^
  --DisplayName="GMS Dlms Cosem Engine"^
  --Description="Scheduler to request technical system commands and gather results"^
  --Install="%COMPONENT_HOME%\bin\GMSDlmsCosemEngine.exe"^
  --Jvm="%JAVA_HOME%\bin\server\jvm.dll"^
  --Startup=auto^
  --StartMode=jvm^
  --StartClass=com.ubitronix.uiem.execution.adapter.dlms.DlmsCosemEngineService^
  --StartMethod=start^
  --StopMode=jvm^
  --StopClass=com.ubitronix.uiem.execution.adapter.dlms.DlmsCosemEngineService^
  --StopMethod=stop^
  --StopTimeout=30^
  --Classpath="%COMPONENT_HOME%\lib\classes;%COMPONENT_HOME%\lib\*;"^
  --LogPath="%COMPONENT_HOME%\logs"^
  --LogPrefix=gmsdlmscosemengine-daemon^
  --LogLevel=info^
  --JvmMx=256^
  --StdOutput=auto^
  --StdError=auto^
  --JvmOptions="-Djava.io.tmpdir=%COMPONENT_HOME%\temp;-DenableTracing=false" %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 & echo [GMS Dlms Cosem Engine] Failed installing the service GMSDlmsCosemEngine %REDIRECT_OUTPUT%

if "%SERVICE_USER_NAME%"=="" set SERVICE_USER_NAME=NT AUTHORITY\LocalService

echo [GMS Dlms Cosem Engine] Configure the service GMSDlmsCosemEngine to start with user %SERVICE_USER_NAME% %REDIRECT_OUTPUT%
sc config GMSDlmsCosemEngine start= auto obj= "%SERVICE_USER_NAME%" password= "%SERVICE_USER_PASSWORD%" %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

echo [GMS Dlms Cosem Engine] Setting permissions for user %SERVICE_USER_NAME% %REDIRECT_OUTPUT%
"%COMPONENT_HOME%\bin\ntrights.exe" +r SeServiceLogonRight -u "%SERVICE_USER_NAME%" %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%" /inheritance:r %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%" /grant *S-1-5-32-544:(OI)(CI)(GA,WO) %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%" /setowner "%SERVICE_USER_NAME%" /t /q %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%" /grant "%SERVICE_USER_NAME%":(OI)(CI)RX %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%\logs" /grant "%SERVICE_USER_NAME%":(OI)(CI)M %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

icacls "%COMPONENT_HOME%\temp" /grant "%SERVICE_USER_NAME%":(OI)(CI)M %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1

echo [GMS Dlms Cosem Engine] End: %date% %time% %REDIRECT_OUTPUT%

exit /b %ERROR_LEVEL%
