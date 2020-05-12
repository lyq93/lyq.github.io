## LinkedList源码分析

### 简介

```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```

> 首先比较与ArrayList不同的地方：
>
> 1、继承的类不同，ArrayList继承abstractList，LinkedList继承AbstractSequentialList，举个例子来说，abstractList的add方法是通过数组实现的，AbstractSequentialList是用过迭代器的方式实现的
>
> 2、LinkedList实现了Deque接口，这个接口是一个双向队列接口，所以说LinkList是个双向链表
>
> 3、LinkedList没有实现randomAccess接口，不支持快速随机访问，也是因为底层实现的原因

```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}

/**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

/**
     * Links e as last element.
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
```

> LinkedList内部私有类Node，再通过add方法可以知道，LinkedList内部存的就是Node对象。
>
> 特点是：
>
> 1、Node对象要维护前驱节点和后驱节点的信息，Item是本身的信息，所以，LinkedList相当于是每一份数据的存储都占用了比真实数据本身更大的内存。
>
> 2、LinkedList的add方法，是在链尾追加元素。

### 获取元素

```java
/**
 * Returns the first element in this list.
 *
 * @return the first element in this list
 * @throws NoSuchElementException if this list is empty
 */
public E getFirst() {
    final Node<E> f = first;
    if (f == null)
        throw new NoSuchElementException();
    return f.item;
}

/**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

/**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

/**
     * Retrieves, but does not remove, the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
     }
```

> 区别：这四个获取头节点的方法的区别在于对链表为空的时候的处理，getFirst方法当链表为空时，直接抛出NoSuchElementException，element方法就是调的getFirst方法，所以是一样的。peek方法和peekFirst方法对链表为空时的处理是直接返回null值

### Deque接口的实现

- ConcurrentLinkedDeque 线程安全的，不允许插入null值
- LinkedBlockingDeque 线程安全的，不允许插入null值
- LinkedList 非线程安全的，允许插入null值
- ArrayDeque 非线程安全的，不允许插入null值

#### 源码

- ConcurrentLinkedDeque

```java
/**
 * Inserts the specified element at the tail of this deque.
 * As the deque is unbounded, this method will never throw
 * {@link IllegalStateException} or return {@code false}.
 *
 * @return {@code true} (as specified by {@link Collection#add})
 * @throws NullPointerException if the specified element is null
 */
public boolean add(E e) {
    return offerLast(e);
}

/**
     * Inserts the specified element at the end of this deque.
     * As the deque is unbounded, this method will never return {@code false}.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offerLast(E e) {
        linkLast(e);
        return true;
    }

/**
     * Links e as last element.
     */
    private void linkLast(E e) {
        checkNotNull(e);
        final Node<E> newNode = new Node<E>(e);

        restartFromTail:
        for (;;)
            for (Node<E> t = tail, p = t, q;;) {
                if ((q = p.next) != null &&
                    (q = (p = q).next) != null)
                    // Check for tail updates every other hop.
                    // If p == q, we are sure to follow tail instead.
                    p = (t != (t = tail)) ? t : q;
                else if (p.prev == p) // NEXT_TERMINATOR
                    continue restartFromTail;
                else {
                    // p is last node
                    newNode.lazySetPrev(p); // CAS piggyback
                    if (p.casNext(null, newNode)) {
                        // Successful CAS is the linearization point
                        // for e to become an element of this deque,
                        // and for newNode to become "live".
                        if (p != t) // hop two nodes at a time
                            casTail(t, newNode);  // Failure is OK.
                        return;
                    }
                    // Lost CAS race to another thread; re-read next
                }
            }
    }

/**
     * Throws NullPointerException if argument is null.
     *
     * @param v the element
     */
    private static void checkNotNull(Object v) {
        if (v == null)
            throw new NullPointerException();
    }
```

- LinkedBlockingDeque

```java
/**
 * Inserts the specified element at the end of this deque unless it would
 * violate capacity restrictions.  When using a capacity-restricted deque,
 * it is generally preferable to use method {@link #offer(Object) offer}.
 *
 * <p>This method is equivalent to {@link #addLast}.
 *
 * @throws IllegalStateException if this deque is full
 * @throws NullPointerException if the specified element is null
 */
public boolean add(E e) {
    addLast(e);
    return true;
}

/**
     * @throws IllegalStateException if this deque is full
     * @throws NullPointerException  {@inheritDoc}
     */
    public void addLast(E e) {
        if (!offerLast(e))
            throw new IllegalStateException("Deque full");
    }
    
    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offerLast(E e) {
        if (e == null) throw new NullPointerException();
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return linkLast(node);
        } finally {
            lock.unlock();
        }
    }
```

- LinkedList

```java
/**
 * Appends the specified element to the end of this list.
 *
 * <p>This method is equivalent to {@link #addLast}.
 *
 * @param e element to be appended to this list
 * @return {@code true} (as specified by {@link Collection#add})
 */
public boolean add(E e) {
    linkLast(e);
    return true;
}

/**
     * Links e as last element.
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
    
    
```

- ArrayDeque

```java
/**
 * Inserts the specified element at the end of this deque.
 *
 * <p>This method is equivalent to {@link #addLast}.
 *
 * @param e the element to add
 * @return {@code true} (as specified by {@link Collection#add})
 * @throws NullPointerException if the specified element is null
 */
public boolean add(E e) {
    addLast(e);
    return true;
}

/**
     * Inserts the specified element at the end of this deque.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     * @throws NullPointerException if the specified element is null
     */
    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }
```

