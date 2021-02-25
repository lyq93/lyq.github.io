## 题目

> 把一个数组最开始的若干个元素搬到数组的末尾。我们称之为数组的旋转。输入一个递增排序的数组的一个旋转，输出旋转数组的最小元素。例如，数组34512，为12345的一个旋转，该数组的最小值为1

## 思路

> 首先最直观的做法显然是直接循环数组找出最小值。但这种解题方法并不契合题目，没有使用到数组的旋转的特点且时间复杂度为O(n)。
>
> 分析：
>
> 1、旋转后的数组可以拆分为2个已排序的数组
>
> 2、前面数组的元素一定是大于等于后面数组的元素
>
> 3、最小的元素刚好是两个数组的分界点
>
> 实现：
>
> 1、维护两个指针，第一个指针指向第一个元素，第二个指针指向数组最后一个元素
>
> 2、判断数组中间位置的元素是否大于第一个指针指向的元素，如果大于，说明中间位置的元素落在第一个数组里，然后把中间位置的下标赋值给第一个指针，继续查找
>
> 3、最终，第一个指针一定是指向第一个数组的最后一个元素，第二个指针指向第二个元素的第一个元素，这就是退出循环的条件，最小元素就是在第二个指针指向的位置。

## 实现

> ```java
> public static void main(String[] args) {
>     List<Integer> numbers = Arrays.asList(3, 4, 5, 1, 2);
>     System.out.println(minNumber(numbers));
> }
> 
> private static int minNumber(List<Integer> numbers) {
>     if (CollectionUtils.isEmpty(numbers)) {
>         return -1;
>     }
>     int index_1 = 0;
>     int index_2 = numbers.size() - 1;
>     // 初始化为index_1的原因是旋转数组的一个特殊情况是把0个元素搬到数组后面，这将导致旋转后的数组依旧是原数组本身。
>     // 最小的元素依然是第一个位置
>     int mid_index = index_1;
>     while (numbers.get(index_1) >= numbers.get(index_2)) {
>         if (index_2 - index_1 == 1) {
>             mid_index = index_2;
>             break;
>         }
>         mid_index = (index_1 + index_2) / 2;
>         // 如果存在指针一、指针二、中间数字三个数相等的情况，没办法判断中间数字是落在第一个数组还是第二个数组
>         // 这种情况下，直接通过O(n)查找最小元素
>         if(numbers.get(index_1) == numbers.get(index_2)
>                 && numbers.get(index_1) == numbers.get(mid_index)) {
>             return findMinInArray(numbers, index_1, index_2);
>         }
>         if (numbers.get(mid_index) >= numbers.get(index_1)) {
>             index_1 = mid_index;
>         }
>         if(numbers.get(mid_index) <= numbers.get(index_2)) {
>             index_2 = mid_index;
>         }
>     }
>     return numbers.get(mid_index);
> }
> 
> private static int findMinInArray(List<Integer> numbers, int index_1, int index_2) {
>     int result = numbers.get(index_1);
>     for(int i = index_1 + 1; i < index_2; i++) {
>         if (result > numbers.get(i)) {
>             result = numbers.get(i);
>         }
>     }
>     return result;
> }
> ```
>
> 注意：
>
> 这里的实现有2个特殊情况
>
> 1、可能存在搬0个元素到数组末尾的情况，这种情况下，旋转数组依然是原数组，最小值依然是第一个元素。因此，在初始化中间位置的时候，是直接初始化为数组的第一个元素。
>
> 2、可能存在指针一、指针二、中间位置这3个下标对应的元素完全相等的情况，这种情况下是没办法判断中间位置是落在哪个数组上的。这种情形下直接使用顺序查找的方式。