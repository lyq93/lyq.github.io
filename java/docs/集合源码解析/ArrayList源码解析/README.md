## ArrayList源码解析

### ArrayList的扩容机制

#### 核心代码

```java
private static int calculateCapacity(Object[] elementData, int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
}
```

> elementData是存放数组数据的对象
>
> minCapacity是最小扩容量，例如调用add(Object e)方法的时候，最小的扩容量就是elementData.size() + 1
>
> DEFAULT_CAPACITY是默认的扩容大小，值为10

```java
private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}
```

> 该方法就是判断是否需要扩容，如果最小的扩容值大于了数组长度的话，就调用grow的方法进行扩容

```java
/**
 * Increases the capacity to ensure that it can hold at least the
 * number of elements specified by the minimum capacity argument.
 *
 * @param minCapacity the desired minimum capacity
 */
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
  	// 这里进行了位运算，相当于是扩容了原来数组的1.5倍
    int newCapacity = oldCapacity + (oldCapacity >> 1);
  	// 判断新扩容的长度是否满足最小扩容量，如果不满足的话就直接采用最小扩容量
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // 这里就是拷贝数组
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

> 再简单阐述下整个扩容流程：
>
> 1、调用add(Object e)方法的时候，会调用ensureCapacityInternal方法，参数值为数组size + 1
>
> 2、然后会调用calculateCapacity方法，然后返回扩容量跟默认扩容量中的最大值
>
> 3、然后调用ensureExplicitCapacity方法，判断是否需要扩容，判断条件是最小扩容量 - 数组长度，如果大于0就需要扩容
>
> 4、如果需要扩容就调用grow方法，grow的方法首先会去扩容到原数组的1.5倍，然后去判断扩容到1.5倍，然后判断是否大于最小扩容量，如果不大于的话就取最小扩容量作为新数组的长度。当再调用add(Object e)方法的时候，在判断是否需要扩容的地方的时候，就不会进入到grow方法了。
>
> 例如：
>
> List<String> list = new ArrayList<>();
>
> List.add("1");
>
> 扩容量 = list.size() + 1，即等于1
>
> 所以minCapacity 就取自DEFAULT_CAPACITY，即10
>
> 然后判断是否需要扩容的条件minCapacity - elementData.length > 0是满足的，所以进入grow方法
>
> 计算新的容量：int newCapacity = oldCapacity + (oldCapacity >> 1); 为0
>
> newCapacity - minCapacity < 0，所以newCapacity = minCapacity
>
> 所以新数组扩容到10