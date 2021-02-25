## 题目

> ```
> 长度为n的数组，所有数字都在0 - n-1范围内。数组存在重复的数字，输出任意一个重复的数字
> ```

> ```java
> private static int duplicate(List<Integer> numbers, int length) {
>         if (CollectionUtils.isEmpty(numbers)) {
>             return -1;
>         }
> 
>         boolean checkResult = numbers.stream().anyMatch(number -> number > length - 1);
>         if (checkResult) {
>            return -1;
>         }
>         // 针对数字在范围0 - n-1之间采取的特殊处理，把数字与下标对应
>         int resultNumber = -1;
>         for (int i = 0; i < length; i++) {
>             while (numbers.get(i) != i && resultNumber == -1) {
>                 if (numbers.get(i) == numbers.get(numbers.get(i))) {
>                     resultNumber = numbers.get(i);
>                     break;
>                 }
> 
>                 int tmp = numbers.get(i);
>                 numbers.set(i, numbers.get(tmp));
>                 numbers.set(tmp, tmp);
>             }
>         }
>         return resultNumber;
>     }
> ```
>
> 由于数组里面的元素的大小范围是在0 - n-1之间，刚好与数组的下标符合。因为，解题的思路是，把数组中每个元素与下标对应，当发现存在相同的多个相同下标的元素的情况即是重复元素

## 题目

> 数组长度为n+1，数组中的元素在1 - n的范围，不能修改数组，找出重复的数字

> ```java
> private static int duplicate2(List<Integer> numbers, int length) {
>     if (CollectionUtils.isEmpty(numbers)) {
>         return -1;
>     }
>     int start = 1;
>     int end = length;
>     while (end >= start) {
>         int middle = (start + end) / 2;
>         int count = getCount(numbers, start, middle);
>         if (end == start) {
>            if(count > 1) {
>                return start;
>            } else {
>                break;
>            }
>         }
>         if (count > middle - start + 1) {
>             end = middle;
>         } else {
>             start = middle + 1;
>         }
>     }
>     return -1;
> }
> 
> private static int getCount(List<Integer> numbers, int start, int end) {
>         List<Integer> counts = numbers.stream()
>                 .filter(number -> number >= start && number <= end)
>                 .collect(Collectors.toList());
>         return counts.size();
>     }
> ```
>
> 思路：
>
> 1、可以创建一个n+1长度的辅助数组，逐一把原数组的数字放到对应的下标中，这样就能发现重复元素，但是这种方式利用了额外的内存空间
>
> 2、为了不额外花费内存，另一种方式是直接使用二分查找，把1-n的数组从中间分开，然后判断这些数字在整个数组中出现的次数，如果大于元素个数次，意味着重复的数字就存在于该部分数字中，然后继续折半，直至找到对应的重复数字