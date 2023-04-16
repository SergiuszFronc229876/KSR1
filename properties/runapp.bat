@echo off
setlocal enabledelayedexpansion

set "JAVA_OPTS="

for /f "tokens=*" %%a in ('type %1.properties') do (
    set "line=%%a"
    if "!line:~0,28!" == "PERCENTAGE_OF_THE_TRAINING_SET" (
        set "JAVA_OPTS=!JAVA_OPTS! -DPERCENTAGE_OF_THE_TRAINING_SET=!line:~29!"
    )
    if "!line:~0,8!" == "NEIGHBORS" (
        set "JAVA_OPTS=!JAVA_OPTS! -DNEIGHBORS=!line:~9!"
    )
    if "!line:~0,6!" == "METRIC" (
        set "JAVA_OPTS=!JAVA_OPTS! -DMETRIC=!line:~7!"
    )
    if "!line:~0,8!" == "FEATURES" (
        set "JAVA_OPTS=!JAVA_OPTS! -DFEATURES=!line:~9!"
    )
)

if "%CSVDIR%" == "" (
    set "CWD=%CD%"
    set "CSVDIR=%CWD%\%~n1.csv"
    set "JAVA_OPTS=!JAVA_OPTS! -DCSVDIR="%CSVDIR%""
)
if "%GUIMODE%" == "" (
    set "JAVA_OPTS=!JAVA_OPTS! -DGUIMODE=false"
)

java %JAVA_OPTS% -jar target\KSR1-1.0-SNAPSHOT-jar-with-dependencies.jar