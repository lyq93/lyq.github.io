## 题目

> 给定一个二叉树和一个节点，如何找出中序遍历的下一个节点？树中的节点除了有左右子节点外，还有一个指向父节点的指针

## 思路

> 中序遍历序列的下一个节点的规律为：
>
> 1、如果节点存在右子树，则下一个节点是右子树的最左节点
>
> 2、如果节点不存在右子树，则判断该节点是否是父节点的左节点，如果是，则下一个节点为父节点
>
> 3、如果节点不存在右子树且该节点为父节点的右节点，那么继续往上找到，直到找到节点为父节点的左节点

## 实现

> ```java
> private static BinaryTreeNode getNextNode(BinaryTreeNode node) {
>     if (node == null) {
>        return null;
>     }
>     BinaryTreeNode nextNode = null;
>     if (node.rightTreeNode != null) {
>         BinaryTreeNode right =  node.rightTreeNode;
>         while (right.leftTreeNode != null) {
>             right = right.leftTreeNode;
>         }
>         nextNode = right;
>     } else if(node.parentTreeNode != null) {
>         BinaryTreeNode parentTreeNode = node.parentTreeNode;
>         BinaryTreeNode currentNode = node;
>         while (parentTreeNode != null && currentNode == parentTreeNode.rightTreeNode) {
>             parentTreeNode = parentTreeNode.parentTreeNode;
>             currentNode = parentTreeNode;
>         }
>         nextNode = parentTreeNode;
>     }
>     return nextNode;
> }
> ```



