## 并发编程基础

### 线程的概念

> 线程是进程的一个实体，线程不会单独存在。进程是系统进行资源分配和调度的基本单位，而线程是进程的一个执行的路径。一个进程中至少有一个线程，多个线程共享进程的内存。

### 线程的创建方式

- 继承Thread类

> ```java
> package com.sz.lyq.ThreadDemo.createThread;
> 
> /**
>  * 继承的方式创建线程
>  */
> public class ExtendThread extends Thread {
>     @Override
>     public void run() {
>         System.out.println("this is child thread");
>     }
> 
>     public static void main(String[] args) {
>         new ExtendThread().start();
>     }
> }
> ```

> 1、使用继承的方式，那么就有继承的局限性
>
> 2、通过继承的方式相当于是把线程和任务进行绑定，如果说要2个不同的线程执行同一份任务，需要创建2个线程类

- 实现Runnable接口

> ```java
> package com.sz.lyq.ThreadDemo.createThread;
> 
> /**
>  * 实现runnable的方式创建线程
>  */
> public class RunnableThread implements java.lang.Runnable {
>     @Override
>     public void run() {
>         System.out.println("this is child Thread");
>     }
> 
>     public static void main(String[] args) {
>         RunnableThread runnableThread = new RunnableThread();
>         new Thread(runnableThread).start();
>     }
> }
> ```

> 1、接口编程，避免了继承的局限性
>
> 2、线程与任务解耦

- FutureTask方式实现Callable接口

> ```java
> package com.sz.lyq.ThreadDemo.createThread;
> 
> import java.util.concurrent.Callable;
> import java.util.concurrent.FutureTask;
> 
> /**
>  * 实现callable接口创建线程
>  * 看Thread类的构造函数并没有参数是futureTask的，futureTask为什么可以作为构造函数的参数传入
>  * 通过看FutureTask的源码可以知道，futureTask本质也是个Runnable
>  */
> public class CallableThread implements Callable<String> {
>     @Override
>     public String call() throws Exception {
>         return "hello";
>     }
> 
>     public static void main(String[] args) {
> 
>         FutureTask<String> stringFutureTask = new FutureTask<>(new CallableThread());
>         new Thread(stringFutureTask).start();
> 
>         try {
>             if(stringFutureTask.isDone()){
>                 String result = stringFutureTask.get();
>                 System.out.println(result);
>             }
>         } catch (Exception e) {
>             e.printStackTrace();
>         }
>     }
> }
> ```

> 1、FutureTask实现了RunnableFuture接口，RunnableFuture接口继承自Runnable和Future接口
>
> 2、FutureTask的方式相较于Runnable的方式，多了返回值

### wait函数

- 属于Object类的方法
- 未获取锁的情况下调用会抛出IllegalMonitorStateException
- 调用interrupt方法会抛出InterruptedException
- 线程调用共享对象的wait方法时，只会释放当前共享对象的锁，不会释放线程持有的其他共享对象的锁

> ```java
> package com.sz.lyq.ThreadDemo.waitDemo;
> 
> /**
>  * 当一个线程持有多个共享资源的时候，调用共享对象的wait方法只会释放当前共享对象的锁，
>  * 当前线程持有的其他共享对象的锁是不会释放的
>  * 同时，该例子也证明了当线程挂起的时候，如果有其他线程调用了interrupt方法的话，
>  * 会抛出InterruptedException
>  */
> public class releaseLockDemo {
>     // 资源A
>     private static volatile Object resourceA = new Object();
>     // 资源B
>     private static volatile Object resourceB = new Object();
> 
>     public static void main(String[] args) {
>         Thread t1 = new Thread(new Runnable() {
>             @Override
>             public void run() {
>                 synchronized (resourceA){
>                     System.out.println("thread-1 get ResourceA");
>                     synchronized (resourceB){
>                         System.out.println("thread-1 get ResourceB");
>                         try {
>                             // 挂起
>                             System.out.println("thread-1 release ResourceA");
>                             resourceA.wait();
>                         } catch (InterruptedException e) {
>                             e.printStackTrace();
>                         }
>                     }
>                 }
>             }
>         });
> 
>         Thread t2 = new Thread(new Runnable() {
>             @Override
>             public void run() {
>                 synchronized (resourceA){
>                     System.out.println("thread-2 get ResourceA");
>                     synchronized (resourceB){
>                         System.out.println("thread-2 get ResourceB");
>                         try {
>                             // 挂起
>                             resourceA.wait();
>                         } catch (InterruptedException e) {
>                             e.printStackTrace();
>                         }
>                     }
>                 }
>             }
>         });
> 
>         t1.start();
>         t2.start();
> 
>         try {
>             t1.join();
>             t2.join();
>         } catch (InterruptedException e) {
>             e.printStackTrace();
>         }
> 
>         System.out.println("thread-main is done");
>     }
> }
> ```

### 虚假唤醒概念

> 一个线程可以从挂起状态变为可运行状态，即使没有调用notify方法，notifyAll方法，或者被中断。
>
> 避免的方法：可以不停的去测试唤醒线程的条件是否满足

### Notify函数

#### 概念

> 随机唤醒一个线程，唤醒的线程不会马上执行，而是需要去竞争锁

#### 代码演示

> ..

### Join函数

#### 概念

> 等待线程执行完毕的一个方法，属于Thread类的方法

#### 代码演示

> ..

### Sleep函数

- 让出线程的执行权，但不会释放锁

> ..

- 线程在sleep期间，调用interrupt方法，会使线程抛出InterruptedException异常并返回

> ..

### 线程中断相关方法

- void interrupt()

> 当调用了wait，sleep，join等函数使线程挂起时，其他线程调用了该方法的话，会使线程异常返回，否则只是设置中断标志，线程会继续执行

- boolean isInterrupted()

> 检测调用该方法的线程是否被中断

- boolean interrupted()

> 1、发现中断，清除中断标志
>
> 2、检测当前线程是否被中断，当前线程指的不是调用该方法的线程

> code !







