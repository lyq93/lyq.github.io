## 题目

> 合并两个已排序的数组，数组A有刚好存放数组B元素的额外内存，合并后的数组保持排序。

## 思路

> 解法类似于归并排序，使用三个指针，指针A指向数组A最末尾元素的下标，指针B指向数组B最末尾元素的下标，指针C指向数组A最末尾下标。
>
> 指针A和指针B所指向的元素进行比较，大的元素放入指针C指向的内存。与此同时，推动相应的指针行进。直到其中一个数组对应的指针已经没有元素，然后把另外一个数组的元素全部置入指针C指向的内存。

## 解题

> ```java
> private static void merge(List<Integer> a, List<Integer> b) {
>     int aPos = -1;
>     for(int i = 0; i < a.size(); i++) {
>         if (a.get(i) > 0) {
>            aPos++;
>         }
>     }
>     int bPos = b.size() - 1;
>     int new_aPos = a.size() - 1;
>     while (aPos >= 0 && bPos >= 0) {
>         if (a.get(aPos) >= b.get(bPos)) {
>             a.set(new_aPos, a.get(aPos));
>             aPos--;
>         } else {
>             a.set(new_aPos, b.get(bPos));
>             bPos--;
>         }
>         new_aPos--;
>     }
>     while (aPos >= 0) {
>         a.set(new_aPos, a.get(aPos));
>         aPos--;
>         new_aPos--;
>     }
>     while (bPos >= 0) {
>         a.set(new_aPos, b.get(bPos));
>         bPos--;
>         new_aPos--;
>     }
> }
> ```

