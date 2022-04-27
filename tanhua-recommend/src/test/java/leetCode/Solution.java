package leetCode;

import java.util.*;

/**
 * @Author: leah_ana
 * @Date: 2022/4/25 10:42
 */

public class Solution {
    Map<Node, Node> cachedNode = new HashMap<Node, Node>();

    // 复杂链表的复制
    public Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }
        if (!cachedNode.containsKey(head)) {
            Node newNode = new Node(head.val);
            cachedNode.put(head, newNode);
            newNode.next =copyRandomList(head.next);
            newNode.random=copyRandomList(head.random);
        }
        return cachedNode.get(head);
    }


    // 反转链表
    public ListNode reverseList(ListNode head) {
        ListNode cur = head;
        ListNode pre = null;
        while (cur != null) {
            ListNode temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        return pre;
    }


    public ListNode reverseList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode node = reverseList2(head.next);
        head.next.next = head;
        head.next = null;
        return node;
    }

    // 剑指 Offer 06. 从尾到头打印链表
    public int[] reversePrint(ListNode head) {
        Stack<ListNode> stack = new Stack<>();
        ListNode temp = head;
        while (temp != null) {
            stack.push(temp);
            temp = temp.next;
        }

        int[] res = new int[stack.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = stack.pop().val;
        }
        return res;
    }

    // 最长回文子串
    public String longestPalindrome(String s) {
        char[] chars = s.toCharArray();
        int len = chars.length;
        int star = 0, end = 0;
        int length = 0;
        length = getLength(chars, star, length);
        return s.substring(star, star + length);
    }


    private int getLength(char[] chars, int star, int length) {
        int end;
        for (int i = 0; i < chars.length; i++) {
            char temp = chars[star];
            if (chars[i] == temp) {
                end = i;
                length = Math.max((end - star), length);
            }
        }
        return length;
    }

    // 最长公共子字符串因子
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        //设置一个最长公共子字符串
        String prefix = strs[0];

        //遍历字符串数组
        for (String str : strs) {
            //方法用于检测字符串是否以指定的前缀开始
            if (prefix.startsWith(str)) {
                prefix = str;
                continue;
            }
            //拆分字符串为字符数组
            for (int i = 0; i < prefix.length(); i++) {
                //从0开始比较字符串的第i个字符是否相等
                if (str.charAt(i) != prefix.charAt(i)) {
                    //设定最长公因子字符串为到0-i个字符
                    prefix = new String(Arrays.copyOf(prefix.toCharArray(), i));
                    break;
                }
            }
        }
        return prefix;
    }

    // 对角线遍历
    public int[] findDiagonalOrder(int[][] mat) {
        if (mat.length == 0) {
            return new int[0];
        }
        // 00 01 02 03 04
        // 10 11 12 13 14
        // 20 21 22 23 24
        // 30 31 32 33 34
        // 40 41 42 43 44
        int x = mat.length;//行
        int y = mat[0].length; //列
        int[] arr = new int[x * y];//结果存放数组
        int count = x + y - 1;// 对角线数量
        int row = 0, col = 0, index = 0;//行row 列col 数组arr的索引为index
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0) {
                // 偶数对角线 往右上遍历
                while (row >= 0 && col < y) {
                    arr[index] = mat[row][col];
                    index++;
                    row--;
                    col++;
                }
                //判断是否为越界情况：不越界正常行加一，越界行加二，列减一；
                //（此处不理解的拿张草稿纸将循环中row和col的值遍历写一下对照矩阵图就明白了）
                if (col < y) {
                    row++;
                } else {
                    row += 2;
                    col--;
                }

            } else {
                // 奇数对角线 往左下遍历
                while (row < x && col >= 0) {
                    arr[index] = mat[row][col];
                    index++;
                    row++;
                    col--;
                }
                if (row < x) {
                    col++;

                } else {
                    row--;
                    col += 2;
                }

            }

        }
        return arr;
    }

    public int[] findDiagonalOrder2(int[][] mat) {
        // 00 01 02 03 04
        // 10 11 12 13 14
        // 20 21 22 23 24
        // 30 31 32 33 34
        // 40 41 42 43 44
        if (mat.length == 0) {
            return new int[0];
        }
        int m = mat.length;
        int n = mat[0].length;
        //存放数组
        int[] ans = new int[m * n];
        //对角线方向次数
        int count = n + m - 1;

        //定义初始化 行标记，列标记，存放数组索引
        int row = 0, col = 0, Index = 0;
        //开始对角线循环
        for (int i = 0; i < count; i++) {
            //判断对角线方向（因题目初始从右上（即i=0）开始）：偶数右上，奇数左下
            if (i % 2 == 0) {
                //右上操作
                while (row >= 0 && col < n) {
                    //将矩阵数存入存放数组
                    ans[Index] = mat[row][col];
                    //索引后移
                    Index++;
                    //右上规律：行减一，列加一
                    row--;
                    col++;
                }
                //判断是否为越界情况：不越界正常行加一，越界行加二，列减一；
                //（此处不理解的拿张草稿纸将循环中row和col的值遍历写一下对照矩阵图就明白了）
                if (col < n) {
                    row++;
                } else {
                    row += 2;
                    col--;
                }
            }
            //左下操作：按规律与右上相反即可
            else {
                while (row < m && col >= 0) {
                    ans[Index] = mat[row][col];
                    Index++;
                    row++;
                    col--;
                }
                if (row < m) {
                    col++;

                } else {
                    row--;
                    col += 2;
                }
            }
        }
        // 返回存放数组
        return ans;

    }

    public int[] findDiagonalOrder1(int[][] mat) {
        if (mat.length == 0) {
            return new int[]{};
        }
        // x<mat.length && y<mat[0].length 行列数限制
        int row = mat.length - 1;//行边界
        int col = mat[0].length - 1;//列边界

        int[] res = new int[row * col];
        List<List<Integer>> list = new ArrayList<>();

        int maxLength = row + col;//对角线数量

        //初始化对角线集合
        for (int i = 0; i <= maxLength; i++) {
            list.add(new ArrayList<>());
        }
        //遍历对角线
        for (int x = 0; x <= row; x++) {
            for (int y = 0; y <= col; y++) {
                //获取x+y处索引的值
                // x+y 相等 说明在同一对角线上
                int index = x + y;
                int value = mat[x][y];
                // 给这个对角线集合添加值
                list.get(index).add(value);
            }
        }
        List<Integer> resList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 0) {
                // 偶数按照插入顺序倒叙排列
                Collections.reverse(list.get(i));
            }
            resList.addAll(list.get(i));
        }
        int[] arr = new int[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            arr[i] = resList.get(i);
        }


        return arr;

    }

    public int[] function(int[][] matrix) {
        if (matrix.length <= 0) {
            return new int[]{};
        }
        //根据每个元素x和y的初始化集合
        List<List<Integer>> axisCount = new ArrayList<>();
        //x和y轴之后最大值
        int maxLength = matrix.length + matrix[0].length - 1;

        for (int i = 0; i < maxLength; i++) {
            //初始化集合
            axisCount.add(new ArrayList<>());
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                //计算每个元素x和y的和
                int a = i + j;
                int value = matrix[i][j];
                //根据x和y轴的和获取list，存放当前value
                axisCount.get(a).add(value);
            }
        }
        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < axisCount.size(); i++) {
            List<Integer> singleList = axisCount.get(i);
            //偶数按照插入顺序倒叙排列
            if (i % 2 == 0) {
                Collections.reverse(axisCount.get(i));
            }
            //奇数
            dataList.addAll(singleList);
        }
        int[] arr = new int[dataList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = dataList.get(i);
        }
        return arr;

    }


}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }
}

//class Node {
//    int val;
//    Node next;
//    Node random;
//
//    public Node(int val) {
//        this.val = val;
//        this.next = null;
//        this.random = null;
//    }
//}
class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}