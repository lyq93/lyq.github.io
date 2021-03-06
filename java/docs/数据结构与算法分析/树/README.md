## 树

### 树的一些基本概念

- 定义

  > 树是一些节点的集合。集合可以是空集，这种情况树称为空树。集合不为空的情况下，树由称为根的节点r以及0个或者多个非空的子树T1,T2,T3....Tn组成。一棵树是由n个节点和n-1条边组成。每个节点都可以有任意多个儿子或者0个儿子。
  >
  > 树叶：没有儿子的节点
  >
  > 兄弟：同一父亲节点的节点
  >
  > 节点的深度：对于任意节点n，n节点的深度就是从根到该节点的唯一路径的长。
  >
  > 节点的高：对于任意节点n，n节点的高就是从该节点出发，到一片树叶的最长的路径的长。
  >
  > 树高：树的高度就是根节点的高度，所以等于根高。

- 先序遍历

  > 在先序遍历中，对节点的处理是在诸儿子节点被处理之前。也就是说，处理顺序是节点>左儿子节点>又儿子节点

  - 示例代码

    ```java
    private void listAll(int depth) {
        printName(depth);
        if(isDirectory()){
            for(File c : directory) {
                c.listAll(depth + 1);
            }
        }
    }
    ```

- 后序遍历

  > 在后序遍历中，一个节点处的工作是在它的诸儿子节点被计算之后进行的。也就是说，处理顺序是左儿子节点>右儿子节点>节点

  - 示例代码

    ```java
    private int size() {
        int totalSize = sizeOfThisFile();
        if(isDirectory()) {
            for(File c : directory) {
                totalSize += c.size();
            }
        }
        return totalSize;
    }
    ```

- 中序遍历

  > 待补充

  - 示例代码

    待补充

### 二叉树

#### 概念

> 二叉树是一棵树，其中每个节点都不能有多于两个的儿子。二叉树一个性质是一颗平均的二叉树的深度要比节点个数N小得多。对于对于特殊类型的二叉树，如二叉查找树（后面会讲到）其深度为Ologn，而对于最坏的情况的线性二叉树，其深度可以是N-1

#### 实现

> 因为二叉树节点最多有两个子节点，所以可以保存直接链接到它们的链。这样方式类似于双链表的结构，在声明中，element就是节点元素，然后再加上两个到其他节点的引用。
>
> ```java
> class BinaryNode {
>     // 节点元素
>     Object element;
>     // 左儿子节点
>     BinaryNode left;
>     // 右儿子节点
>     BinaryNode right;
> }
> ```

#### 使用场景

> 二叉树的主要用处之一是用在编译器的设计领域，例如表达式树。表达式树的树叶是操作数，其他节点是操作符，如下二叉树图。
>
> 待补充
>
> 从该表达式图中，我们计算出左子树和右子树的值，然后应用根处的运算符可以算出整个表达式树的值。左子树是a+(b*c)，右子树是((d * e) + f) * g，因此整个表达式就是a + (b * c) + ((d * e) + f) * g
>
> 从实现层面上来说
>
> 1、我们可以通过递归产生一个带括号的左表达式，然后打印根的运算发，再产生一个带右括号的表达式而得到一个中缀表达式。这就是一种中序的遍历。
>
> 2、如果使用后序遍历的话，先打印左子树，再打印右子树，最后打印运算符，这种方式下，将产生后缀表达式。这是一种后序遍历。
>
> 3、使用先序遍历也是可以产生一个表达式，产生的表达式是前缀记法。

### 二叉查找树ADT

#### 概念

> 使二叉树称为二叉查找树的性质是，对于树的任意一个节点x，它的左子树中的所有项都小于该节点，它的右子树中的所有项都大于该节点。所以，二叉查找树是以一种有序的方式排序元素的。
>
> 二叉查找树的一个重要应用就是查找。因为二叉查找树的平均深度是Ologn

#### 操作

> 代码实现二叉查找树的一个一般类，包含树的各种操作

- 数据域

```java
private static class BinaryNode<AnyType> {

    BinaryNode(AnyType element) {
        this(element, null, null);
    }

    BinaryNode(AnyType element, BinaryNode<AnyType> left, BinaryNode<AnyType> right) {
        this.element = element;
        this.left = left;
        this.right = right;
    }

    // 节点元素
    Object element;
    // 左儿子节点
    BinaryNode left;
    // 右儿子节点
    BinaryNode right;
}
```

> 这个类作为树的内部静态类，作为树的一个节点表示，包含元素，左儿子节点，右儿子节点

- contains方法

> 如果在树T中存在含有X项的节点，则该操作会返回true，如果这样的节点不存在，则返回false
>
> ```java
> private boolean contains(AnyType x, BinaryNode<AnyType> t) {
>     if (t == null) {
>         return false;
>     }
>     int compareResult = x.compareTo(t.element);
>     if (compareResult < 0) {
>         return contains(x, t.left);
>     } else if (compareResult > 0) {
>         return contains(x, t.right);
>     } else {
>         return true;
>     }
> }
> ```

- findMin和findMax方法

> 返回树中最小/最大的元素的节点的引用。从根节点开始，如果存在左节点就一直递归向左进行，终止的时候就是最小的节点。同理，最大节点的实现也是如此
>
> ```java
> private BinaryNode<AnyType> findMin(BinaryNode<AnyType> t) {
>     if(t == null) {
>         return null;
>     }
>     if(t.left == null) {
>         return t;
>     }
>     return findMin(t.left);
> }
> ```

- insert方法

> 为了将X插入到树中，可以像contains方法一样沿着树进行查找。如果存在X就什么都不做，否则就将X插入到路径的最后的一个点上。
>
> ```java
> private BinaryNode<AnyType> insert(AnyType x, BinaryNode<AnyType> t) {
>     if(t == null) {
>         return new BinaryNode<>(x,null,null);
>     }
>     int compareResult = x.compareTo(t.element);
>     if(compareResult < 0) {
>         t.left = insert(x, t.left);
>     }
>     if(compareResult > 0) {
>         t.right = insert(x, t.right);
>     }
>     return t;
> }
> ```
>
> 这里之所以要用t去接收树以及返回t，是因为t是引用该树的根，而根在第一次插入的时候发生了变化，所以insert需要返回一个对新树根的引用

- remove方法

> 删除节点操作有几种情况需要考虑
>
> 1、删除叶子节点，直接删除即可
>
> 2、删除有单个儿子节点的节点，调整父节点链
>
> 3、删除双儿子节点的节点，要用右子树中的最小值代替该节点，然后递归删除
>
> 这里只演示最后一种情况
>
> 图示一说明：
>
> <img src="https://raw.githubusercontent.com/lyq93/lyq.github.io/master/image-20201123235110304.png" style="zoom:50%;" />
>
> > 需要删除节点2，节点2有两个子节点
>
> 图示二说明：
>
> <img src="https://raw.githubusercontent.com/lyq93/lyq.github.io/master/image-20201123234739818-20201123235241022.png" style="zoom: 50%;" />
>
> > 把节点2的右子树中最小的3替换节点2的值，然后修改节点3的父节点的链，如图示的虚线部分
>
> 代码实现：
>
> ```java
> /**
>  * 删除操作有3种情况：
>  * 1、删除的节点是树叶，那么直接删除即可
>  * 2、删除的节点有一个子节点，那么用子节点替换该节点即可
>  * 3、删除的节点有两个子节点，那么找到该节点的右子树中的最小值，替换该
>  * 节点，然后再到右子树中递归去删除该节点（因为右子树的最小节点一定是树叶，
>  * 所以这种操作很容易）
>  * @param x
>  * @param t
>  * @return
>  */
> private BinaryNode<AnyType> remove(AnyType x, BinaryNode<AnyType> t) {
>     if (t == null) {
>         return null;
>     }
>     int compareResult = x.compareTo(t.element);
>     if(compareResult < 0) {
>         t.left = remove(x, t.left);
>     }else if(compareResult > 0) {
>         t.right = remove(x, t.right);
>     } else {
>         if(t.right != null && t.left != null) {
>             t.element = (AnyType) findMin(t.right).element;
>             t.right = remove(t.element, t.right);
>         } else {
>             t = t.left == null ? t.right : t.left;
>         }
>     }
>     return t;
> }
> ```

#### 存在的问题

> 二叉查找树的删除操作总是把右子树中的最小节点替换被删除的节点。这种方式的处理有助于使左子树比右子树深。因为删除操作永远不会降低左子树高度，如果执行多次插入/删除操作之后，左子树就是明显比右子树深。这种不平衡的树就会导致查找所要消耗的时间上升。因此，需要有一个平衡的条件使得树不会发生类似的情况。
>
> 思路：
>
> 1、任何节点的深度都不能过深
>
> 2、左子树与右子树的深度差不能超过一定的深度

### AVL树(平衡查找树)

#### 概念

> AVL树是带有平衡条件的二叉查找树。最理想的情况是左子树高度和右子树高度相等，这种称为理想平衡树。但由于这种条件太严格难以使用，一般我们所说的AVL树是指，左子树的高与右子树的高不得相差超过1

#### 图示

> 待补充

#### 存在的问题

> 平衡二叉查找树需要保证左右树高差是小于等于1的，那么在插入操作的时候，就有可能打破这种平衡。那么就需要在插入操作完成前对树进行调整使其回到平衡二叉查找树的状态

##### 插入操作导致平衡结构破坏图示

> 待补充
>
> 
>
> 问题描述：
>
> 执行了破坏平衡条件的插入操作后，那么从插入点到根节点的路径上一定存在一个节点它的新平衡破坏了AVL条件。假设，另这个节点为x，那么插入操作就可能存在以下几种情况：
>
> 1、对x的左儿子的左子树进行一次插入操作
>
> 2、对x的左儿子的右子树进行一次插入操作
>
> 3、对x的右儿子的左子树进行一次插入操作
>
> 4、对x的右儿子的右子树进行一次插入操作
>
> 第1、4属于镜像插入，属于同一种情况；
>
> 图示：
>
> 待补充
>
> k2就是破坏了AVL条件的节点，插入的节点是b，导致了k2的左子树比右子树的高度差大于了1。这种情况是第一种情况，处理这种插入发生在“外边”的情况，可以使用单旋转解决，后面会提到。
>
> 第2、3属于镜像插入，属于同一种情况；
>
> 图示：
>
> 待补充
>
> k2就是破坏了AVL条件的节点，插入的节点是x1，导致了k2的左子树比右子树的高度差大于了1。这种情况是第二种情况，处理这种插入发生在“内边”的情况，可以使用双旋转解决，后面会提到。

##### 处理方式

- 单旋转

> 抽象的形容就是，把树看作是灵活的，或者看成是一个树形的挂坠？抓住k1的点，这时候会发生什么变化呢？k1会作为整个树的根，而k2会作为k1的右儿子节点，因为y是原k1的右儿子节点，因为y比k2小，所以这个时候，y需要挂载到k2的左儿子节点上。整个树进行旋转之后，就成为如下的样子：
>
> 待补充
>
> 这个时候，树重新达到了AVL条件。同样的，第四种情况也是一个单旋转操作可以处理，这里就不再举例。

- 双旋转

> 同样的如果插入发生在“内边”，使用单旋转并没有解决问题。这个时候因为插入发生在y，那么可以把y看作是一个子树，y作为树的一个根节点。从图可以知道，k2不能作为根，k1单旋转也解决不了问题。那么只有y可以作为根了。这使得k1作为y的左儿子节点，k2作为y的右儿子节点，从而确定了整个树的结构。
>
> 值得一提的是，这个操作的名字叫做双旋转，也就是说两次单旋转操作。首先在k1与y之间发生一次单旋转，使得k1作为y的左儿子节点。再在k2与y之间发生一次单旋转，使得k2作为y的右儿子节点。这样子y就上升为整个树的根
>
> 图示说明：
>
> 待补充

#### 算法转化为编程实现

> 插入操作在之前的示例中已经展示过了，现在对插入方法进行优化，在插入操作完成前进行一个balance方法的调用，对树的结构进行调整。
>
> ```java
> private AvlNode<AnyType> balance(AvlNode<AnyType> t) {
>     if(t == null) {
>         return null;
>     }
>     // 节点左树深度大于右树
>     if(height(t.left) - height(t.right) > ALLOWED_VALUE) {
>         // 左子树的左节点深度大于左子树的右节点深度，外边插入，单旋转
>         if(height(t.left.left) >= height(t.left.right)) {
>             t = singleLeftTranslate(t);
>         } else {
>             // 内边插入，双旋转
>             t = doubleLeftTranslate(t);
>         }
>     }
>     // 右子树深度大于左子树深度
>     if(height(t.right) - height(t.left) > ALLOWED_VALUE) {
>         // 右子树的右节点大于右子树的左节点，外边插入，单旋转
>         if(height(t.right.right) >= height(t.right.left)) {
>             t = singleRightTranslate(t);
>         } else {
>             // 内边插入，双旋转
>             t = doubleRightTranslate(t);
>         }
>     }
>     // 插入完成后，重新计算节点高度
>     t.height = Math.max(height(t.left), height(t.right)) + 1;
> 
>     return t;
> }
> ```
>
> ```java
> private AvlNode<AnyType> singleLeftTranslate(AvlNode<AnyType> k2) {
> 
>     AvlNode<AnyType> k1 = k2.left;
>     k2.left = k1.right;
>     k1.right = k2;
>     k2.height = Math.max(height(k2.left),height(k2.right)) + 1;
>     k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
> 
>     return k1;
> }
> ```
>
> 总结：
>
> 对于AVL树的相关算法的实现或者相关图形的转换，脑子里需要有关于平衡AVL树的四种情况的典型树图，这样子对于单旋转或者双旋转的处理都能得心应手

### 伸展树

#### 概念

> 当树的一个节点被访问，那么这个节点后续被访问的可能性也会变大。这种情况下，当节点被访问后需要把这个节点通过AVL树的旋转降低该节点的深度，从而提高访问效率

#### 实现

> 通过对一个节点和它的父节点不断的进行单旋转，使这个节点最后变为根节点。但存在的缺陷是会把其他节点推到远处。

### 展开

> 针对伸展树存在的缺陷，在对旋转如何实施的选择上，可以做些处理。
>
> 解决方案：
>
> 1、令x是访问路径的节点（非根）
>
> 2、如果x的父节点是树根，只要旋转x和树根
>
> 3、如果x有父节点和祖父节点，如果是“之”字型，进行双旋转；如果是一字型把左树换成右树
>
> 所谓“之”字型，就是假设x是父节点的右节点，父节点是祖父节点的左节点，这种称为“之”字型
>
> 所谓“一”字型，就是假设x是父节点的左节点，父节点是祖父节点的左节点，这种称为“一”字型

### B+树

### 出现的原因

> 到目前为止，我们始终在假设整个数据结构是存储在计算机内存中的。但是如果数据过大，主存装不下。那么就意味着必须把数据结构放到磁盘上。此时，上述的计算时间复杂度的模型就不适用了。因为这种模型是建立在所有操作的耗时是相等的情况下。然而现在涉及到磁盘I/O
>
> 磁盘的访问代价太高，所以我们更愿意进行大量的计算
>
> 对于一颗不平衡的二叉查找树来说，最坏的情况可能是线性的深度。那么假设有1000w项，那么就需要1000w次磁盘访问。

#### 概念

> 有更多分支就有更少的高度，一个31节点的理想二叉树是5层，一个5叉树则只有3层。一颗M叉查找树可以有M路分支，随着分支的增加，树的深度在减少。一颗完成二叉树的高度大约是log2N，而一颗完全M叉树的高度大约是logMN

#### B+树特性

- 数据项存储在树叶上
- 非叶节点存储直到M-1个关键字以指示搜索方向，关键字i代表子树i+1中的最小关键字
- 树的根或者是一片树叶，或者其儿子数在2和M之间
- 除根外，所有的非树叶节点的儿子数在M/2和M之间
- 所有的树叶都在相同的深度上并有L/2和L之间个数据项

> 待补充一个B+树示例进行特性说明。
>
> 1、M是叶子节点的分支数
>
> 2、L是每个叶子节点的存储的数据项大小
>
> 3、对于非叶子节点来说，只存储关键字来指示搜索的方向，关键字的数量是M-1，所以取决于当前这个非叶子节点下M的大小

#### B+树高度变化的原理

- B+树变高

> 1、对于B+树来说，插入一个数据项，有可能存在该树叶已经装满了数据项。这种情况下，需要对该树叶进行拆分为两个树叶，并往非叶父节点生成一个关键字
>
> 2、在生成树叶节点的场景下，上述行得通是因为非叶父节点的儿子数未满，若分裂树叶的时候，非叶父节点的儿子数已经满了，这个时候就需要分裂非叶父节点
>
> 3、如果分裂非叶父节点的场景下，该深度的节点已经达到最大限度，那么就继续往上分裂。至此，最终会存在分裂根节点的情况，这个时候就会产生2个根节点。因此，就需要生成一个新根，节点为分裂出来的节点。
>
> 这就是B+树增加高度的唯一方式

- B+树变矮

> 1、对于B+树来说，删除一个数据项有可能存在删除的数据项是树叶节点存储的数据项数量的最小值。那么删除之后，它的数据项就小于最小值了。这时候可以通过用邻近节点的数据项来矫正。若临近树叶的数据项也是最小值，那么就需要合并这两个树叶
>
> 2、由于合并树叶的关系，如果父节点的树叶数已经是最小值，那么合并之后，树叶数就小于最小值，那么就需要往上进行合并。那么最终就有可能存在这样一种情况，根本来有两个节点，这两个节点被合并了，导致根只有一个节点，这个时候的处理就需要删除根，让合并后的新节点作为根。
>
> 这就是B+树降低高度的唯一方式

### 后续树模型待补充







