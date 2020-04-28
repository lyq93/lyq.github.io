package com.sz.lyq.templateDemo.demoClass;

public class TemplateDemoMain {

    public static void main(String[] args) {
        TemplateClass templateClass = new TemplateClass();
        //通过子类调用父类的模版方法，执行定义好的算法
        templateClass.templateMethod();
    }

}
