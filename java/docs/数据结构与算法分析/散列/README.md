# 散列

## 概念

> 散列表可以理解存放一些项的固定大小的数组。
>
> 但不同于一般数组的实现特点是，每一个项的插入，都需要经过散列函数计算后得到相应的散列位置后存储到对应的位置。
>
> 理想情况下，这个散列函数应该是计算简单的且每一项都应该映射到不同的位置，但是因为输入的项是无限的，表的大小是有限的，所以一定会出现一种情况就是，多个输入项经过散列函数计算之后得到相同的位置，这就产生了冲突。
>
> 当产生冲突的时候，需要选择一个函数来决定应该如何处理

### 散列函数

> - 对于项是整数的情况来说，散列函数一般是项跟表大小取模来作为散列函数，但是表的大小需要有一点考量。好的办法通常是让表的大小为素数。一方面，假设输入的项的个位数都是0，而表的大小为10，这种就是不好的选择。另一方面，涉及到后续解决冲突对于项的处置涉及到的问题。
> - 对于项是字符串的情况来说，散列函数的选择就需要仔细考虑
>   - 使用字符串中的字符ASCII码值的和与表取模，这种实现是非常简单的，但是存在非常明显的问题。假设表的大小非常大，且输入的字符串最长为8字符，那么由于ASCII码值最大为127，所以散列函数产生的最大值是127*8，也就是说，经过这种散列函数对数据的存储，是不均匀的
>   - 只计算字符串的前三个字符的ASCII乘以某一个固定数值的N次方，例如第一个字符的ASCII值*27的0次方，第二个字符就是乘以27的1次方。这种实现方式同样也是不合适的。原因在于，3个字母的组合是有限的，意味着分布依然是不均匀的
>   - 某一个固定数值*上一个字母的hash值+当前字母的ASCII值，这种实现根据hash值的不断累加参与计算达到随机性。另外值得一提的是，这种计算方式在输入的项的长度特别长的时候，会花费过多时间。所以甚至可以采取一个折中的处理方式就是，只使用奇数位字符参与计算。典型的以对均匀分布的轻微干扰来换取时间收益

## 分离链接法

### 做法

> 将散列到同一个位置的项使用链表存储。
>
> 执行查找操作，通过散列函数来确定遍历哪个链表，再从确定的链表中执行一次查找
>
> 执行插入操作，需要先检查元素是否存在对应的链表中，如果不存在则执行插入，有个细节的处理就是，基于一个新插入的元素最有可能在最近被访问，所以应该是往链表的前端插入

### 代码实现

> ```java
> public class LinkedListDemo {
> 
>     public LinkedListDemo() {
>         this(default_size);
>     }
> 
>     public LinkedListDemo(int tableSize) {
>         this.tableSize = tableSize;
>         arrays = new ArrayList<>(tableSize);
>         for(int i = 0; i < tableSize; i++) {
>             arrays.set(i, new LinkedList<>());
>         }
>         this.current = 0;
>     }
> 
>     private List<LinkedList<Integer>> arrays;
>     private static final int default_size = 11;
>     private int current;
>     private int tableSize;
> 
>     /**
>      * 是否包含元素
>      * @param element
>      * @return
>      */
>     public boolean contains(int element) {
>         int index = this.myHash(element);
>         if (CollectionUtils.isEmpty(arrays.get(index))) {
>             return false;
>         }
>         return arrays.get(index).contains(element);
>     }
> 
>     /**
>      * 插入操作
>      * @param element
>      */
>     public void insert(int element) {
>         if (this.contains(element)) {
>            return;
>         }
>         int index = this.myHash(element);
>         LinkedList<Integer> hitElements = arrays.get(index);
>         hitElements.add(element);
>         if (++current > tableSize) {
>             // 元素与表的大小比为1，让每个链表平均只有1个节点
>             this.rehash();
>         }
>     }
> 
>     /**
>      * 删除元素
>      * @param element
>      * @return
>      */
>     public boolean remove(int element) {
>         if(!this.contains(element)) {
>             return true;
>         }
>         int index = this.myHash(element);
>         LinkedList<Integer> hitElements = arrays.get(index);
>         for(int i = 0; i < hitElements.size(); i++) {
>             if(hitElements.get(i).equals(element)) {
>                 hitElements.set(i, null);
>                 current--;
>             }
>         }
>         return true;
>     }
> 
>     /**
>      * hash函数
>      * @param element
>      * @return
>      */
>     private int myHash(int element) {
>         return element % tableSize;
>     }
> 
>     /**
>      * 重hash
>      */
>     private void rehash() {
>         List<LinkedList<Integer>> tmp = arrays;
>         arrays = new LinkedList<LinkedList<Integer>>(new ArrayList<>(arrays.size() * 2));
>         for(int i = 0; i < tmp.size(); i++) {
>             arrays.add(i, tmp.get(i));
>         }
>     }
> }
> ```
>
> 分离链接法的实现不好的地方在于：
>
> 1、使用了一些链表
>
> 2、分配新单元需要时间
>
> 3、需要对第二种数据结构提供实现

## 探测散列表

### 思路

> 使用一种数据结构，例如数组（简单）来存放元素包括冲突元素。理论上，探测散列表的表大小要比分离链接法的表要大，因为需要保证装填因子小与0.5。目的是为了出现冲突时，使用一定的规则找到空间存放冲突的元素。值得注意的一点是，因为存在发生冲突的情况，所以探测散列表的删除不能为真删除，否则，contains方法就失效了。

### 代码实现

> ```java
> public class LinkedListDemo2 {
> 
>     public LinkedListDemo2() {
>         this(default_size);
>     }
> 
>     public LinkedListDemo2(int tableSize) {
>         this.tableSize = tableSize;
>         current = 0;
>         arrays = new ArrayList<>(tableSize);
>     }
> 
>     private List<ElementClass> arrays;
>     private static final int default_size = 11;
>     private int tableSize;
>     private int current;
> 
>     // 数组存的元素结构
>     private static class ElementClass {
>         // 元素
>         private int element;
>         // 是否删除
>         private boolean isActive;
> 
>         public ElementClass(int element) {
>             this(element, true);
>         }
> 
>         public ElementClass(int element, boolean isActive) {
>             this.element = element;
>             this.isActive = isActive;
>         }
>     }
> 
>     public boolean contains(int element) {
>         int index = findIndex(element);
>         return isActive(index);
>     }
> 
>     public void insert(int element) {
>         int index = findIndex(element);
>         if(isActive(index)) {
>             return;
>         }
>         ElementClass elementClass = new ElementClass(element, true);
>         arrays.add(index, elementClass);
>         if(++current > tableSize / 2) {
>             rehash();
>         }
>     }
> 
>     public boolean remove(int element) {
>         int index = findIndex(element);
>         if (isActive(index)) {
>            arrays.get(index).isActive = false;
>         }
>         return true;
>     }
> 
> 
>     /**
>      * 查找元素方法（平方探测）
>      * @param element
>      * @return
>      */
>     private int findIndex(int element) {
>         int step = 1;
>         int index = myHash(element);
>         while (arrays.get(index) != null &&
>                 arrays.get(index).element != element) {
>             index = index + step * step;
>             if(index > tableSize) {
>                 index = index - tableSize;
>             }
>             step++;
>         }
>         return index;
>     }
> 
>     /**
>      * 元素是否存在
>      * @param index
>      * @return
>      */
>     private boolean isActive(int index) {
>         return arrays.get(index) != null &&
>                 arrays.get(index).isActive;
>     }
> 
>     private int myHash(int element) {
>         return element % tableSize;
>     }
> 
>     private void rehash() {
>         // ...
>     }
> }
> ```



