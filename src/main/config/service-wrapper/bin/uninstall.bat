@echo off

@rem parse the parameters 
setlocal enabledelayedexpansion
set /a cnt=0
for %%g in (%*) do (
  set /a cnt=!cnt!+1
  if !cnt!==1 set COMPONENT_HOME=%%g
  if !cnt!==2 set DEBUG=%%g
  if !cnt!==3 set OUTPUT_FILE=%%g
)

@rem remove leading and trailing quotes
set "COMPONENT_HOME=%COMPONENT_HOME:"=%"
set "DEBUG=%DEBUG:"=%"
set "OUTPUT_FILE=%OUTPUT_FILE:"=%"

if not %cnt%==3 echo "Wrong number of parameters" & exit /b 1

@rem echo on if debug is true
if "%DEBUG%"=="true" @echo on

@rem check if output file is writeable
if "%OUTPUT_FILE%"=="" goto withoutlogfile 
type nul >> "%OUTPUT_FILE%"
if %ERRORLEVEL% equ 0 set REDIRECT_OUTPUT=^>^>"%OUTPUT_FILE%" 2^>^&1
:withoutlogfile

echo [GMS Dlms Cosem Engine] Uninstalling GMS Dlms Cosem Engine %REDIRECT_OUTPUT%
echo [GMS Dlms Cosem Engine] Begin: %date% %time% %REDIRECT_OUTPUT%
echo [GMS Dlms Cosem Engine] Uninstalling the service GMSDlmsCosemEngine %REDIRECT_OUTPUT%

"%COMPONENT_HOME%\bin\GMSDlmsCosemEngine.exe" //DS//GMSDlmsCosemEngine^
  --LogPath="%COMPONENT_HOME%\logs"^
  --LogPrefix=gmsdlmscosemengine-daemon^
  --LogLevel=info %REDIRECT_OUTPUT%
if %ERRORLEVEL% geq 1 set ERROR_LEVEL=1 & echo [GMS Dlms Cosem Engine] Failed uninstalling the service GMSDlmsCosemEngine %REDIRECT_OUTPUT%

echo [GMS Dlms Cosem Engine] End: %date% %time% %REDIRECT_OUTPUT%

exit /b %ERROR_LEVEL%
