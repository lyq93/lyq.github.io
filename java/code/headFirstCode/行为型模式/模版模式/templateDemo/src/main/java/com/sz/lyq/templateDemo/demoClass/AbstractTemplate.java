package com.sz.lyq.templateDemo.demoClass;

public abstract class AbstractTemplate {

    /**
     * 这是模版方法，加final不允许修改
     */
    public final void templateMethod() {
        methodA();
        //基类的默认hock方法实现为true
        if(hock()) {
            methodB();
        }
        methodC();
        methodD();
    }

    /**
     * 当前类实现，如果不允许修改的话，可以加final
     */
    public final void methodA() {
        System.out.println("this is methodA");
    }

    /**
     * 当前类实现，如果不允许修改的话，可以加final
     */
    public final void methodB() {
        System.out.println("this is methodB");
    }

    /**
     * 由子类实现的方法
     */
    public abstract void methodC();

    /**
     * 由子类实现的方法
     */
    public abstract void methodD();

    /**
     * 这是一个钩子方法，可以在模版方法中当条件进行逻辑判断
     * 增加模版方法的灵活度
     * @return
     */
    public boolean hock(){
        return true;
    }
}
