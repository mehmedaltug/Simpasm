<h2>Simpasm NASM Compiler</h2>

<h3>Why?</h3>
I have noticed while writing my operating system that assembly is very easy to lose track of, and it is somewhat hard to write a filesystem and switch to C, so I decided to make my own mini programming language.<br>

<h3>Features</h3>
<p>It makes abstractions that makes life easier.<br>
You can write this simple if statement and someone can understand it easily:</p>

```
if sp <= 10 {
  ax + 5
}
```

After which it translates to this:

```
cmp sp, 10
jg if_X
add ax, 5
if_X:
```
And you can define functions that are simpler to use:

```
fn add(ax, bx) {
    ax + bx
}

add(4, cx)
```
Which are a pain to use originally:

```
jmp add_exit
add:
add ax, bx
ret
add_exit:

mov ax, 4
mov bx, cx
call add
```

<h3>Example Syntax</h3>

```
ax = 7
bx + dx
sp - 0x12
/ dx
* bx
# Example comment here

segment test
cx + 4 
if cx < 50 {
    goto test
}

loop ax > 40 {
    ax - 1
}

#Result will be in ax
fn add_nums(ax, bx, cx){
    push all
    push bx, cx
    bx + cx
    ax + bx
    pop bx, cx, all
}
add_nums(4, 20, 40)
```
<p>In addition to these if the compiler comes across a line that does not match any of the types predefined it assumes it is a valid NASM line and includes those in the output but marks them as unsafe and gives a warning.<br></p>
<p>You can disable unsafe lines with "--disable-unsafe" or "-du" flags
<br>And you can disable warnings with "disable-warnings" or "-dw" flags.</p>

<h3>Build</h3>
Intellij has a configured artifact to build the jar file using the Main.java and its requirements or if you prefer you can build the jar file yourself using the manifest.mf file. After building the jar file you can use windows or unix build scripts I provided in the root directory to create standalone executables if you prefer.<br>

<h3>Implementation</h3>
For general operations, compiler uses splitting and keywords to determine the suitable replacement.<br>
For if statements and loops it uses a custom Stack class that keeps track of the position we are in to determine which lines are in loops/ifs.<br>
For function definitions and arguments, compiler uses a custom Dictionary class to save the function signatures and use the arguments accordingly.
