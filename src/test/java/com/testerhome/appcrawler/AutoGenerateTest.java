package com.ceshiren.appcrawler;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.junit.jupiter.api.Test;

/**
 * @author Naruto
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2019/5/197:54 PM
 */
public class AutoGenerateTest {
    @Test
    public void fun(){
        ClassPool pool=ClassPool.getDefault();
        try {

            CtClass cc = pool.makeClass("NarutoTest");

            CtField cf1=CtField.make("private String name;",cc);
            CtField cf2=CtField.make("private int age;",cc);

            cc.addField(cf1);
            cc.addField(cf2);


            //增加方法
            CtMethod setName=CtMethod.make("public void setName(String name){this.name = name;}",cc);
            CtMethod getName=CtMethod.make("public String getName(){return name;}",cc);

            cc.addMethod(setName);
            cc.addMethod(getName);

            CtMethod setAge=CtMethod.make("public void setAge(int age){this.age = age;}",cc);
            CtMethod getAge=CtMethod.make("public int getAge(){return age;}",cc);

            cc.addMethod(setAge);
            cc.addMethod(getAge);


            //测试方法
            CtMethod testMethod=CtMethod.make("public void fun(){ org.junit.jupiter.api.Assertions.assertTrue(false); }",cc);
            cc.addMethod(testMethod);

            //测试方法
            CtMethod testsayMethod=CtMethod.make("public void say(){System.out.println(\"Hello World\"); org.junit.jupiter.api.Assertions.assertTrue(false); }",cc);
            cc.addMethod(testsayMethod);

            //构造方法
            /*CtConstructor ccs=new CtConstructor(new CtClass[]{CtClass.intType,pool.get("java.lang.String")}, cc);
            ccs.setBody("System.out.println(\"HelloWorld\");");
            cc.addConstructor(ccs);*/


            CtMethod funMethodDescriptor = cc.getDeclaredMethod("fun");

            ClassFile ccFile = cc.getClassFile();
            ConstPool constpool = ccFile.getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            Annotation annot = new Annotation("org.junit.jupiter.api.Test", constpool);
            attr.addAnnotation(annot);

            funMethodDescriptor.getMethodInfo().addAttribute(attr);


            CtMethod sayMethodDescriptor = cc.getDeclaredMethod("fun");

            ClassFile sayccFile = cc.getClassFile();
            ConstPool sayconstpool = sayccFile.getConstPool();
            AnnotationsAttribute sayattr = new AnnotationsAttribute(sayconstpool, AnnotationsAttribute.visibleTag);
            Annotation sayannot = new Annotation("org.junit.jupiter.api.Test", sayconstpool);
            attr.addAnnotation(sayannot);

            sayMethodDescriptor.getMethodInfo().addAttribute(attr);




            cc.writeFile("/tmp/AA");



        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Test
    public void fun2()throws  Exception{
        ClassPool pool=ClassPool.getDefault();
        pool.importPackage(" org.junit.Assert");

        CtClass cc = pool.makeClass("GaaraTest");


        //CtClass superClazz = pool.getOrNull("demo.Demo2Test");



        //测试方法
        CtMethod testMethod=CtMethod.make("public void fun(){System.out.println(new Integer(2).equals(new Integer(1)));}",cc);
        cc.addMethod(testMethod);

        //测试方法
        CtMethod testsayMethod=CtMethod.make("public void say(){System.out.println(\"Hello World\");}",cc);
        cc.addMethod(testsayMethod);


        //为fun添加@Test
        CtMethod funMethodDescriptor = cc.getDeclaredMethod("fun");
        ClassFile ccFile = cc.getClassFile();
        ConstPool constpool = ccFile.getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation("org.junit.jupiter.api.Test", constpool);
        attr.addAnnotation(annot);

        funMethodDescriptor.getMethodInfo().addAttribute(attr);


        //cc.setSuperclass(superClazz);
        //为say添加@Test
        CtMethod sayMethodDescriptor = cc.getDeclaredMethod("say");

        ClassFile sayccFile = cc.getClassFile();
        ConstPool sayconstpool = sayccFile.getConstPool();
        AnnotationsAttribute sayattr = new AnnotationsAttribute(sayconstpool, AnnotationsAttribute.visibleTag);
        Annotation sayannot = new Annotation("org.junit.jupiter.api.Test", sayconstpool);
        attr.addAnnotation(sayannot);

        sayMethodDescriptor.getMethodInfo().addAttribute(attr);

        cc.writeFile("/tmp/AA");

    }


    @Test
    public void fun3(){
        //System.out.println(System.getProperty("user.dir"));
        System.out.println(Integer.valueOf(2).equals(Integer.valueOf(1)));
    }
}
