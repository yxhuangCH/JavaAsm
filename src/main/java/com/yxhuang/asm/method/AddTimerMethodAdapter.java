package com.yxhuang.asm.method;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddTimerMethodAdapter extends MethodVisitor {

    private String owner;

    public AddTimerMethodAdapter(MethodVisitor methodVisitor, String owner) {
        super(Opcodes.ASM7, methodVisitor);
        this.owner = owner;
    }

    @Override
    public void visitCode() {
        // 这是方法调用前增加的内容
        mv.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC, owner, "timer", "J");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitInsn(Opcodes.LSUB);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, "timer", "J");
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println("AddTimerMethodAdapter visitInsn opcode= " + opcode);
        // Opcodes.IRETURN 或者 Opcodes.ATHROW 代表着方法调用结束
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW){
            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, "timer", "J");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitInsn(Opcodes.LADD);
        }
        mv.visitInsn(opcode);
    }
}
