package leetCode;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @Author: leah_ana
 * @Date: 2022/4/25 16:25
 * @Desc: 剑指 Offer 30. 包含min函数的栈
 */
class MinStack {

    Deque<Integer> xStack;
    Deque<Integer> minStack;

    /**
     * initialize your data structure here.
     */
    public MinStack() {
        xStack = new LinkedList<Integer>();
        minStack = new LinkedList<Integer>();
        minStack.push(Integer.MAX_VALUE);
    }

    public void push(int x) {
        xStack.push(x);
        // 添加到xStack栈中的元素x 和辅助栈的顶层最小元素进行对比,将辅助栈中的最小值更新
        minStack.push(Math.min(minStack.peek(), x));
    }

    public void pop() {
        xStack.pop();
        minStack.pop();
    }

    public int top() {
        return xStack.peek();
    }

    public int min() {
        // 辅助栈只有一个元素,那么返回的就是辅助栈的顶层元素
        return minStack.peek();
    }
}

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(x);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.min();
 */