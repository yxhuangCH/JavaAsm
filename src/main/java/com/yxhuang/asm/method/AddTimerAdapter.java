package com.yxhuang.asm.method;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class AddTimerAdapter extends ClassVisitor {

    private String owner;
    private boolean isInterface;

    public AddTimerAdapter(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        // 判断是否为接口
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (!isInterface && methodVisitor != null && name.equals("m")) { // 如果是 m() 方法，则使用自定义的 MethodVisitor
            methodVisitor = new AddTimerMethodAdapter(methodVisitor, owner);
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        // 插入成员变量 timer
        if (!isInterface){
            FieldVisitor visitor = cv.visitField(ACC_PUBLIC + ACC_STATIC , "timer",
                    "J", null, null);
            if (visitor != null){
                visitor.visitEnd();
            }
        }
        super.visitEnd();
    }
}
