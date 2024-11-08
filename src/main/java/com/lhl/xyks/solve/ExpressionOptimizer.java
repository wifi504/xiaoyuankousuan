package com.lhl.xyks.solve;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式优化器
 * 根据不同题型特征，将识别结果进一步优化
 * 硬核算法，纯算法，两个错误的OCR结果，我也能给你预测一个正确的
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_21:23
 */
public class ExpressionOptimizer {


    /**
     * 20以内的数比大小  (测试结果 200 个 错 0 个)
     *
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param paddle Paddle 模型(原图) 识别结果
     * @param eng    ENG(1.5模糊) 模型 识别结果
     * @return 优化表达式
     */
    public static String optimizeCompare20(String xyks, String paddle, String eng) {
        try {
            xyks = xyks.replaceAll("-", "");
            eng = eng.replaceAll("-", "");
            // 如果小猿口算识别的数字两个都是20以内，认为正确
            String[] split = xyks.split("\\?");
            String left = split[0];
            String right = split[1];
            if (Integer.parseInt(left) <= 20 && Integer.parseInt(right) <= 20) return xyks + ":小猿模型";

            // 识别有错，利用 Paddle 的结果纠错

            // 如果 Paddle 的尾巴不是？，并且中间有问号，说明它按序识别了，返回
            if (paddle.charAt(paddle.length() - 1) != '?' && paddle.contains("?")) return paddle + ":Paddle";

            // Paddle 的识别没按序，判断字符串长度，逐个还原答案

            // 两个 1 位数，问号直接插中间
            if (paddle.length() == 3 && paddle.contains("?")) {
                if (paddle.charAt(0) == xyks.charAt(0)) {
                    // 不换位
                    return paddle.charAt(0) + "?" + paddle.charAt(1) + ":Paddle+小猿推测";
                }
                // 换位
                return paddle.charAt(1) + "?" + paddle.charAt(0) + ":Paddle+小猿推测";
            }

            // 1 位数 和 两位数
            if (paddle.length() == 4 && paddle.contains("?")) {
                // 如果第一位比2还大，肯定是 xx ? x
                if (Integer.parseInt(paddle.charAt(0) + "") >= 2)
                    return paddle.substring(1) + paddle.charAt(0) + ":Paddle+小猿推测";
                // 否则是 x ? xx
                return paddle.substring(2, 4) + paddle.substring(0, 2) + ":Paddle+小猿推测";
            }

            if (eng.length() == paddle.length() && eng.length() == 5) {
                if (eng.charAt(2) == '?') return eng + ":ENG";
                return eng.substring(0, 2) + "?" + eng.substring(3) + ":ENG+小猿推测";
            }

            throw new RuntimeException();
        } catch (Exception e) {
            // 到这里的概率很小了，直接随机看天意了
            if (Math.random() < 0.5) {
                return "99?0:Random";
            }
            return "0?99:Random";
        }
    }

    /**
     * 100以内的数比大小  (测试结果 200 个 错 0 个)
     *
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param eng    ENG 模型(1.5模糊) 识别结果
     * @param paddle Paddle 模型(原图) 识别结果
     * @return 优化表达式
     */
    public static String optimizeCompare100(String xyks, String eng, String paddle) {
        try {
            xyks = xyks.replaceAll("-", "");
            eng = eng.replaceAll("-", "");
            // 如果小猿口算识别的数字两个都是100以内，认为正确
            String[] split = xyks.split("\\?");
            String left = split[0];
            String right = split[1];
            if (Integer.parseInt(left) <= 100 && Integer.parseInt(right) <= 100 && xyks.length() == paddle.length())
                return xyks + ":小猿模型";

            // 否则看 ENG
            split = eng.split("\\?");
            left = split[0];
            right = split[1];
            if (Integer.parseInt(left) <= 100 && Integer.parseInt(right) <= 100) return eng + ":ENG";

            // 否则借助 Paddle
            if (paddle.length() == 5) {
                // 比较左右两边的相似度，决定换不换位
                double leftSimilarity = getStringSimilarity(left, paddle.substring(0, 2));
                double rightSimilarity = getStringSimilarity(left, paddle.substring(2, 4));
                if (leftSimilarity > rightSimilarity) {
                    // 左边更像左边，不用换
                    return paddle.substring(0, 2) + "?" + paddle.substring(2, 4) + ":Paddle+小猿推测";
                } else {
                    // 左边更像右边，换
                    return paddle.substring(2, 4) + "?" + paddle.substring(0, 2) + ":Paddle+小猿推测";
                }
            }

            String s = QuestionMarkInserter.insertQuestionMark(xyks, eng, paddle);
            return s + ":Paddle+小猿推测";


        } catch (Exception e) {
            // 其他情况看天意吧
            if (Math.random() < 0.5) {
                return "999?0:Random";
            }
            return "0?999:Random";
        }
    }

    /**
     * 一万以内的数比大小  (测试结果 500 个 错 3 个)
     *
     * @param eng    ENG 模型(1.5模糊) 识别结果
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param paddle Paddle 模型(原图) 识别结果
     * @return 优化表达式
     */
    public static String optimizeCompare1w(String eng, String xyks, String paddle) {
        try {
            xyks = xyks.replaceAll("-", "");
            eng = eng.replaceAll("-", "");

            // 判断 ENG 有没有可能正确
            if (eng.contains("?") && eng.length() == paddle.length()) return eng + ":ENG";

            // 判断 xyks 有没有可能正确
            if (xyks.contains("?") && xyks.length() == paddle.length()) return xyks + ":小猿模型";

            // Paddle 问号位置对不对
            if (paddle.charAt(paddle.length() - 1) != '?') return paddle + ":Paddle";

            // 这种情况拼凑 Paddle

            // 找到 问号可能存在的位置
            String s = QuestionMarkInserter.insertQuestionMark(eng, xyks, paddle.substring(0, paddle.length() - 1));
            return s + ":Paddle+小猿推测";
        } catch (Exception e) {
            if (Math.random() < 0.5) {
                return "99999?0:Random";
            }
            return "0?99999:Random";
        }
    }

    /**
     * 小数乘法的口算  (测试结果 500 个 错 1 个)
     *
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param paddle Paddle 模型(1.5模糊然后2.0锐化) 识别结果
     * @return 优化表达式
     */
    public static String optimizeDecimalMultiplication(String xyks, String paddle) {

        // 如果数字以0开头，又没小数点，则添加小数点
        xyks = addPointAfter0(xyks);
        paddle = addPointAfter0(paddle);

        // 判断有没有两个连续符号出现
        Pattern pattern = Pattern.compile("[+\\-*/%^=]{2,}");
        boolean isValid = !pattern.matcher(xyks).find();

        if (xyks.length() == paddle.length() && isValid) return xyks + ":小猿模型";
        if (paddle.charAt(0) == '?') {
            paddle = paddle.substring(1);
        }
        return paddle + ":Paddle";
    }

    private static String addPointAfter0(String s) {
        if (!s.isEmpty()) {
            if (s.charAt(0) == '0' && s.charAt(1) != '.') {
                s = "0." + s.substring(1);
            }
            int index = s.indexOf('*');
            if (s.charAt(index + 1) == '0' && s.charAt(index + 2) != '.') {
                s = s.substring(0, index + 2) + '.' + s.substring(index + 2);
            }
        }
        return s;
    }


    /**
     * 多位数除法的口算  (测试结果 500 个 错 0 个)
     *
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param paddle Paddle 模型(原图) 识别结果
     * @param eng    ENG 模型(原图) 识别结果
     * @return 优化表达式
     */
    public static String optimizeDivision(String xyks, String paddle, String eng) {
        // 先判断paddle(原图)表达式合法吗
        String invalidPattern = "^[*/+]"         // 开头有 */+
                + "|=[*/+]"                     // 等号右边有 */+
                + "|[+-/*^%]={1}"               // 等号左边有 -
                + "|[+\\-*/%^=]{2,}";           // 连续符号
        Pattern pattern = Pattern.compile(invalidPattern);
        boolean isPaddleValid = !pattern.matcher(paddle).find() && paddle.contains("/");
        boolean isXyksValid = !pattern.matcher(xyks).find() && xyks.contains("/");

        // 然后匹配长度是不是小猿（拉伸），则输出
        if (isXyksValid && isPaddleValid && xyks.length() == paddle.length()) return xyks + ":小猿模型";

        // 如果是长度问题，输出paddle
        if (isPaddleValid) return paddle + ":Paddle";
        // 否则输出 eng（原图） 替换+，等号右边只有?
        String res = eng.replaceAll("\\+", "/");
        int index = res.indexOf('=');
        if (index == -1) index = res.length();
        return res.substring(0, index).replaceAll("-", "") + "=?:ENG+小猿推测";
    }

    /**
     * 两三位数加减法  (测试结果 200 个 错 0 个)
     *
     * @param paddle Paddle 模型(1.25拉伸) 识别结果
     * @return 优化表达式
     */
    public static String optimizeIntegerAdditionSubtraction(String paddle) {
        // 这个类型，Paddle逆天的好，经过核实，原来0.985的正确率，是数据集标注错了。。。
        // 人家正确率 100%
        return paddle + ":Paddle";
    }

    /**
     * 3.14、平方数有关的口算  (测试结果 100 个 错 0 个)
     *
     * @param paddle Paddle 模型(1.5模糊) 识别结果
     * @return 优化表达式
     */
    public static String optimizePiSquare(String paddle) {
        // 这个 Paddle 也好的离谱，就是*有概率被识别成x
        String res = paddle.replaceAll("x", "*");
        // 等号右边不能有东西
        int index = res.lastIndexOf("=");
        if (index != -1) {
            res = res.substring(0, index);
        }
        return res + "=?:Paddle";
    }

    /**
     * 混合运算  (测试结果 200 个 错 6 个)
     *
     * @param xyks   小猿模型(1.25拉伸) 识别结果
     * @param paddle Paddle 模型(1.25拉伸) 识别结果
     * @return 优化表达式
     */
    public static String optimizeArithmetic(String xyks, String paddle) {
        // 混合运算一定是三个数字，Paddle的数字很可靠，小猿的符号很可靠
        // 在 Paddle 里，从左到右，能判断出三个纯数字，纯数字找出来存数组
        List<Integer> paddleNumbers = extractNumbers(paddle);

        // 如果 Paddle 有 除号，或者")*" "-("，认为它没瞎，直接返回吧
        if ((paddle.contains("/") || paddle.contains(")*") || paddle.contains(")=")
                || paddle.contains("-(") || paddle.contains("*("))
                && paddleNumbers.size() == 3) {
            String res = paddle.replaceAll(":", "/")
                    .replaceAll("=", "/") + ":Paddle";
            res = res.replaceAll("/\\?", "=?");
            if (res.charAt(res.length() - 8) == '/') return
                    res.substring(0, res.length() - 8) + "=?:Paddle";
            return res;
        }

        // 如果存在右括号并且后面是减，那肯定错了，是除
        int index = paddle.indexOf(")");
        if (index != -1 && paddle.charAt(index + 1) == '-') return paddle.substring(0, index + 1)
                + "/" + paddle.substring(index + 2) + ":Paddle+小猿推测";

        // 大抵是瞎了
        List<Integer> xyksNumbers = extractNumbers(xyks);
        if (paddleNumbers.size() == 3) {
            // 如果 在 小猿 里，从左到右，也能判断出三个纯数字，并且合法，则纯符号找出来
            // 小猿合法吗
            Pattern pattern = Pattern.compile("[+\\-*/%^=]{2,}");
            boolean isValid = !pattern.matcher(xyks).find();
            if (xyksNumbers.size() == 3 && isValid) {
                // 找纯符号
                List<String> strings = extractNonNumbers(xyks);
                String res = "";
                // 如果这个符号数组，就2个，毋庸置疑，肯定插空
                if (strings.size() == 2) {
                    res += paddleNumbers.get(0);
                    res += strings.get(0);
                    res += paddleNumbers.get(1);
                    res += strings.get(1);
                    res += paddleNumbers.get(2);
                    return res + "=?:Paddle+小猿组合推测";
                }
                // 如果符号数组是 3 个，那就是有括号的情况了
                if (strings.size() == 3) {
                    // 左边是括号
                    if (strings.get(0).charAt(0) == '(') {
                        res += strings.get(0);
                        res += paddleNumbers.get(0);
                        res += strings.get(1);
                        res += paddleNumbers.get(1);
                        res += strings.get(2);
                        if (strings.get(2).equals(")")) res += "/";
                        res += paddleNumbers.get(2);
                        return res + "=?:Paddle+小猿组合推测";
                    }
                    // 右边是括号
                    res += paddleNumbers.get(0);
                    res += strings.get(0);
                    res += paddleNumbers.get(1);
                    res += strings.get(1);
                    res += paddleNumbers.get(2);
                    res += strings.get(2);
                    return res + "=?:Paddle+小猿组合推测";
                }
            }

            // 小猿 寄了，我们试着让 Paddle 的符号别错
            // 先拿左边子串
            int equalsIndex = paddle.lastIndexOf('=');
            String leftSide = paddle.substring(0, equalsIndex);
            // 等号和冒号 肯定是 除
            leftSide = leftSide.replaceAll("=", "/")
                    .replaceAll(":", "/");
            int index1 = leftSide.indexOf(")");
            char c = leftSide.charAt(index1 + 1);
            if (c >= '0' && c <= '9') leftSide = leftSide.replaceAll("\\)", ")/");
            return leftSide + "=?:Paddle+小猿纠错";
        }
        // Paddle 判断不出来三个纯数字，直接看小猿的
        if (xyksNumbers.size() == 3) {
            // 判断有没有两个连续符号出现
            Pattern pattern = Pattern.compile("[+\\-*/%^=]{2,}");
            if (!pattern.matcher(xyks).find()) return xyks + ":小猿模型";
        }
        // 这下没人能对了，再赌一把，不是三个数字，就是两个了呗
        // 是两个多半就是除号识别掉了，找到最大的 Paddle 的纯数字
        if (paddleNumbers.size() == 2) {
            // 把最大的找出来
            int max = Math.max(paddleNumbers.get(0), paddleNumbers.get(1));
            String maxStr = String.valueOf(max);
            // 两位数，直接切
            if (maxStr.length() == 2) return paddle.replaceAll(maxStr,
                    maxStr.charAt(0) + "/" + maxStr.charAt(1)) + ":Paddle+小猿推测Plus";
            // 三位数，两种可能，成立条件：口算题，肯定得除的尽啊
            if (maxStr.length() == 3) {
                // x / xx 这种必然除不尽，舍了
                // xx / x
                return paddle.replaceAll(maxStr, maxStr.substring(0, 2)
                        + "/" + maxStr.charAt(2)) + ":Paddle+小猿推测Plus";
            }
            // 四位数，三种可能，同理
            if (maxStr.length() == 4) {
                // x / xxx 不可能
                // xx / xx 可能
                int left = Integer.parseInt(maxStr.substring(0, 2));
                int right = Integer.parseInt(maxStr.substring(2, 4));
                if (left % right == 0) return paddle.replaceAll(maxStr,
                        left + "/" + right) + ":Paddle+小猿推测Plus";
                // xxx / x 可能
                left = Integer.parseInt(maxStr.substring(0, 3));
                right = Integer.parseInt(maxStr.charAt(3) + "");
                if (left % right == 0) return paddle.replaceAll(maxStr,
                        left + "/" + right) + ":Paddle+小猿推测Plus";
            }
        }
        // 你就一个数字，别玩儿了，废了
        return "0+0+0=?:Error";
    }

    /**
     * 获取两个字符串的相似度
     *
     * @param s1 字符串
     * @param s2 字符串
     * @return 相似度，范围为0到1
     */
    public static double getStringSimilarity(String s1, String s2) {
        // 获取两个字符串的最大长度
        int maxLength = Math.max(s1.length(), s2.length());
        // 如果两个字符串都为空，返回相似度1
        if (maxLength == 0) {
            return 1.0;
        }

        // 使用Levenshtein距离计算两个字符串之间的差异
        int distance = levenshteinDistance(s1, s2);

        // 相似度 = (1 - (编辑距离 / 最大长度))
        return 1.0 - (double) distance / maxLength;
    }

    /**
     * 计算Levenshtein距离（编辑距离）
     *
     * @param s1 字符串
     * @param s2 字符串
     * @return Levenshtein距离
     */
    public static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        // 初始化dp数组
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        // 填充dp数组
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];  // 无需编辑
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1],  // 替换
                            Math.min(dp[i - 1][j],    // 删除
                                    dp[i][j - 1])) + 1;  // 插入
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * 查找算式并返回左边部分的数字数组
     *
     * @param expression 算式
     * @return 左边部分的数字数组
     */
    public static List<Integer> extractNumbers(String expression) {
        // 找到从右边开始的第一个等号
        int equalsIndex = expression.lastIndexOf('=');
        if (equalsIndex == -1) {
            return new ArrayList<>();  // 没有等号，返回空列表
        }

        // 截取等号左边的部分
        String leftSide = expression.substring(0, equalsIndex);

        // 使用正则表达式匹配纯数字
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(leftSide);

        // 存储数字
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        return numbers;  // 返回找到的数字列表
    }

    /**
     * 查找算式并返回左边部分的非数字符号数组
     *
     * @param expression 算式
     * @return 左边部分的非数字符号数组
     */
    public static List<String> extractNonNumbers(String expression) {
        // 找到从右边开始的第一个等号
        int equalsIndex = expression.lastIndexOf('=');
        if (equalsIndex == -1) {
            return new ArrayList<>();  // 没有等号，返回空列表
        }

        // 截取等号左边的部分
        String leftSide = expression.substring(0, equalsIndex);

        // 使用正则表达式移除数字，保留其他符号
        // 这里 \\D+ 匹配数字以外的连续部分
        List<String> nonNumberParts = new ArrayList<>();
        String[] parts = leftSide.split("\\d+");  // 使用数字分割字符串

        // 将非数字的部分加入结果
        for (String part : parts) {
            if (!part.isEmpty()) {
                nonNumberParts.add(part);
            }
        }

        return nonNumberParts;  // 返回找到的非数字部分
    }
}
