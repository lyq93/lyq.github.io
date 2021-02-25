## 题目

> 输入一个链表的头节点，从尾到头打印出每个节点的值

## 思路

> 由头到尾顺序遍历链表，打印的时候却要求是反序的。这是典型的后进先出。
>
> 1、使用栈结构来实现
>
> 2、使用递归实现，存在一个问题，当链表的长度非常长的情况下，会导致递归的层级非常的深

## 解题

> ```java
> /**
>  * 递归实现
>  * @param head
>  */
> private static void reversPrint(ListNode head) {
>     reversPrint(head.nextNode);
>     System.out.println(head.value);
> }
> 
> /**
>  * 把链表放到先进后出的栈结构中实现
>  * @param head
>  */
> private void reversPrintByStack(ListNode head) {
>     MyStack myStack = new MyStack();
>     while (head != null) {
>         myStack.push(head.value);
>         head = head.nextNode;
>     }
> 
>     while (CollectionUtils.isEmpty(myStack.elementList)) {
>         myStack.pop();
>     }
> }
> 
> /**
>  * 链表节点定义
>  */
> class ListNode {
>     private int value;
>     ListNode nextNode;
> }
> 
> /**
>  * 简单模拟一个栈结构
>  */
> class MyStack {
> 
>     private List<Integer> elementList;
>     private static final int default_size = 100;
>     private int elementPos = 0;
> 
>     public MyStack() {
>         this(default_size);
>     }
> 
>     public MyStack(int length) {
>         elementList = new ArrayList<>(length);
>     }
> 
>     public void push(int element) {
>         elementList.add(element);
>         elementPos++;
>     }
> 
>     public int pop() {
>         int result = elementList.get(elementPos - 1);
>         elementList.remove(elementPos - 1);
>         return result;
>     }
> }
> ```
>
> 简单模拟了一个链表的结构和栈的结构为了说明相应的问题。
>
> 另外出栈的时候其实需要判断是否栈空，入栈的时候需要判断是否栈满。
>
> 再简单说下栈结构的实现方式
>
> 栈的特性无非就是后进先出，实现栈的结构那么就可以用数组或者链表来实现。
>
> 完整的栈定义可以参考JAVA的Stack类。