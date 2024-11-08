package com.lhl.xyks.solve;

import com.lhl.xyks.pojo.Color;
import com.lhl.xyks.pojo.Point;
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
    static boolean isFirst = true; // 绘制的答案来自当前题目吗
    static BufferedImage currentImage; // 上面的题缓存
    static BufferedImage nextImage; // 下面的题缓存
    static Point lineStart; // 检测书写完成的水平线 点1
    static Point lineEnd; // 检测书写完成的水平线 点2

    /**
     * 执行连续的自动答题
     *
     * @param questionOCR 对应类型题目的识别器
     * @param num         题目数量
     */
    public static void execute(QuestionOCR questionOCR, int num) {
        // 准备答题列表
        for (int i = 1; i <= num + ConfigParser.globalConfig.mistakeTryNum; i++) {
            questions.add(new Question(i));
        }
        log("答题列表准备完毕");

        for (int i = 0; i < questions.size(); i++) {
            // 通过页眉蓝色区域有没有变白判断答题是否结束
            if (!screen.isPointColorLike(ConfigParser.globalConfig.point6, ConfigParser.globalConfig.point6.color))
                break;
            log("开始答题，当前题号：" + (i + 1) + "，isFirst=" + isFirst);
            if (isFirst) {
                isFirst = false;
                // 如果是第一次，先识别再画答案
                currentImage = screen.capture(ConfigParser.globalConfig.currentArea);
                nextImage = screen.capture(ConfigParser.globalConfig.nextArea);

                currentImage = ImageTools.pngToBinary(currentImage, 0.5f);
                String currentResult;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    currentResult = questionOCR.multiOCR(currentImage);
                } else {
                    currentResult = questionOCR.singleOCR(currentImage);
                }
                questions.get(i).updateExpression(currentResult);

                waitDraw();
                log("[绘制] 当前识别，题号：" + i + 1);
                doDraw(questions.get(i));

                if (i == questions.size()) break;
                nextImage = ImageTools.pngToBinary(nextImage, 0.85f);
                nextImage = ImageTools.resizePNG(nextImage, 1.4, 1.4);
                String nextResult;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    nextResult = questionOCR.multiOCR(nextImage);
                } else {
                    nextResult = questionOCR.singleOCR(nextImage);
                }
                questions.get(i + 1).updateExpression(nextResult);

                log("[当前识别模式] currentResult=" + currentResult + "，nextResult=" + nextResult);
            } else {
                // 如果无答案，说明预识别失败，重新识别
                if (i != num - 1 && questions.get(i).ans.equals("-")) {
                    log("[校验] 预识别无答案");
                    isFirst = true;
                    questions.get(i).info = "识别失败";
                    continue;
                }
                waitDraw();
                // 这里是小猿口算软件内算式切换的动画时长
                try {
                    Thread.sleep(ConfigParser.globalConfig.questionAnimationDuration);
                } catch (InterruptedException ignore) {
                }
                // 截取下面的题 （等动画过去再截图，否则容易截到上一张）
                nextImage = screen.capture(ConfigParser.globalConfig.nextArea);
                // 画当前题，如果答案为空，重新识别
                if ("-".equals(questions.get(i).ans)) {
                    isFirst = true;
                    questions.get(i).info = "无效答案";
                    continue;
                }
                doDraw(questions.get(i));

                // 预识别
                nextImage = ImageTools.pngToBinary(nextImage, 0.85f);
                nextImage = ImageTools.resizePNG(nextImage, 1.4, 1.4);
                String result;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    result = questionOCR.multiOCR(nextImage);
                } else {
                    result = questionOCR.singleOCR(nextImage);
                }
                log("[预先识别模式] result=" + result);
                // 如果识别到的下一题和理论上的当前题一致，说明上一题失败了，重来
                if (result.split(":")[0].equals(questions.get(i).text)) {
                    isFirst = true;
                    questions.get(i).info = "重复识别";
                    continue;
                }
                // 把结果存下来
                questions.get(i + 1).updateExpression(result);
            }

            try {
                log(questions.get(i - 1).toString()
                        + questions.get(i).toString()
                        + questions.get(i + 1).toString());
            } catch (Exception ignore) {
            }
        }

        log("[外层大循环] 结束，退出本轮答题");
        waitDraw();
    }

    private static void doDraw(Question question) {
        log("[绘制调用] 要绘制的问题：" + question.text + "，ans=" + question.ans);
        if ("-".equals(question.ans)) {
            question.info = "无效答案";
            return;
        }
        drawing = new Thread(() -> {
            question.info = "作答中";
            mouse.drawSymbols(question.ans, ConfigParser.globalConfig.point5);
            question.info = "完成";
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
        long drawFinish = System.currentTimeMillis();
        log("[wait] 判断书写水平线上是否存在黑色");
        if (lineStart == null) {
            int x1 = ConfigParser.globalConfig.currentArea.x;
            int x2 = ConfigParser.globalConfig.currentArea.width + x1;
            int y = ConfigParser.globalConfig.point5.y;
            y += ConfigParser.globalConfig.drawSymbolWidth;
            lineStart = new Point(x1, y);
            lineEnd = new Point(x2, y);
        }
        screen.waitWhileLineColorContains(lineStart, lineEnd, Color.hexToColor("#000000"));
        long handwritingDisappears = System.currentTimeMillis();
        log("[wait] 笔刷条件“黑色消失”成立");
        log("[wait] 等待的总时间" + (handwritingDisappears - start)
                + "ms，线程阻塞" + (drawFinish - start)
                + "ms，字迹阻塞" + (handwritingDisappears - drawFinish) + "ms");
    }


    private static void log(String message) {
        if (true) {
            System.out.println(message);
        }
    }
}
