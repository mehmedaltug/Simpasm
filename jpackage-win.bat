@echo off
set VERSION=
for /f "delims=" %%i in (version.txt) do (
    set VERSION=%%i
)

echo Building the executable...
jpackage --name simpasm --app-version %VERSION% --main-jar simpasm.jar --input . --main-class Main --dest bin\

echo Program executable is created. After installing, dont forget to add it to the user PATH!
pause
