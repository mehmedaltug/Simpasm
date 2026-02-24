VERSION=`cat version.txt`

jpackage --name simpasm --app-version $VERSION --main-jar simpasm.jar --input . --main-class Main --dest bin/

echo 'Dont forget to create a symlink /opt/simpasm/bin/simpasm -> /usr/bin/simpasm !'
echo 'It can be created with "sudo ln -s /opt/simpasm/bin/simpasm /usr/bin"'
