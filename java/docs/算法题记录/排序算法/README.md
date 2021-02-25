## 题目

> 各种排序算法

## 思路

> 1、插入排序
>
> 2、冒泡排序
>
> 3、归并排序
>
> 4、快速排序

## 顺序排序实现

> 待补充

## 冒泡排序实现

> 待补充

## 归并排序实现

> 待补充

## 快速排序实现

> ```java
> public static void main(String[] args) throws Exception {
>     List<Integer> params = Arrays.asList(3, 1, 4, 2, 5);
>     quickSort(params, params.size(), 0, params.size() - 1);
>     params.forEach(number -> System.out.println(number));
> }
> 
> private static int partition(List<Integer> numbers, int length, int start, int end) throws Exception {
>     if (CollectionUtils.isEmpty(numbers) || length <= 0 || start < 0 || end >= length) {
>        throw new Exception("invalid param");
>     }
>     int index = (start + end) / 2;
>     swap(numbers, index, end);
> 
>     int small_pos = start - 1; // 小于选中数字的数据的指针
>     // 从第一个数字开始跟选中数字进行比较，如果小于选中数字，
>     // 推动small_pos指针且判断是否存在比选中数字大的数据，如果存在进行一次交换
>     for (index = start; index < end; index++) {
>         if (numbers.get(index) < numbers.get(end)) {
>            ++small_pos;
>            if (index != small_pos) {
>                swap(numbers, small_pos, index);
>            }
>         }
>     }
>     // 把选中数字放到对应的分界点，左边是小于，右边是大于
>     ++small_pos;
>     swap(numbers, small_pos, end);
>     return small_pos;
> }
> 
> private static void quickSort(List<Integer> numbers, int length, int start, int end) throws Exception {
>     if (start == end) {
>        return;
>     }
>     // index是选中数字的下标
>     int index = partition(numbers, length, start, end);
>     if (index > start) {
>        // index - 1排序小于选中数字的数组元素
>        quickSort(numbers, length, start, index - 1);
>     }
>     if (index < end) {
>        // index + 1排序大于选中数字的数组元素
>        quickSort(numbers, length, index + 1, end);
>     }
> }
> 
> private static void swap(List<Integer> numbers, int index, int end) {
>     int endElement = numbers.get(end);
>     numbers.set(end, numbers.get(index));
>     numbers.set(index, endElement);
> }
> ```

