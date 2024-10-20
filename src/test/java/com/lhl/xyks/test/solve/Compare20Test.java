package com.lhl.xyks.test.solve;

import com.lhl.xyks.solve.ExpressionOptimizer;
import com.lhl.xyks.ui.GlobalKeyListener;
import com.lhl.xyks.ui.GlobalKeyListener.Key;
import com.lhl.xyks.ui.ScreenSelector;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;
import com.lhl.xyks.utils.ocr.OCR;
import com.lhl.xyks.utils.ocr.OCRService;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/21_2:03
 */
public class Compare20Test {
    @Test
    public void test() throws InterruptedException {
        GlobalKeyListener.initialize();
        ScreenSelector selector = new ScreenSelector();
        Screen screen = Screen.getScreen();
        Mouse mouse = Mouse.getMouse();
        System.out.println("返回键");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Point back = selector.getSelectorPoint();
        System.out.println("当前题目区域");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Compare20Worker.currentArea = selector.getSelectorArea();
        System.out.println("下一题目区域");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Compare20Worker.nextArea = selector.getSelectorArea();
        System.out.println("画板");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Compare20Worker.panel = selector.getSelectorPoint();
        System.out.println("结算中间按钮");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Point jiesuan = selector.getSelectorPoint();
        System.out.println("继续按钮");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Point jixu = selector.getSelectorPoint();
        System.out.println("匹配按钮");
        GlobalKeyListener.waitKeyTrigger(Key.CTRL);
        Point pipei = selector.getSelectorPoint();


        for (int i = 0; i < 1; i++) {
            System.out.println("开始答题");
            screen.waitUntilPointColorLike(back, back.color);
            Compare20Worker.execute(10);
            System.out.println("答题结束！");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            screen.waitUntilPointColorLike(jiesuan, jiesuan.color);
            mouse.leftClickAt(jiesuan);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mouse.leftClickAt(jixu);
            System.out.println("准备匹配");
            int randomNumber = 1000 + new Random().nextInt(4001);
            System.out.println("等待" + randomNumber / 1000.0 + "秒后进入匹配（随机0~5）");
            try {
                Thread.sleep(randomNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("进入匹配！");
            mouse.leftClickAt(pipei);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        GlobalKeyListener.shutdown();
    }
}

class Compare20Worker {

    public static String ocrOld = "";

    public static Screen screen = Screen.getScreen();

    public static void execute(int qSize) throws InterruptedException {
        int i = 0;
        while (screen.isPointColorLike(panel, panel.color)) {
            if (i == 0 || ocrOld.equals(result)) {
                work(currentArea);
            } else {
                work(nextArea);
            }
            if (i++ > qSize + 3) break;
        }
    }

    // 答题区画板
    static Point panel = null;

    // 题目划区
    // 当前题目
    static Area currentArea = null;
    // 下一题目
    static Area nextArea = null;


    /*
        算法逻辑：
        针对比较大小类型题目，识别左上、右上、左下、右下四个区域
        第一题的解题识别左上和右上，其余全部识别下面一栏的（这个思路是用鼠标绘制的时间抵消OCR的时间）

        尽管OCR只识别两个数字，我们仍然开两个线程同时识别
    */

    static String xyks;
    static String eng;
    static String paddle;
    static String result;

    public static void work(Area area) throws InterruptedException {
        long start = System.currentTimeMillis();
        // 截获指定区域
        BufferedImage capture = Screen.getScreen().capture(area);
        // OCR识别
        OCR xyksOCR = OCRService.getModel("xyks");
        OCR engOCR = OCRService.getModel("eng");
        OCR paddleOCR = OCRService.getModel("paddle");

        BufferedImage resizePNG = ImageTools.resizePNG(capture, 1.25, 1);
        BufferedImage blurPNG = ImageTools.blurPNG(capture, 1.5f);


        Thread t1 = new Thread(() -> {
            xyks = xyksOCR.optimize(xyksOCR.recognize(resizePNG));
        });
        Thread t2 = new Thread(() -> {
            eng = engOCR.optimize(engOCR.recognize(blurPNG));
        });
        Thread t3 = new Thread(() -> {
            paddle = paddleOCR.optimize(paddleOCR.recognize(capture));
        });
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        result = ExpressionOptimizer.optimizeCompare20(xyks, paddle, eng);

        String[] split = result.split(":");
        // 判题
        String[] res = split[0].split("\\?");
        boolean isLargeThan = Integer.parseInt(res[0]) > Integer.parseInt(res[1]);
        // 答题
        if (isLargeThan) {
            Mouse.getMouse().drawSymbol('>', panel);
        } else {
            Mouse.getMouse().drawSymbol('<', panel);
        }
        long end = System.currentTimeMillis();
        // 输出日志
        System.out.println("识别到题目：" + split[0]
                + "，判题结果：" + (isLargeThan ? ">" : "<")
                + "，耗时：" + (end - start) + "ms" + "来自模型：" + split[1]);
    }


}
