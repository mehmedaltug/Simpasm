VERSION=`grep "Implementation" src/META-INF/MANIFEST.MF | cut -d" " -f2`

jpackage --name Simpasm --app-version $VERSION --main-jar out/artifacts/Simpasm_java_jar/Simpasm-java.jar --input . --main-class Main --dest out/artifacts/Simpasm_java_jar/