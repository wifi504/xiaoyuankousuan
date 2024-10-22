package com.lhl.xyks.solve;

import com.lhl.xyks.pojo.Question;
import com.lhl.xyks.utils.ConfigParser;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;
import net.sourceforge.jeuclid.elements.presentation.token.Mo;

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


    static boolean isDrawing = false; // 绘制状态
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
        log(44, "答题列表准备完毕");

        // 通过页眉蓝色区域有没有变白判断答题是否结束
        while (screen.isPointColorLike(ConfigParser.globalConfig.point6, ConfigParser.globalConfig.point6.color)) {
            // 如果重试次数超过了允许的失败重试次数，结束本轮答题
            if (index > num + ConfigParser.globalConfig.mistakeTryNum) break;
            index++;
            log(49, "-------------< 当前回合：" + index + " >-----------");
            // 题目指针
            if (currentNum >= num) {
                currentNum = 0;
                isFirst = true;
                index = 0;
                break;
            } else {
                currentNum++;
            }
            log(59, "开始答题，当前题号：" + currentNum);

            if (!isFirst) {
                // 只要不是第一次画答案，结果集里都有答案，直接画
                log(63, "等待绘制（预识别），题号：" + currentNum);
                doDraw(questions.get(-1 + currentNum));
                waitDraw();
            }

            // 截题目图，如果是首题模式，截当前，否则截下一个。如果是最后一题，截一个保险
            BufferedImage image;
            if (isFirst || currentNum == num) {
                image = screen.capture(ConfigParser.globalConfig.currentArea);
                image = ImageTools.pngToBinary(image, 0.5f);
            } else {
                image = screen.capture(ConfigParser.globalConfig.nextArea);
                image = ImageTools.pngToBinary(image, 0.85f);
                image = ImageTools.resizePNG(image, 1.4, 1.4);
            }

            // 多线程识别吗
            String result;
            if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                result = questionOCR.multiOCR(image);
            } else {
                result = questionOCR.singleOCR(image);
            }

            // 如果是最后一题，主图识别结果和原来一样，稳了，这把直接结束，否则退回去再画一遍
            if (currentNum == num) {
                if (questions.get(-1 + currentNum).text.equals(result.split(":")[0])) {
                    continue;
                }
                questions.get(-1 + currentNum).updateExpression(result);
                currentNum--;
                continue;
            }

            if (isFirst) {
                // 第一次的状态，就用现在的识别更新题目
                questions.get(-1 + currentNum).updateExpression(result);
                log(106, "First更新，上面识别" + result);

            } else {
                log(93, "下面识别：" + result);
                log(94, "当前的题：" + questions.get(-1 + currentNum).exp);
                // 如果识别的题目等于当前题目了（上次识别的下一题），说明上个题错了，重试
                if (questions.get(-1 + currentNum).text.equals(result.split(":")[0])) {
                    log(89, "在题号：" + currentNum + "发现两次next重复");
                    currentNum--;
                    isFirst = true; // 重新以首题模式作答
                    continue;
                } else {
                    // 缓存下个题
                    log(93, "成功缓存题号：" + (currentNum + 1));
                    questions.get(-1 + currentNum + 1).updateExpression(result);
                    log(106, "预更新");
                }
            }

            if (isFirst) {
                // 第一次画，提前把第二题答案准备了
                BufferedImage nextImage = screen.capture(ConfigParser.globalConfig.nextArea);
                nextImage = ImageTools.pngToBinary(nextImage, 0.85f);
                nextImage = ImageTools.resizePNG(nextImage, 1.4, 1.4);
                String nextResult;
                if (ConfigParser.globalConfig.allowOCRMultiThreading) {
                    nextResult = questionOCR.multiOCR(nextImage);
                } else {
                    nextResult = questionOCR.singleOCR(nextImage);
                }
                questions.get(-1 + currentNum + 1).updateExpression(nextResult);
                log(120, "下面识别（First模式）" + nextResult);

                // 第一次画答案，得保证答案集里有值，所以得在最后
                questions.get(-1 + currentNum).updateExpression(result);
                log(100, "等待绘制（正常识别），题号：" + currentNum);
                doDraw(questions.get(-1 + currentNum));
                waitDraw();
                isFirst = false;
            }

            log(125, questions.toString());
        }
        log(108, "结束：检测到退出了答题页面");
        waitDraw();
    }

    private static void doDraw(Question question) {
        new Thread(() -> {
            question.info = "作答中";
            isDrawing = true;
            mouse.drawSymbols(question.ans, ConfigParser.globalConfig.point5);
            isDrawing = false;
            question.info = "验证中";
        }).start();
    }

    private static void waitDraw() {
        while (isDrawing) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
        }
        if (Mouse.checkHandwriting != null) {
            log(128, "笔刷起点的颜色是" + screen.getColorAt(Mouse.checkHandwriting));
            log(129, "要判断的颜色是" + ConfigParser.globalConfig.point5.color);
            screen.waitUntilPointColorLike(Mouse.checkHandwriting, ConfigParser.globalConfig.point5.color, 1000);
            Mouse.checkHandwriting = null;
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private static void log(int line, String message) {
        if (true) {
            System.out.println("(AutoAnswering.java:" + line + ")\t" + message);
        }
    }
}
