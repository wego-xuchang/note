### java编程题

#### 1.二维数组

在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。

```
一种是：
把每一行看成有序递增的数组，
利用二分查找，
通过遍历每一行得到答案，
时间复杂度是nlogn
```

```java 

public class Solution {
    public boolean Find(int [][] array,int target) {
         
        for(int i=0;i<array.length;i++){
            int low=0;
            int high=array[i].length-1;
            while(low<=high){
                int mid=(low+high)/2;
                if(target>array[i][mid])
                    low=mid+1;
                else if(target<array[i][mid])
                    high=mid-1;
                else
                    return true;
            }
        }
        return false;
 
    }
}
```



```
另外一种思路是：
利用二维数组由上到下，由左到右递增的规律，
那么选取右上角或者左下角的元素a[row][col]与target进行比较，
当target小于元素a[row][col]时，那么target必定在元素a所在行的左边,
即col--；
当target大于元素a[row][col]时，那么target必定在元素a所在列的下边,
即row++；
```

```java
public class Solution {
    public boolean Find(int [][] array,int target) {
        int row=0;
        int col=array[0].length-1;
        while(row<=array.length-1&&col>=0){
            if(target==array[row][col])
                return true;
            else if(target>array[row][col])
                row++;
            else
                col--;
        }
        return false;
 
    }
}
```



```java

public class Solution {
    public boolean Find(int [][] array,int target) {
        int m = array.length - 1;
        int i = 0;
        while(m >= 0 && i < array[0].length){
            if(array[m][i] > target)
                m--;
            else if(array[m][i] < target)
                i++;
            else
                return true;
        }
         
        return false;
    }
}
```

#### 2.替换空格

请实现一个函数，将一个字符串中的每个空格替换成“%20”。例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Ha

```java

public class Solution {
    public String replaceSpace(StringBuffer str) {
        String sti=str.toString();
        char []strChar=sti.toCharArray();
     StringBuffer out =new StringBuffer();
        
        for(int i=0;i<strChar.length;i++){
            if(strChar[i]==' ')
                out.append("%20");
            else
                out.append(strChar[i]);
        }
        return out.toString();
    }
}
```



#### 3.链表

输入一个链表，按链表值从尾到头的顺序返回一个ArrayList。



```java
import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        ArrayList<Integer> list=new ArrayList<Integer>();
        ListNode pre=null;
        ListNode next=null;
        while(listNode!=null){
            next=listNode.next;
            listNode.next=pre;
            pre=listNode;
            listNode=next;
        }
        while(pre!=null){
            list.add(pre.val);
            pre=pre.next;
        }
        return list;
    }
}
```



```java

方法一：借助堆栈的“后进先出”实现
/**
*    public class ListNode {
*        int val;
*        ListNode next = null;
*
*        ListNode(int val) {
*            this.val = val;
*        }
*    }
*
*/
import java.util.ArrayList;
import java.util.Stack;
public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        Stack<Integer> stack=new Stack<Integer>();
        while(listNode!=null){
            stack.push(listNode.val);
            listNode=listNode.next;     
        }
        
        ArrayList<Integer> list=new ArrayList<Integer>();
        while(!stack.isEmpty()){
            list.add(stack.pop());
        }
        return list;
    }
}


方法二：借助递归实现（递归的本质还是使用了堆栈结构）
import java.util.ArrayList;
import java.util.Stack;
public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        ArrayList<Integer> list=new ArrayList<Integer>();
        
        ListNode pNode=listNode;
        if(pNode!=null){
            if(pNode.next!=null){
                list=printListFromTailToHead(pNode.next);
            }
            list.add(pNode.val);
        }
        
        return list;
    }
}

```



#### 4.重建二叉树

题目描述

输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。

```java

public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        TreeNode root=reConstructBinaryTree(pre,0,pre.length-1,in,0,in.length-1);
        return root;
    }
    //前序遍历{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}
    private TreeNode reConstructBinaryTree(int [] pre,int startPre,int endPre,int [] in,int startIn,int endIn) {
         
        if(startPre>endPre||startIn>endIn)
            return null;
        TreeNode root=new TreeNode(pre[startPre]);
         
        for(int i=startIn;i<=endIn;i++)
            if(in[i]==pre[startPre]){
                root.left=reConstructBinaryTree(pre,startPre+1,startPre+i-startIn,in,startIn,i-1);
                root.right=reConstructBinaryTree(pre,i-startIn+startPre+1,endPre,in,i+1,endIn);
                      break;
            }
                 
        return root;
    }
}
```

#### 5.用两个栈实现队列

题目描述

用两个栈来实现一个队列，完成队列的Push和Pop操作。 队列中的元素为int类型。

```java


import java.util.Stack;
 
public class Solution {
    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();
     
    public void push(int node) {
        stack1.push(node);
    }
     
    public int pop() {
        if(stack1.empty()&&stack2.empty()){
            throw new RuntimeException("Queue is empty!");
        }
        if(stack2.empty()){
            while(!stack1.empty()){
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }
}
```



#### 6.旋转数组的最小数

题目描述

把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。 输入一个非减排序的数组的一个旋转，输出旋转数组的最小元素。 例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1。 NOTE：给出的所有元素都大于0，若数组大小为0，请返回0。



> 也就是寻找分界点，分界点前后都是非递减数组，分界点后面的非递减数组比分界点前面的数组都要小，因此对旋转数组按顺序查找，当出现后一个数比前一个小时，这个数就是最小值，若没有出现后一个数比前一个数小的情况，这说明这个数组所有的数都相等，返回数组第一个数即可。注意考虑数组为空的情况，返回0  

```java

import java.util.ArrayList;
public class Solution {
    public int minNumberInRotateArray(int [] array) {
     if(array == null || array.length==0)
   return 0;
  int low = 0;
  int high = array.length-1;
  int mid = low;
  while(array[low]>=array[high]){
   if(array[low] == array[high]){
    for(int i=low;i<array.length;i++){
     if(array[low]!=array[i]){
      low = i-1;
      break;
     }
    }
    for(int i=high;i>=0;i--){
     if(array[high]!=array[i]){
      high = i+1;
      break;
     }
    }
   }
   if(high-low<=1){
    mid = high;
    break;
   }
   mid = (low+high)/2;
   if(array[mid]>=array[low]){
    low = mid;
   }else if(array[mid]<=array[high]){
    high = mid;
   }
  }
  return array[mid];
    }
}
```



#### 7.前序遍历二叉树

题目描述

从上往下打印出二叉树的每个节点，同层节点从左至右打印。

```java

public class Solution {
    public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<TreeNode> queue = new ArrayList<>();
        if (root == null) {
            return list;
        }
        queue.add(root);
        while (queue.size() != 0) {
            TreeNode temp = queue.remove(0);
            if (temp.left != null){
                queue.add(temp.left);
            }
            if (temp.right != null) {
                queue.add(temp.right);
            }
            list.add(temp.val);
        }
        return list;
    }
}




public class Solution {
    public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if(root==null){
            return list;
        }
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode treeNode = queue.poll();
            if (treeNode.left != null) {
                queue.offer(treeNode.left);
            }
            if (treeNode.right != null) {
                queue.offer(treeNode.right);
            }
            list.add(treeNode.val);
        }
        return list;
    }
}




public class Solution {
    public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
        ArrayList<TreeNode>  listNode=new ArrayList<TreeNode> ();
        ArrayList<Integer>  listVal=new ArrayList<Integer> ();
        if(root==null)
            return listVal;
        listNode.add(root);
        listVal.add(root.val);
        for(int i=0;i<listNode.size();i++){
          TreeNode node=  listNode.get(i);
            if(node.left!=null){
                listNode.add(node.left);
                listVal.add(node.left.val);
            }
            if(node.right!=null){
                listNode.add(node.right);
                  listVal.add(node.right.val);
        }
             
        }
         
        return listVal;
    }
}
```

#### 8.二叉树的后序遍历

```java
/**
 * T: 二叉搜索树的后序遍历序列
 *
 * 题目描述
 * 输入一个整数数组，判断该数组是不是某二叉搜索树的后序遍历的结果。
 * 如果是则输出Yes,否则输出No。假设输入的数组的任意两个数字都互不相同。
 *
 * date: 2015.11.28  10:43
 * @author SSS
 *
 */
public class Solution {
    public boolean VerifySquenceOfBST(int [] sequence) {
        if (sequence == null || sequence.length == 0) {
            return false;
        }
         
        boolean flag = this.isBST(sequence, 0, sequence.length - 1);
         
        return flag;
    }
     
    /**
     * 递归实现检测
     *
     * 对于后序遍历来说，序列数组的最后一个元素一定是根节点,
     * 则根据这个元素，将前面的数组分为左、右两个部分，左侧部分都小，右侧部分都大，
     * 如果右侧部分有比该根节点小的元素，那么就不是后序遍历,如此递归进行
     * @param arr
     * @param start
     * @param end
     * @return
     */
    public boolean isBST(int []arr, int start, int end) {
        if (start >= end) {
            return true;
        }
         
        // 当前数组(从start到end部分)的根节点
        int curElement = arr[end];
        int splitIndex;
        // 找到比curElement大和比curElement小的分界点,分成左侧、右侧两组数据
        for(splitIndex = start; splitIndex < end && arr[splitIndex] < curElement;splitIndex ++);
         
        // 只需要看右侧即可，因为前面的for循环，已经确保左侧部分全部都小于curElement
        for (int i = splitIndex; i < end; i++) {
            if (arr[i] < curElement) {
                return false;
            }
        }
         
        return isBST(arr, start, splitIndex - 1) && isBST(arr, splitIndex, end - 1);
    }
}
```



算法步骤如下： 

  1. 找到根结点； 

2. 遍历序列，找到第一个大于等于根结点的元素i，则i左侧为左子树、i右侧为右子树； 

  3.   我们已经知道i左侧所有元素均小于根结点，那么再依次遍历右侧，看是否所有元素均大于根结点；若出现小于根结点的元素，则直接返回false；若右侧全都大于根结点，则： 

4. 分别递归判断左/右子序列是否为后序序列；

```java

public boolean VerifySquenceOfBST(int [] sequence) {
    if ( sequence==null || sequence.length<=0 ) return false;
    return VerifySquenceOfBST(sequence, 0, sequence.length-1);
}
 
private boolean VerifySquenceOfBST(int [] sequence, int start, int end){
    if ( start>=end )
        return true;
     
    int root = sequence[end];
     
    int i=start;
    while( sequence[i] < root ){
        i++;
    }
     
    int j=i;
    while( j<end ){
        if ( sequence[j]<root ) {
            return false;
        }
        j++;
    }
     
    boolean left = VerifySquenceOfBST(sequence, start, i-1);
    boolean right = VerifySquenceOfBST(sequence, i, end-1);
    return left && right;
}
```

#### 9.字符串的排序

题目描述

输入一个字符串,按字典序打印出该字符串中字符的所有排列。例如输入字符串abc,则打印出由字符a,b,c所能排列出来的所有字符串abc,acb,bac,bca,cab和cba。

输入描述:

```
输入一个字符串,长度不超过9(可能有字符重复),字符只包括大小写字母。
```

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
public class Solution {
    public ArrayList<String> Permutation(String str) {
        List<String> resultList = new ArrayList<>();
        if(str.length() == 0)
            return (ArrayList)resultList;
        //递归的初始值为（str数组，空的list，初始下标0）
        fun(str.toCharArray(),resultList,0);
        Collections.sort(resultList);
        return (ArrayList)resultList;
    }
     
    private void fun(char[] ch,List<String> list,int i){
        //这是递归的终止条件，就是i下标已经移到char数组的末尾的时候，考虑添加这一组字符串进入结果集中
        if(i == ch.length-1){
            //判断一下是否重复
            if(!list.contains(new String(ch))){
                list.add(new String(ch));
                return;
            }
        }else{
            //这一段就是回溯法，这里以"abc"为例
             
            //递归的思想与栈的入栈和出栈是一样的,某一个状态遇到return结束了之后，会回到被调用的地方继续执行
             
            //1.第一次进到这里是ch=['a','b','c'],list=[],i=0，我称为 状态A ，即初始状态
            //那么j=0，swap(ch,0,0)，就是['a','b','c']，进入递归，自己调自己，只是i为1，交换(0,0)位置之后的状态我称为 状态B 
            //i不等于2，来到这里，j=1，执行第一个swap(ch,1,1)，这个状态我称为 状态C1 ,再进入fun函数，此时标记为T1，i为2，那么这时就进入上一个if，将"abc"放进list中
            /////////////-------》此时结果集为["abc"]
             
            //2.执行完list.add之后，遇到return，回退到T1处，接下来执行第二个swap(ch,1,1)，状态C1又恢复为状态B
            //恢复完之后，继续执行for循环，此时j=2,那么swap(ch,1,2),得到"acb"，这个状态我称为C2,然后执行fun，此时标记为T2,发现i+1=2,所以也被添加进结果集，此时return回退到T2处往下执行
            /////////////-------》此时结果集为["abc","acb"]
            //然后执行第二个swap(ch,1,2)，状态C2回归状态B,然后状态B的for循环退出回到状态A
             
            //             a|b|c(状态A)
            //               |
            //               |swap(0,0)
            //               |
            //             a|b|c(状态B)
            //             /  \
            //   swap(1,1)/    \swap(1,2)  (状态C1和状态C2)
            //           /      \
            //         a|b|c   a|c|b
             
            //3.回到状态A之后，继续for循环，j=1,即swap(ch,0,1)，即"bac",这个状态可以再次叫做状态A,下面的步骤同上
            /////////////-------》此时结果集为["abc","acb","bac","bca"]
             
            //             a|b|c(状态A)
            //               |
            //               |swap(0,1)
            //               |
            //             b|a|c(状态B)
            //             /  \
            //   swap(1,1)/    \swap(1,2)  (状态C1和状态C2)
            //           /      \
            //         b|a|c   b|c|a
             
            //4.再继续for循环，j=2,即swap(ch,0,2)，即"cab",这个状态可以再次叫做状态A，下面的步骤同上
            /////////////-------》此时结果集为["abc","acb","bac","bca","cab","cba"]
             
            //             a|b|c(状态A)
            //               |
            //               |swap(0,2)
            //               |
            //             c|b|a(状态B)
            //             /  \
            //   swap(1,1)/    \swap(1,2)  (状态C1和状态C2)
            //           /      \
            //         c|b|a   c|a|b
             
            //5.最后退出for循环，结束。
             
            for(int j=i;j<ch.length;j++){
                swap(ch,i,j);
                fun(ch,list,i+1);
                swap(ch,i,j);
            }
        }
    }
     
    //交换数组的两个下标的元素
    private void swap(char[] str, int i, int j) {
            if (i != j) {
                char t = str[i];
                str[i] = str[j];
                str[j] = t;
            }
        }
    }
```





