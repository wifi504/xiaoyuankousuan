package com.lhl.xyks.pk;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 完成比较大小题目
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_3:18
 */
public class DoCompareQuestion {

    public static int ocrOldL = 0;
    public static int ocrOldR = 0;

    public static void execute(int qSize) {
        int i = 0;
        while (!Pause.isPosColorLike(new Point(2592, 307), Pause.hexToColor("#FFFFFF"))) {
            Pause.waitPosColorLike(absPoint, Pause.hexToColor("#E7EFFE"), 1000);
            if (i == 0 || (ocrOldL == leftNum && ocrOldR == rightNum)) {
                work(currentLeft, currentRight);
            } else {
                work(nextLeft, nextRight);
            }
            if (i++ > qSize + 3) break;
        }
    }

    // 答题区中心点
    static final Point absPoint = new Point(2751, 696);

    // 题目划区
    // 左上 右上 -> 当前题目
    static Area currentLeft = new Area(2528, 266, 189, 95);
    static Area currentRight = new Area(2783, 266, 187, 90);
    // 左下 右下 -> 下一题目
    static Area nextLeft = new Area(2577, 381, 154, 67);
    static Area nextRight = new Area(2769, 382, 140, 58);

    // 判题的两个数
    static int leftNum = 0;
    static int rightNum = 0;

    /*
        算法逻辑：
        针对比较大小类型题目，识别左上、右上、左下、右下四个区域
        第一题的解题识别左上和右上，其余全部识别下面一栏的（这个思路是用鼠标绘制的时间抵消OCR的时间）

        尽管OCR只识别两个数字，我们仍然开两个线程同时识别
    */


    public static void work(Area imgL, Area imgR) {
        long start = System.currentTimeMillis();
        // 截获指定区域
        BufferedImage il = ScreenCapture.getBufferedImageByArea(imgL);
        BufferedImage ir = ScreenCapture.getBufferedImageByArea(imgR);
        // OCR识别
        ocrTwoImg(il, ir);
        // 判题
        boolean isLargeThan = leftNum > rightNum;
        // 答题
        if (isLargeThan) {
            MouseDraw.drawSymbolAtPos(absPoint.x, absPoint.y, '>');
        } else {
            MouseDraw.drawSymbolAtPos(absPoint.x, absPoint.y, '<');
        }
        long end = System.currentTimeMillis();
        // 输出日志
        System.out.println("识别到题目：" + leftNum + " ? " + rightNum
                + "，判题结果：" + (isLargeThan ? ">" : "<")
                + "，耗时：" + (end - start) + "ms");
    }

    // 绘制操作单独作为线程执行
    public static void drawSymbolAtPosBySelfThread(int x, int y, char symbol) {
        new Thread(() -> MouseDraw.drawSymbolAtPos(x, y, symbol)).start();
    }

    // 双线程并发 OCR 识别题目
    public static void ocrTwoImg(BufferedImage l, BufferedImage r) {
        Thread t1 = new Thread(() -> {
            try {
                leftNum = Integer.parseInt(OCR.doOCRFromBufferedImage(l));
            } catch (NumberFormatException ignore) {
                leftNum = -1;
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                rightNum = Integer.parseInt(OCR.doOCRFromBufferedImage(r));
            } catch (NumberFormatException e) {
                rightNum = -1;
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
