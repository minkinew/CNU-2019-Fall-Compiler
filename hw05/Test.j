.class public Test
.super java/lang/Object
; strandard initializer
.method public static sum(I)I
.limit stack 32
.limit locals 32
ldc 0
istore_1
ldc 1
istore_2
L:
iload_2
iload_0
if_icmpgt end
iload_1
iload_2
iadd
istore_1
iload_2
ldc 1
iadd
istore_2
goto L
end:
iload_1
ireturn
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 32
.limit locals 32
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc 10000
ldc 9900
isub
istore_1
iload_1
invokestatic Test/sum(I)I
invokevirtual java/io/PrintStream/println(I)V
return
.end method