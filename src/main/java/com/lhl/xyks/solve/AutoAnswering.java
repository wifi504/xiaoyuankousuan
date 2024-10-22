package com.lhl.xyks.solve;

import com.lhl.xyks.pojo.Question;
import com.lhl.xyks.utils.ConfigParser;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 自动答题器
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_15:55
 */
public class AutoAnswering {

    // 工具
    static Screen screen = Screen.getScreen();
    static Mouse mouse = Mouse.getMouse();

    // 题目缓存
    static ArrayList<Question> questions = new ArrayList<>();


    static Thread drawing = null; // 绘制线程
    static int currentNum = 0; // 当前题号
    static int index = 0; // 当前循环回合
    static boolean isFirst = true; // 绘制的答案来自当前题目吗

    /**
     * 执行连续的自动答题
     *
     * @param questionOCR 对应类型题目的识别器
     * @param num         题目数量
     */
    public static void execute(QuestionOCR questionOCR, int num) {
        // 准备答题列表
        for (int i = 1; i <= num; i++) {
            questions.add(new Question(i));
        }
        log("答题列表准备完毕");

        // 通过页眉蓝色区域有没有变白判断答题是否结束
        while (screen.isPointColorLike(ConfigParser.globalConfig.point6, ConfigParser.globalConfig.point6.color)) {
            // 如果重试次数超过了允许的失败重试次数，结束本轮答题
            index++;
            if (index > num + ConfigParser.globalConfig.mistakeTryNum) break;
            log("-------------< 当前回合：" + index + " >-----------");
            // 题目指针
            if (currentNum >= num) {
                currentNum = 0;
                isFirst = true;
                index = 0;
                break;
            } else {
                currentNum++;
            }
            log("开始答题，当前题号：" + currentNum + "，isFirst=" + isFirst);

            if (isFirst) {
                isFirst = false;
                // 如果是第一次，先识别再画答案
                BufferedImage currentImage = screen.capture(ConfigParser.globalConfig.currentArea);
                BufferedImage nextImage = screen.capture(ConfigParser.globalConfig.nextArea);

                currentImage = ImageTools.pngToBinary(currentImage, 0.5f);
                String currentResult;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    currentResult = questionOCR.multiOCR(currentImage);
                } else {
                    currentResult = questionOCR.singleOCR(currentImage);
                }
                questions.get(-1 + currentNum).updateExpression(currentResult);

                waitDraw();
                log("[绘制] 当前识别，题号：" + currentNum);
                doDraw(questions.get(-1 + currentNum));

                nextImage = ImageTools.pngToBinary(nextImage, 0.85f);
                nextImage = ImageTools.resizePNG(nextImage, 1.4, 1.4);
                String nextResult;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    nextResult = questionOCR.multiOCR(nextImage);
                } else {
                    nextResult = questionOCR.singleOCR(nextImage);
                }
                questions.get(-1 + currentNum + 1).updateExpression(nextResult);

                log("[当前识别模式] currentResult=" + currentResult + "，nextResult=" + nextResult);
            } else {
                // 画当前题
                BufferedImage image = screen.capture(ConfigParser.globalConfig.nextArea);
                waitDraw();
                doDraw(questions.get(-1 + currentNum));
                if (currentNum != num) {
                    // 已经有当前答案了，只需要识别下一题
                    image = ImageTools.pngToBinary(image, 0.85f);
                    image = ImageTools.resizePNG(image, 1.4, 1.4);
                    String result;
                    if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                        result = questionOCR.multiOCR(image);
                    } else {
                        result = questionOCR.singleOCR(image);
                    }
                    log("[预先识别模式] result=" + result);
                    // 如果识别到的下一题和理论上的当前题一致，说明上一题失败了，重来
                    if (result.split(":")[0].equals(questions.get(-1 + currentNum).text)) {
                        isFirst = true;
                        currentNum--;
                        continue;
                    }
                    // 把结果存下来
                    questions.get(-1 + currentNum + 1).updateExpression(result);
                } else {
                    // 这是最后一题，等待画完，再画一次，结束
                    BufferedImage endImage = screen.capture(ConfigParser.globalConfig.currentArea);
                    endImage = ImageTools.pngToBinary(endImage, 0.5f);
                    String endResult;
                    if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                        endResult = questionOCR.multiOCR(endImage);
                    } else {
                        endResult = questionOCR.singleOCR(endImage);
                    }
                    questions.get(-1 + currentNum).updateExpression(endResult);
                    waitDraw();
                    doDraw(questions.get(-1 + currentNum));
                }
            }

//            log(questions.toString());
        }
        log("[外层大循环] 结束，退出本轮答题");
        waitDraw();
    }

    private static void doDraw(Question question) {
        log("[绘制调用] 要绘制的问题：" + question.text + "，ans=" + question.ans);
        drawing = new Thread(() -> {
            question.info = "作答中";
            mouse.drawSymbols(question.ans, ConfigParser.globalConfig.point5);
            question.info = "验证中";
        });
        drawing.start();
    }

    private static void waitDraw() {
        long start = System.currentTimeMillis();
        log("[wait] 方法调用...");
        if (drawing != null) {
            log("[wait] 在绘制期间，等待线程");
            try {
                drawing.join();
            } catch (InterruptedException ignore) {
            }
            drawing = null;
        }
        if (Mouse.checkHandwriting != null) {
            log("[wait] 笔刷起点的颜色是" + screen.getColorAt(Mouse.checkHandwriting));
            log("[wait] 要判断的颜色是" + ConfigParser.globalConfig.point5.color);
            screen.waitUntilPointColorLike(Mouse.checkHandwriting, ConfigParser.globalConfig.point5.color, 1000);
            log("[wait] 笔刷条件成立");
            Mouse.checkHandwriting = null;
        }
        long end = System.currentTimeMillis();
        if (end - start < 500) {
            try {
                log("[wait] 固定延迟...");
                Thread.sleep(500 - end + start);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log("[wait] 等待的总时间：" + (System.currentTimeMillis() - start));
    }


    private static void log(String message) {
        if (true) {
            System.out.println(message);
        }
    }
}
