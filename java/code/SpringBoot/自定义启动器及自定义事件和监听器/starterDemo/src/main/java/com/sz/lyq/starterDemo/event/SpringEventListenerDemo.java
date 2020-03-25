package com.sz.lyq.starterDemo.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringEventListenerDemo {
    public static void main(String[] args) {
        /**
         * 该注解上下文通过查看ApplicationContext实现可得知
         * 并且通过查看源码的方式可知有两种注册监听的方式（1.构造器直接传入component类型，类似于Application run
         * 2.通过API的方式，即register方法）
         */
        //通过构造器的方式注册listener，这种方式在构造器内部已经调用了refresh方法，不需要额外再次通过API方式调用，否则会报异常
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyApplicationListener.class);

        //通过API的方式注册listener
//        context.register(MyApplicationListener.class);

//        context.refresh();

        context.publishEvent(new MyApplicationEvent("hello world"));

    }

    public static class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {

        @Override
        public void onApplicationEvent(MyApplicationEvent event) {
            System.out.println("MyApplicationListener receives event resource:" + event.getSource());
        }
    }

    public static class MyApplicationEvent extends ApplicationEvent {

        /**
         * Create a new {@code ApplicationEvent}.
         *
         * @param source the object on which the event initially occurred or with
         *               which the event is associated (never {@code null})
         */
        public MyApplicationEvent(Object source) {
            super(source);
        }
    }
}
