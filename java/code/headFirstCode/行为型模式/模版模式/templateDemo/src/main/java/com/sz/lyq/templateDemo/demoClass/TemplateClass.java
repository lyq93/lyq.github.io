package com.sz.lyq.templateDemo.demoClass;

public class TemplateClass extends AbstractTemplate {
    @Override
    public void methodC() {
        System.out.println("this is methodC");
    }

    @Override
    public void methodD() {
        System.out.println("this is methodD");
    }

    /**
     * 钩子方法，可选实现
     * @return
     */
    @Override
    public boolean hock() {
        return false;
    }
}
