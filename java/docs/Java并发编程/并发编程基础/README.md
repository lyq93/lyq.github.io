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

### 线程死锁

#### 概念

> 当两个或以上的线程在执行中，相互争夺资源而造成的互相等待的现象，在没有外力作用的情况下，一直等待下去。

#### 条件

- 互斥条件

> 指线程对已获取的资源进行排他性使用。也就是该资源只能供一个线程访问

- 请求并持有条件

> 指一个线程已经持有了一个资源，而又要去请求一个新的资源，新的资源又被其他线程所持有，所以当前线程就会被阻塞，又不释放自身持有的资源。

- 不可剥夺条件

> 指线程持有的资源在自己使用完之前不能被其他线程抢占。

- 环路等待条件

> 指发生死锁的时候，一定存在一个线程-资源的环形链，即线程A等待线程b持有的资源，线程B等待线程C持有的资源，线程C等待线程A持有的资源。

#### 避免死锁

> 要避免死锁，只要破坏产生死锁条件的其中之一就可以了。在上述4个条件中，可以破坏的是请求并持有和环路等待条件。这2个条件很大程度上跟资源的请求顺序有关系。所以，合理的资源请求顺序是可以避免死锁的。

### 用户线程和守护线程

> java中的线程分为两类，一类是用户线程，一类是守护线程。他们之间的区别是，如果不存在用户线程的情况下，JVM会退出，不管是否存在守护线程。把线程设置为守护线程，只需要调用setDaemon方法即可。

### ThreadLocal

#### 概念

> TheadLocal是JDK包提供的，提供了线程本地变量。也就是说，如果你创建一个ThreadLocal变量，那么访问这个变量的所有线程都会在本地有一个副本。当多个线程操作这个变量的时候，实际上操作的是自己本地内存里的变量，从而避免了线程安全的问题

#### 原理

> 核心就是Thread里面的threadLocals，变量是存在threadLocals里面的，而ThreadLocal更像是一个工具架子，调用set方法的时候，是通过获取到当前线程，拿到threadLocals变量，往变量里设置值。threadLocals可以理解为是一个定制版的hashMap，用hashMap的结构也就意味着，每个线程都可以关联多个ThreadLocal

#### 源码展示

- ThreadLocal#set

> ```java
> public void set(T value) {
>   	// 获取当前线程
>     Thread t = Thread.currentThread();
>   	// 获取到threadLocals变量
>     ThreadLocalMap map = getMap(t);
>   	// 如果不是第一次设置值，就直接设置，否则创建对象
>     if (map != null)
>         map.set(this, value);
>     else
>         createMap(t, value);
> }
> ```

- ThreadLocal#getMap

> ```java
> ThreadLocalMap getMap(Thread t) {
>     return t.threadLocals; // 线程的threadLocals变量
> }
> ```

- ThreadLocal#createMap

> ```java
> void createMap(Thread t, T firstValue) {
>     t.threadLocals = new ThreadLocalMap(this, firstValue);
> }
> ```

> 值得注意的一点是，从源码分析知道，变量是存在线程里面的，如果线程一直不结束，那么就有可能存在内存溢出的问题。所以，在使用完变量后，可以调用ThreadLocal的remove的方法移除变量。





