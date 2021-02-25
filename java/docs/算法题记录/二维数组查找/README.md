## 题目

> 在一个二维数组中，每一行按照从左到右递增的顺序排序，每一列按照从上到下递增的顺序排序。完成一个函数，输入一个这样的二维数组以及一个整数，判断数组中是否存在该整数

## 思路

> 解题的关键的信息在于从左到右的递增排序，从上到下的递增排序。因此，选取二维数组中的某个数字与要查找的数字进行比较
>
> 1、如果选取的数字小于要查找的数字，那么要查找的数字应该在选取的数字的右边或者下边
>
> 2、反之，则要查找的数字应该在选取的数字的左边或者上边
>
> 按照上述的分析，那么要查找的数字有可能出现在两个区域而且区域还存在重叠。如果需要简化解题难度，那么一定跟选取的点有关系。如果选取二维数组的一个角的数字来解题，问题就变简单了。

## 解题

> ```java
> private static boolean find(List<List<Integer>> numbers, int hitNumber) {
>     if (CollectionUtils.isEmpty(numbers)) {
>         return false;
>     }
> 
>     int rows = numbers.get(0).size();
>     int columns = numbers.size();
>     int row = 0;
>     int column = columns - 1;
>     while (rows > 0 && columns > 0) {
>         int selectNumber = numbers.get(column).get(row);
>         if (selectNumber == hitNumber) {
>             return true;
>         } else if (selectNumber > hitNumber){
>             column--;
>         } else {
>             row++;
>         }
>     }
>     return false;
> }
> ```

