package com.yxhuang.asm;

import com.yxhuang.asm.method.AddTimerAdapter;
import com.yxhuang.asm.method.MyMethod;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.*;

public class Test {

    public static void main(String[] args) throws Exception {
        String path = Test.class.getClassLoader().getResource("").getPath();
        System.out.println("path " + path);


//        print(path);
//        addFileAndMethod(path);
//        removeFieldAndMethod(path);


//        modifyMethod(path);
//        modifyMethod2(path);
//        generaClass(path);
        generaClass2(path);
//        System.out.println("====\n");
//        MyMain myMain2 = new MyMain();
//        myMain2.test01();

//        addTimer(path);
//        MyMethod myMethod = new MyMethod();
//        myMethod.m();
    }

    /**
     * 打印
     *
     * @param path
     */
    private static void print(String path) {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {

            /**
             * 源文件的信息
             * @param source
             * @param debug
             */
            @Override
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
                System.out.println("visitSource source " + source + " debug" + debug);
            }

            @Override
            public void visitOuterClass(String owner, String name, String descriptor) {
                super.visitOuterClass(owner, name, descriptor);
                System.out.println("visitOuterClass owner " + owner + " name" + name + " descriptor " + descriptor);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                System.out.println("visitAnnotation descriptor " + descriptor + " visible " + visible);
                return super.visitAnnotation(descriptor, visible);
            }

            @Override
            public void visitAttribute(Attribute attribute) {
                super.visitAttribute(attribute);
                System.out.println("Attribute attribute " + attribute.toString());
            }

            /**
             *  拿到类的信息， 然后对满足条件的类进行过滤
             */
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                System.out.println("visit version=" + version + " access=" + access + " name=" + name
                        + "\nsignature=" + signature + "  superName=" + superName);
                for (String interfaceStr : interfaces) {
                    System.out.println("visit interfaces=" + interfaceStr);
                }
            }

            // 内部类信息
            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                super.visitInnerClass(name, outerName, innerName, access);
                System.out.println("visitInnerClass name " + name + " outerName" + outerName
                        + " innerName " + innerName + " access " + access);
            }

            // 遍历类的成员变量信息
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                System.out.println("visitField name " + name + " descriptor " + descriptor + " signature " + signature);
                return super.visitField(access, name, descriptor, signature, value);
            }

            // 类的方法信息, 拿到需要修改的方法，然后进行修改操作
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                System.out.println("visitMethod name " + name + " descriptor " + descriptor + " signature " + signature);
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            // 遍历类中的成员信息结束
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
     *
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
                        "xyz",  // 成员变量的名称
                        "Ljava/lang/String", // 成员变量的描述符
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
        // 重新复写原来的 class 文件
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/MyMain.class"), bytesModified);
    }

    /**
     * 移除字段和方法
     *
     * @param path
     */
    private static void removeFieldAndMethod(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {
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
     *
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
                // 新增加 test04
                MethodVisitor methodVisitor = this.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "test04",
                        "(I)I",
                        null,
                        null
                );
                if (methodVisitor != null) {
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

    /**
     * 在方法里面添加内容
     * @param path
     * @throws IOException
     */
    private static void modifyMethod2(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.MyMain");
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor modifyClassVisitor = new ModifyClassVisitor(classWriter);
        classReader.accept(modifyClassVisitor, 0);
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

        // 生成类
        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
                "com/yxhuang/asm/Comparable", null, "java/lang/Object",
                new String[]{"com/yxhuang/asm/Mesurable"});

        // 生成 less 成员变量
        classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                "less",
                "I",
                null,
                new Integer(-1))
                .visitEnd();

        // 生成 compareTo 方法
        classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT,
                "compareTo",
                "(Ljava/lang/Object;)I",
                null,
                null)
                .visitEnd();
        classWriter.visitEnd();
        byte[] b = classWriter.toByteArray();

        // 生成 class 文件
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/Comparable.class"), b);
        //　使用　类加载器加载
        MyClassLoader myClassLoader = new MyClassLoader();
        Class clazz = myClassLoader.defineClass("com.yxhuang.asm.Comparable", b);
        System.out.println("clazz name: " + clazz.getSimpleName());
    }

    // 使用  GeneratorAdapter 生成类
    private static void generaClass2(String path) throws IOException {
        ClassWriter classWriter = new ClassWriter(0);
        // 生成类
        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "Example", null, "java/lang/Object", null);

        // 生成构造方法
        Method method = Method.getMethod("void <init> ()");
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, method, null, null, classWriter);
        mg.loadThis();
        mg.invokeConstructor(Type.getType(Object.class), method);
        mg.returnValue();
        mg.endMethod();

        // 生成 main 方法
        method = Method.getMethod("void main (String[])");
        mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, method, null, null, classWriter);
        mg.getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
        mg.push("Hello world!");
        mg.invokeVirtual(Type.getType(PrintStream.class),
                Method.getMethod("void println (String)"));
        mg.returnValue();
        mg.endMethod();

        classWriter.visitEnd();

        // 写入本地磁盘
        byte[] bytesModified = classWriter.toByteArray();
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/Example.class"), bytesModified);
    }

    private static void addTimer(String path) throws IOException {
        byte[] bytes = getBytes(path, "com.yxhuang.asm.method.MyMethod");
        ClassReader classReader = new ClassReader(bytes);
        // 这里是COMPUTE_FRAMES 自动计算栈帧，如果是 0 会报错的，因为我们在方法里面增加内容，必然导致栈帧的深度改变
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor addTimerVisitor = new AddTimerAdapter(classWriter);
        classReader.accept(addTimerVisitor, 0);
        byte[] bytesModified = classWriter.toByteArray();
        FileUtils.writeByteArrayToFile(new File(path + "com/yxhuang/asm/method/MyMethod.class"), bytesModified);

    }
}
