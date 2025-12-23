set VERSION=
for /f "tokens=2 delims=: " %%i in ('type src\META-INF\MANIFEST.MF ^| findstr /i /c:"Implementation"') do (
    set VERSION=%%i
)

jpackage --name simpasm --app-version %VERSION% --main-jar out\artifacts\Simpasm_java_jar\Simpasm-java.jar --input . --main-class Main --dest out\artifacts\Simpasm_java_jar\

echo Program executable is created. After installing, dont forget to add it to the user PATH!
pause
