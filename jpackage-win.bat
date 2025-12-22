set VERSION=
for /f "tokens=2 delims=: " %%i in ('type src\META-INF\MANIFEST.MF ^| findstr /i /c:"Implementation"') do (
    set VERSION=%%i
)

jpackage --name Simpasm --app-version %VERSION% --main-jar out\artifacts\Simpasm_java_jar\Simpasm-java.jar --input . --main-class Main --dest out\artifacts\Simpasm_java_jar\