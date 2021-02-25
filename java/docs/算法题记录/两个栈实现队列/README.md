## 题目

> 使用两个栈实现一个队列。实现队尾新增元素、队头删除元素的功能。

## 思路

> 已知，栈先进后出，队列先进先出。
>
> 新增元素的时候，把元素压入栈1中。删除元素的时候，把栈1的元素出栈压入到栈2中，
>
> 从栈2的栈顶删除的元素就是最先新增的元素。

## 实现

> ```java
> public class QueueByStack {
>     private MyStack stack1;
>     private MyStack stack2;
> 
>     public QueueByStack() {
>         stack1 = new MyStack();
>         stack2 = new MyStack();
>     }
> 
>     /**
>      * 队列新增操作
>      *
>      * @param element
>      */
>     public void append(int element) {
>         stack1.push(element);
>     }
> 
>     /**
>      * 队列删除操作
>      *
>      * @throws Exception
>      */
>     public void delete() throws Exception {
>         if (CollectionUtils.isEmpty(stack2.elementList)) {
>             while (!CollectionUtils.isEmpty(stack1.elementList)) {
>                 stack2.push(stack1.pop());
>             }
>         }
>         if (CollectionUtils.isEmpty(stack2.elementList)) {
>             throw new Exception("queue is empty");
>         }
>         stack2.pop();
>     }
> 
> }
> ```

## 扩充

> 用两个队列实现栈也是类似的。
>
> 思路就是入栈元素的时候把数据放入队列1中，出栈元素的时候，从队列1中把输入入到队列2中，最后一个数据返回出去。