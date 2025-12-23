VERSION=`grep "Implementation" src/META-INF/MANIFEST.MF | cut -d" " -f2 | cut -c1-8`

jpackage --name simpasm --app-version $VERSION --main-jar out/artifacts/Simpasm_java_jar/Simpasm-java.jar --input . --main-class Main --dest out/artifacts/Simpasm_java_jar/

echo 'Dont forget to create a symlink /opt/simpasm/bin/simpasm -> /usr/bin/simpasm !'
echo 'It can be created with "sudo ln -s /opt/simpasm/bin/simpasm /usr/bin"'
