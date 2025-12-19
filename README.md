<h2>Simpasm NASM Compiler</h2>

<h3>Why?</h3>
I have noticed while writing my operating system that assembly is very easy to lose track of and it is somewhat hard to write a filesystem and switch to C, so I decided to make my own mini programming language.<br>

<h3>Features</h3>
<p>It makes abstractions that makes life easier.<br>
For example, if statements become easier to keep track of and saves you the hussle:</p>

```
if sp <= 10 {
  ax + 5
}
```

Becomes:

```
cmp sp, 10
jg if_X
add ax, 5
if_X:
```
<br>
where X is determined by the compiler.<br>

<h3>Syntax</h3>
<p>REGISTER = VALUE<br>
REGISTER + VALUE<br>
REGISTER - VALUE<br>
/ REGISTER<br>
* REGISTER<br>
# COMMENT<br>
segment SEGMENT<br>
goto SEGMENT<br>
if REGISTER COMPARATOR VALUE {<br>
  ...<br>
}<br>
loop REGISTER COMPARATOR VALUE {<br>
  ...<br>
}<br></p>
<p>You can replace any of the VALUEs with registers.<br>
In addition to these if the compiler comes across a line that does not match any of these it assumes it is a valid NASM line and includes those in the output but marks them as unsafe.<br></p>

<h3>Usage</h3>
Intellij has a configured artifact to build the jar file using the Main.java and its requirements and then another build process has to be started to create an .exe (or standart executable for linux/mac) to build the program.<br>
To use the program you must specify at least 1 argument which will be the name of the source file. Also there is an optional second argument which is the output file name.

<h3>Implementation</h3>
For general operations, program uses splittage and keywords to determine the suitable replacement.<br>
For if statements and loops it uses a custom Stack class that keeps track of the position we are in to determine which lines are in loops/ifs.


<h2>TODO:</h2>
<h3>Add easier functions</h3>
Syntax will be something similar to this:<br>

```
fn NAME(PARAMETERS) {
  ...
}
NAME(VALUES)

```
And it will automaticly set required registers to values you specify, ex:

```
fn add(ax, bx){
  ax + bx
}
add(5, 6) # result will be in ax
```

<h3>Command-line flags</h3>
Some useful flags like:<br>
<p>--disable-unsafe<br>
-du </p>
will raise an error if unsafe lines are encountered (which requires me to add more functionality)<br>
<p>--disable-warnings<br>
-dw </p>
will disable unsafe warnings
