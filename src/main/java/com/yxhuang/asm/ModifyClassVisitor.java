package com.yxhuang.asm;

import org.objectweb.asm.*;

/**
 * 自定义修改类
 */
public class ModifyClassVisitor extends ClassVisitor{

    public ModifyClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("ModifyClassVisitor visitField name " + name + " descriptor " + descriptor + " signature " + signature);
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ("test01".equals(name)) {
            methodVisitor = new MethodAdapter(methodVisitor, access, name, descriptor);
        }
        return methodVisitor;
    }
}
