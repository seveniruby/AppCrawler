package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.plugin.junit5.AllureTemplate;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;


public class GenerateClassByJavassist {
    String ClassName, path;
    public GenerateClassByJavassist(String ClassName, String path) throws Exception{
        this.ClassName = ClassName;
        this.path = path;
        init(path);
    }
    public void init(String path) throws Exception {

        String activity = "com.xueqiu.android." + ClassName;
        System.out.println(activity);
        //ClassPool：CtClass对象的容器
        ClassPool pool = ClassPool.getDefault();

        //通过ClassPool生成一个public新类ClassName
        CtClass ctClass = pool.makeClass(ClassName + "Tests");
        //声明父类
        ctClass.setSuperclass(pool.get(AllureTemplate.class.getName()));

        //添加构造函数
        //CtConstructor ctConstructor = new CtConstructor(new CtClass[]{pool.get("java.lang.String")}, ctClass);
        CtConstructor ctConstructorNull = new CtConstructor(new CtClass[]{}, ctClass);
        StringBuffer buffer = new StringBuffer();
        buffer.append("com.testerhome.appcrawler.Report report=com.testerhome.appcrawler.ReportFactory.genReport(\"junit5\");\n" +
                "        com.testerhome.appcrawler.data.AbstractElementStore store=report.loadResult(\"E://elements.yml\");\n" +
                "        com.testerhome.appcrawler.ReportFactory.initStore(store);\n" +
                "        this.pageName=\""+ activity +"\";\n" +
                "        com.testerhome.appcrawler.ReportFactory.showCancel_$eq(true);");
        System.out.println("{\n"+ buffer.toString() +"\n}");
        //ctConstructor.setBody("{"+ buffer.toString() +"}");
        ctConstructorNull.setBody("{"+ buffer.toString() +"}");
        //把构造函数添加到新的类中
        ctClass.addConstructor(ctConstructorNull);
        //ctClass.addConstructor(ctConstructor);
        System.out.println("生成Activity类开始...");
        //把生成的class文件写入文件夹
        ctClass.writeFile(path);
        System.out.println("生成Activity类完毕...");

    }

}
