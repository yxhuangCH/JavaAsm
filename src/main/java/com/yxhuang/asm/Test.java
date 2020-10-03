package com.yxhuang.asm;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;

import java.io.*;
import java.net.URL;
import java.nio.file.FileVisitor;

public class Test {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello word");
        String path = Test.class.getClassLoader().getResource("").getPath();
        System.out.println("path " + path);

//        print(path);
//        addFileAndMethod(path);
//        removeFieldAndMethod(path);
//        MyMain myMain = new MyMain();
//        System.out.println("myMain " + myMain.b);
//        myMain.test01();

//        modifyMethod(path);
        generaClass(path);
    }

    /**
     * 打印
     * @param path
     */
    private static void print(String path) {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {

            @Override
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
                System.out.println("visitSource source " + source + " debug"  + debug );
            }

            @Override
            public void visitOuterClass(String owner, String name, String descriptor) {
                super.visitOuterClass(owner, name, descriptor);
                System.out.println("visitOuterClass owner " + owner + " name"  + name + " descriptor "  + descriptor);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                System.out.println("visitAnnotation descriptor " + descriptor + " visible " +  visible);
                return super.visitAnnotation(descriptor, visible);
            }

            @Override
            public void visitAttribute(Attribute attribute) {
                super.visitAttribute(attribute);
                System.out.println("Attribute attribute " + attribute.toString() );
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                super.visitInnerClass(name, outerName, innerName, access);
                System.out.println("visitInnerClass name " + name + " outerName"  + outerName
                        + " innerName " + innerName +  " access " + access);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                System.out.println("visitField name " + name + " descriptor " + descriptor + " signature " + signature);
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                System.out.println("visitMethod name " + name + " descriptor " + descriptor + " signature " + signature);
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                System.out.println("visitEnd");
            }
        };
        classReader.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
    }

    /**
     * 增加字段
     * @param path
     */
    private static void addFileAndMethod(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                System.out.println("addFile visitEnd");

                // 增加一个字段
                FieldVisitor fieldVisitor = this.visitField(
                        Opcodes.ACC_PUBLIC,
                        "xyz",
                        "Ljava/lang/String",
                        null,
                        null);
                if (fieldVisitor != null) {
                    fieldVisitor.visitEnd();
                }

                // 增加一个方法
                MethodVisitor methodVisitor = this.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "test03",
                        "(ILjava/lang/String;)V",
                        null,
                        null
                );
                if (methodVisitor != null) {
                    methodVisitor.visitEnd();
                }
            }
        };
        classReader.accept(classVisitor, classReader.SKIP_DEBUG);
        byte[] bytesModified = classWriter.toByteArray();
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/MyMain2.class"), bytesModified);
    }

    /**
     * 移除字段和方法
     * @param path
     */
    private static void removeFieldAndMethod(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
//                if ("abc".equals(name)) { // 过滤 abc 字段
//                    return null;
//                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ("test01".equals(name)) {
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        classReader.accept(classVisitor, classReader.SKIP_DEBUG);
        byte[] bytesModified = classWriter.toByteArray();
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/MyMain.class"), bytesModified);
    }

    /**
     * 修改方法内容
     * @param path
     */
    private static void modifyMethod(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ("test01".equals(name)) {  // 删除 test01 方法
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                System.out.println("addFile visitEnd");
                // 新增加 test01
                MethodVisitor methodVisitor = this.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "test04",
                        "(I)I",
                        null,
                        null
                );
                if (methodVisitor != null){
                    methodVisitor.visitCode();
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
                    methodVisitor.visitIntInsn(Opcodes.BIPUSH, 100);
                    methodVisitor.visitInsn(Opcodes.IADD);
                    methodVisitor.visitInsn(Opcodes.IRETURN);
                    methodVisitor.visitEnd();
                }
//                // 增加一个方法
//                MethodVisitor methodVisitor = this.visitMethod(
//                        Opcodes.ACC_PUBLIC,
//                        "test03",
//                        "(ILjava/lang/String;)V",
//                        null,
//                        null
//                );CoreValue
//                if (methodVisitor != null) {
//                    methodVisitor.visitEnd();
//                }
            }
        };
        classReader.accept(classVisitor, 0);
        byte[] bytesModified = classWriter.toByteArray();
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/MyMain.class"), bytesModified);

    }

    private static byte[] getBytes(String path, String className) {
        FileInputStream fis;
        String fileName = path + className.replace('.', File.separatorChar) + ".class";
        System.out.println("getTypeFromBasePath fileName :" + fileName);

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        BufferedInputStream bis = new BufferedInputStream(fis);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            int c = bis.read();
            while (c != -1) {
                out.write(c);
                c = bis.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return out.toByteArray();

    }

    /**
     * 生成一个类
     */
    private static void generaClass(String path) throws IOException {
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
                "com/yxhuang/asm/Comparable", null, "java/lang/Object",
                new String[]{"com/yxhuang/asm/Mesurable"});
        classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                "LESS",
                "I",
                null,
                new Integer(-1))
                .visitEnd();
        classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
                "compareTo",
                "(Ljava/lang/Object;)I",
                null,
                null)
                .visitEnd();
        classWriter.visitEnd();
        byte[] b = classWriter.toByteArray();

        // 生成 class 文件
//        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/Comparable.class"), b);
        //　使用　类加载器加载
        MyClassLoader myClassLoader = new MyClassLoader();
        Class clazz = myClassLoader.defineClass("com.yxhuang.asm.Comparable", b);
        System.out.println( "clazz name: " + clazz.getSimpleName());

    }
}
