## java
* [1.并发编程线程基础](#1)



<h2 id="1">1.并发编程线程基础</h2>
  1.什么是线程？<br>
线程是进程中的一个实体，是进程的更细粒度的一个执行单元。进程是一次程序的运行过程，一个进程中至少有一个线程。一个进程中的所有线程共享进程的堆和方法区的资源，每个线程也有自己的私有程序计数器和栈空间。<br>
  2.线程创建与运行的方式？<br>
  Java中有3种线程创建方式，分别为继承Thread类，实现Runnable接口，使用FutureTask方式。
  [link](https://github.com/lyq93/lyq.github.io/blob/master/java/code/java%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B%E4%B9%8B%E7%BE%8E/%E7%BA%BF%E7%A8%8B%E5%88%9B%E5%BB%BA%E7%9A%84%E6%96%B9%E5%BC%8F)
