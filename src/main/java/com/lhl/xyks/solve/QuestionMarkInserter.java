package com.lhl.xyks.solve;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/20_0:21
 */
public class QuestionMarkInserter {
    /**
     * 根据两个错误的OCR结果和准确的数字推断问号位置并插入问号
     *
     * @param ocr1 错误的OCR结果1
     * @param ocr2 错误的OCR结果2
     * @param accurateNumber 没有问号的准确数字
     * @return 插入问号后的结果
     */
    public static String insertQuestionMark(String ocr1, String ocr2, String accurateNumber) {
        int bestPosition = -1;
        int minError = Integer.MAX_VALUE;

        // 遍历可能的问号插入位置
        for (int i = 1; i < accurateNumber.length(); i++) {
            String leftPart = accurateNumber.substring(0, i);
            String rightPart = accurateNumber.substring(i);

            // 检查左侧和右侧的数字是否小于等于10,000，并且问号后不能跟0
            if (isNumeric(leftPart) && isNumeric(rightPart) && rightPart.charAt(0) != '0') {
                int leftNumber = Integer.parseInt(leftPart);
                int rightNumber = Integer.parseInt(rightPart);

                if (leftNumber <= 10000 && rightNumber <= 10000) {
                    // 计算与前两个OCR结果的误差
                    int error = calculateError(ocr1, ocr2, leftPart, rightPart);
                    if (error < minError) {
                        minError = error;
                        bestPosition = i;
                    }
                }
            }
        }

        // 如果找到最佳位置，插入问号
        if (bestPosition != -1) {
            return accurateNumber.substring(0, bestPosition) + "?" + accurateNumber.substring(bestPosition);
        }

        // 如果未找到合适的位置，返回原数字
        return accurateNumber;
    }

    /**
     * 检查字符串是否为数字
     */
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 计算与前两个OCR结果的误差
     */
    private static int calculateError(String ocr1, String ocr2, String leftPart, String rightPart) {
        int error = 0;

        // 计算左部分与OCR结果的误差
        error += Math.min(getPartError(ocr1, leftPart), getPartError(ocr2, leftPart));

        // 计算右部分与OCR结果的误差
        error += Math.min(getPartError(ocr1, rightPart), getPartError(ocr2, rightPart));

        return error;
    }

    /**
     * 计算单个部分与OCR结果的误差
     */
    private static int getPartError(String ocr, String part) {
        int index = ocr.indexOf(part);
        if (index == -1) {
            return Math.abs(ocr.length() - part.length());
        }
        return 0;
    }

    public static void main(String[] args) {
        // 测试用例
        System.out.println(insertQuestionMark("1032?3848", "1102?2369", "1022369"));  // 102?2369
        System.out.println(insertQuestionMark("1757424", "175?4424", "175424"));      // 175?424
        System.out.println(insertQuestionMark("23?7448", "233?448", "23448"));        // 23?448
        System.out.println(insertQuestionMark("443?7490", "443?498", "443490"));      // 443?490
        System.out.println(insertQuestionMark("550?0933", "5580?933", "550933"));     // 550?933
        System.out.println(insertQuestionMark("34?7254", "34?284", "34254"));         // 34?254
        System.out.println(insertQuestionMark("7177?5178", "717?5618", "7175178"));   // 717?5178
        System.out.println(insertQuestionMark("7837?1763", "783?17643", "7831763"));  // 783?1763
    }
}
