package com.lhl.xyks.test;

import com.lhl.xyks.pk.DoCompareQuestion;
import com.lhl.xyks.pk.DoMathMultiply;
import com.lhl.xyks.pk.MouseDraw;
import com.lhl.xyks.pk.Pause;
import com.lhl.xyks.utils.Mouse;
import org.junit.Test;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_0:43
 */
public class XYKSTest {

    @Test
    public void testJunit() {
        System.out.println("hello xiaoyuan!");
        DoMathMultiply.execute(10);
    }

    @Test
    public void testDraw() {
        MouseDraw.setMoveDelay(1);
        for (int i = 0; i < 10; i++) {
            String randomDouble = getRandomDouble();
            System.out.println("绘制：" + randomDouble);
//            MouseDraw.drawSymbolsAtPos(2560, 656, randomDouble);
            Mouse mouse = Mouse.getMouse();
            mouse.setDrawSymbolWidth(50);
            mouse.drawSymbols(randomDouble);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    public void testThreadDraw() {
        DoCompareQuestion.drawSymbolAtPosBySelfThread(0, 0, '<');
    }

    @Test
    public void testDoCompareQuestion() {
        for (int i = 0; i < 40; i++) {
            System.out.println("开始答题");
            Pause.waitPosColorLike(new Point(2519, 121), Pause.hexToColor("#FEE305"));
            DoCompareQuestion.execute(10);
            System.out.println("答题结束！");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Pause.waitPosColorLike(new Point(2794, 526), Pause.hexToColor("#FFD943"));
            MouseDraw.clickAt(2794, 526);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MouseDraw.clickAt(2923, 932);
            Pause.waitPosColorLike(new Point(2845, 815), Pause.hexToColor("#FBDA4A"));
            System.out.println("准备匹配");
            int randomNumber = 1000 + new Random().nextInt(4001);
            System.out.println("等待" + randomNumber / 1000.0 + "秒后进入匹配（随机0~5）");
            try {
                Thread.sleep(randomNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("进入匹配！");
            MouseDraw.clickAt(2845, 815);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDoMathMultiply() {
        for (int i = 0; i < 1; i++) {
            System.out.println("开始答题");
            Pause.waitPosColorLike(new Point(2519, 121), Pause.hexToColor("#FEE305"));
            DoMathMultiply.execute(10);
            System.out.println("答题结束！");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 开心收下
            Pause.waitPosColorLike(new Point(2833, 853), Pause.hexToColor("#FFD537"));
            MouseDraw.clickAt(2833, 853);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MouseDraw.clickAt(2923, 932);
            Pause.waitPosColorLike(new Point(2924, 1001), Pause.hexToColor("#FFD728"));
            System.out.println("准备匹配");
            int randomNumber = 1000 + new Random().nextInt(4001);
            System.out.println("等待" + randomNumber / 1000.0 + "秒后进入匹配（随机0~5）");
            try {
                Thread.sleep(randomNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("进入匹配！");
            MouseDraw.clickAt(2930, 918);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    // 生成 0 到 500 之间的随机小数，并且小数位数为 0 到 3 位
    public static String getRandomDouble() {
        Random random = new Random();

        // 生成 0 到 500 之间的随机数
        double number = 500 * random.nextDouble();

        // 生成 0 到 3 之间的小数位数
        int decimalPlaces = random.nextInt(4);  // 随机生成 0 到 3

        // 根据小数位数动态生成格式化字符串
        StringBuilder pattern = new StringBuilder("0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                pattern.append("0");
            }
        }

        // 使用 DecimalFormat 格式化数字
        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
        return decimalFormat.format(number);
    }
}
