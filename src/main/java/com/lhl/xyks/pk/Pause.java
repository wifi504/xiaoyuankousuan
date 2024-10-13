package com.lhl.xyks.pk;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_4:25
 */
public class Pause {

    // 创建一个 Robot 实例用于获取屏幕颜色
    private static final Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("无法创建 Robot 实例", e);
        }
    }

    /**
     * 判断指定点颜色是否为
     *
     * @param point 点
     * @param color 颜色
     * @return boolean
     */
    public static boolean isPosColorLike(Point point, Color color) {
        int tolerance = 10; // 定义颜色接近的容差值
        Color currentColor = robot.getPixelColor(point.x, point.y);
        return isColorApproximate(currentColor, color, tolerance);
    }

    public static void waitPosColorLike(Point point, Color color) {
        waitPosColorLike(point, color, Long.MAX_VALUE);
    }

    /**
     * 阻塞线程直到指定点的颜色与传入的颜色接近
     *
     * @param point   指定的屏幕坐标点
     * @param color   目标颜色
     * @param timeout 超时等待毫秒
     */
    public static void waitPosColorLike(Point point, Color color, long timeout) {
        int tolerance = 10; // 定义颜色接近的容差值
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout) {
            // 获取当前点的像素颜色
            Color currentColor = robot.getPixelColor(point.x, point.y);
//            System.out.println("current:" + currentColor);
//            System.out.println("target:" + color);

            // 如果颜色接近，则退出循环
            if (isColorApproximate(currentColor, color, tolerance)) {
                break;
            }

            // 暂停一段时间，避免 CPU 过度占用
            try {
                TimeUnit.MILLISECONDS.sleep(10); // 每次检查间隔 100 毫秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                throw new RuntimeException("线程中断", e);
            }
        }
    }

    /**
     * 比较两个颜色是否接近，使用容差值进行 RGB 比较
     *
     * @param c1        第一个颜色
     * @param c2        第二个颜色（目标颜色）
     * @param tolerance 容差值
     * @return true 如果颜色接近；否则返回 false
     */
    private static boolean isColorApproximate(Color c1, Color c2, int tolerance) {
        int redDiff = Math.abs(c1.getRed() - c2.getRed());
        int greenDiff = Math.abs(c1.getGreen() - c2.getGreen());
        int blueDiff = Math.abs(c1.getBlue() - c2.getBlue());

        return redDiff <= tolerance && greenDiff <= tolerance && blueDiff <= tolerance;
    }

    /**
     * 将十六进制颜色字符串转换为 Color 对象
     *
     * @param hex 十六进制颜色字符串（格式: #RRGGBB 或 RRGGBB）
     * @return 对应的 Color 对象
     */
    public static Color hexToColor(String hex) {
        // 去除 # 前缀（如果有）
        hex = hex.startsWith("#") ? hex.substring(1) : hex;

        // 将十六进制字符串转换为整数，并创建 Color 对象
        int rgb = Integer.parseInt(hex, 16);

        return new Color(rgb);
    }

    public static void main(String[] args) {
        // 测试：阻塞直到屏幕上 (100, 100) 点的颜色接近红色
        Point point = new Point(305, 85);
        Color targetColor = hexToColor("#fb0500");

        System.out.println("等待屏幕点颜色变为红色...");
        waitPosColorLike(point, targetColor);
        System.out.println("颜色匹配，继续执行程序...");
    }
}
