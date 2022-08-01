package com.yrc.juc.e_共享模型之管程;

/**
 * 观察下面代码的汇编指令
 * 使用 javac -encoding utf8 -g LockPrinciple_AssemblyInstruction.java 编译字节码文件
 * 使用 javap -v LockPrinciple_AssemblyInstruction.class 反编译汇编指令文件
 */
public class LockPrinciple_AssemblyInstruction {
    // 锁对象
    static final Object lock = new Object();
    // 共享变量
    static  int counter = 0;

    public static void main(String[] args) {
        synchronized (lock) {
            counter++;
        }
    }
}

/*
Classfile /D:/GitRepository/java/juc/src/main/java/com/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction.class
  Last modified 2022-7-29; size 786 bytes
  MD5 checksum 192f819228c6ac63e44d1747d4a03004
  Compiled from "LockPrinciple_AssemblyInstruction.java"

public class com.yrc.juc.e_共享模型之管程.LockPrinciple_AssemblyInstruction
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #4.#28         // java/lang/Object."<init>":()V
   #2 = Fieldref           #5.#29         // com/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction.lock:Ljava/lang/Object;
   #3 = Fieldref           #5.#30         // com/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction.counter:I
   #4 = Class              #31            // java/lang/Object
   #5 = Class              #32            // com/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction
   #6 = Utf8               lock
   #7 = Utf8               Ljava/lang/Object;
   #8 = Utf8               counter
   #9 = Utf8               I
  #10 = Utf8               <init>
  #11 = Utf8               ()V
  #12 = Utf8               Code
  #13 = Utf8               LineNumberTable
  #14 = Utf8               LocalVariableTable
  #15 = Utf8               this
  #16 = Utf8               Lcom/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction;
  #17 = Utf8               main
  #18 = Utf8               ([Ljava/lang/String;)V
  #19 = Utf8               args
  #20 = Utf8               [Ljava/lang/String;
  #21 = Utf8               StackMapTable
  #22 = Class              #20            // "[Ljava/lang/String;"
  #23 = Class              #31            // java/lang/Object
  #24 = Class              #33            // java/lang/Throwable
  #25 = Utf8               <clinit>
  #26 = Utf8               SourceFile
  #27 = Utf8               LockPrinciple_AssemblyInstruction.java
  #28 = NameAndType        #10:#11        // "<init>":()V
  #29 = NameAndType        #6:#7          // lock:Ljava/lang/Object;
  #30 = NameAndType        #8:#9          // counter:I
  #31 = Utf8               java/lang/Object
  #32 = Utf8               com/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction
  #33 = Utf8               java/lang/Throwable
{
  static final java.lang.Object lock;
    descriptor: Ljava/lang/Object;
    flags: ACC_STATIC, ACC_FINAL

  static int counter;
    descriptor: I
    flags: ACC_STATIC

  public com.yrc.juc.e_共享模型之管程.LockPrinciple_AssemblyInstruction();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 8: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/yrc/juc/e_共享模型之管程/LockPrinciple_AssemblyInstruction;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=3, args_size=1
         0: getstatic     #2                  // Field lock:Ljava/lang/Object;
         3: dup
         4: astore_1
         5: monitorenter
         6: getstatic     #3                  // Field counter:I
         9: iconst_1
        10: iadd
        11: putstatic     #3                  // Field counter:I
        14: aload_1
        15: monitorexit
        16: goto          24
        19: astore_2
        20: aload_1
        21: monitorexit
        22: aload_2
        23: athrow
        24: return
      Exception table:
         from    to  target type
             6    16    19   any
            19    22    19   any
      LineNumberTable:
        line 15: 0
        line 16: 6
        line 17: 14
        line 18: 24
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      25     0  args   [Ljava/lang/String;
      StackMapTable: number_of_entries = 2
        frame_type = 255 // full_frame
          offset_delta = 19
                  locals = [ class "[Ljava/lang/String;", class java/lang/Object ]
        stack = [ class java/lang/Throwable ]
        frame_type = 250 // chop
        offset_delta = 4

static {};
        descriptor: ()V
        flags: ACC_STATIC
        Code:
        stack=2, locals=0, args_size=0
        0: new           #4                  // class java/lang/Object
        3: dup
        4: invokespecial #1                  // Method java/lang/Object."<init>":()V
        7: putstatic     #2                  // Field lock:Ljava/lang/Object;
        10: iconst_0
        11: putstatic     #3                  // Field counter:I
        14: return
        LineNumberTable:
        line 10: 0
        line 12: 10
        }
        SourceFile: "LockPrinciple_AssemblyInstruction.java"
 */
