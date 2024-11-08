package com.lhl.xyks.pojo;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;

/**
 * 单个小题的 Pojo
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_16:46
 */
public class Question {
    // 答案格式化
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.####");

    public int num; // 题号
    public String text = "-"; // 题目
    public String model = "-"; // 识别模型
    public String ans = "-"; // 答案
    public String info = "-"; // 状态
    public String exp = ""; // 表达式原始值

    @Override
    public String toString() {
        return "\nQuestion{" +
                "num=" + num
                + ", text='" + text + '\''
                + ", model='" + model + '\''
                + ", ans='" + ans + '\''
                + ", info='" + info + '\''
                + "}";
    }

    public Question(int num) {
        this.num = num;
    }

    /**
     * 更新题目表达式
     *
     * @param exp 表达式
     */
    public void updateExpression(String exp) {
        this.exp = exp;
        try {
            String[] split = exp.split(":");
            // 更新题目
            text = split[0];
            // 更新模型
            model = split[1];
            // 判断题型，更新结果
            if (text.substring(text.length() - 2).contains("=")) {
                // 算式题
                double evaluate = new ExpressionBuilder(text.substring(0, text.indexOf('='))).build().evaluate();
                ans = decimalFormat.format(evaluate);
            } else {
                // 比大小题
                String[] numsStr = text.split("\\?");
                ans = Integer.parseInt(numsStr[0]) > Integer.parseInt(numsStr[1]) ? ">" : "<";
            }
            // 更新状态
            info = "等待中";
        } catch (Exception e) {
            info = "识别异常";
        }
    }
}
