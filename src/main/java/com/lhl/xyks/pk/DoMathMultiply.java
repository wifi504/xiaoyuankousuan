package com.lhl.xyks.pk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/12_16:57
 */
public class DoMathMultiply {

    public static String ocrOld = "";
    public static String ocrNew = "";

    public static void execute(int qSize) {
        int i = 0;
        // 如果蓝色没变白
        while (!Pause.isPosColorLike(new Point(2614, 144), Pause.hexToColor("#FFFFFF"))) {
            Pause.waitPosColorLike(absPoint, Pause.hexToColor("#E7EFFE"), 1000);
            if (i == 0 || ocrOld.equals(ocrNew)) {
                work(current);
            } else {
                work(next);
            }
            if (i++ > qSize + 10) break;
        }
    }


    // 答题区中心点
    static final Point absPoint = new Point(2560, 656);

    // 题目划区
    // 当前题目
    static Area current = new Area(2540, 320, 423, 115);
    // 下一题目
    static Area next = new Area(2563, 443, 360, 79);

    // 判题的计算式
    static String formula = "";

    public static void work(Area img) {
        long start = System.currentTimeMillis();
        System.out.println("====> 开始识别题目");
        // 截获指定区域
        BufferedImage bi = ScreenCapture.getBufferedImageByArea(img);
        // OCR识别
        String ocr = OCR.doOCRFromBufferedImage(bi);
        ocrOld = ocrNew;
        ocrNew = ocr;
        formula = ocr.trim();
        System.out.println("  - 识别到题目：" + formula);
        // 判题
        String[] split = formula.split("x");
        BigDecimal left;
        try {
            left = new BigDecimal(split[0]);
        } catch (Exception ignore) {
            left = new BigDecimal("-1");
        }
        BigDecimal right;
        try {
            right = new BigDecimal(split[1]);
        } catch (Exception ignore) {
            right = new BigDecimal("-1");
        }

        String answer = left.multiply(right).toString();
        if (answer.contains(".")) {
            double num = Double.parseDouble(answer);
            if ((int) num == num) {
                answer = String.valueOf((int) num);
            } else {
                answer = String.valueOf(num);
            }
        }
        System.out.println("  - 判题结果：" + answer);
        // 答题
        MouseDraw.drawSymbolsAtPos(absPoint.x, absPoint.y, answer);
        long end = System.currentTimeMillis();

        System.out.println("  - 作答耗时：" + (end - start) + "ms");
    }
}
