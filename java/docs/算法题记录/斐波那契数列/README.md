## 题目

> 写一个函数，输入n，求斐波那契数列的第n项。

## 思路

> 斐波那契的定义如下：
>
> f(0) = 0, f(1) = 1,  n >1, f(n) = f(n-1) + f(n - 2) 
>
> 1、递归
>
> 根据斐波那契的定义，最直观的想法就是通过递归实现。但是递归存在许多重复的计算。例如f(10) = f(9) + f(8), f(9) = f(8) + f(7), f(8) = f(7) + f(6)，时间复杂度是随着n以指数的方式递增的。
>
> 2、循环
>
> 自下而上求解。要求f(10)先求出f(9)的解

## 实现

> ```java
> /**
>  * 斐波那契数列递归解法
>  * 时间复杂度以指数级递增
>  * @param n
>  * @return
>  */
> private static int solution_1(int n) {
>     List<Integer> results = Arrays.asList(0, 1);
>     if (n < 2) {
>        return results.get(n);
>     }
>     return solution_1(n - 1) + solution_1(n - 2);
> }
> 
> /**
>  * 斐波那契数列循环解法
>  * 自下而上求解，保存中间结果避免重复计算
>  * @param n
>  * @return
>  */
> public static long solution_2(int n) {
>     List<Integer> results = Arrays.asList(0, 1);
>     if (n < 2) {
>         return results.get(n);
>     }
>     long num_1_pos = 1;
>     long num_2_pos = 0;
>     long sum_pos = 0;
> 
>     for(int i = 2; i <= n; i++) {
>         sum_pos = num_1_pos + num_2_pos;
>         num_2_pos = num_1_pos;
>         num_1_pos = sum_pos;
>     }
>     return sum_pos;
> }
> ```

## 扩充

> ```
> 青蛙台阶问题，一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个n级台阶总共有多少种跳法
> ```

## 思路

> 设n级台阶的跳法为函数f(n)
>
> 1、n = 0， f(n) = 0；
>
> 2、n = 1， f(n) = 1；
>
> 3、n = 2， f(n) = 2；
>
> 4、n > 2，f(n) = f(n - 1) + f(n - 2)；
>
> 青蛙第一次跳台阶的时候，如果选择跳1级台阶，那么剩下的跳法数就是剩余台阶的跳法数为f(n-1)，如果选择跳2级台阶，那么剩余台阶的跳法数为f(n - 2)，符合斐波那契数列

## 实现

> ```java
> List<Integer> results = Arrays.asList(0, 1, 2);
> if (n <= 2) {
>     return results.get(n);
> }
> long num_1_pos = 2;
> long num_2_pos = 1;
> long sum_pos = 0;
> 
> for(int i = 3; i <= n; i++) {
>     sum_pos = num_1_pos + num_2_pos;
>     num_2_pos = num_1_pos;
>     num_1_pos = sum_pos;
> }
> return sum_pos;
> ```

