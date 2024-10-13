package com.lhl.xyks.pk;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

/**
 * @author WIFI连接超时
 * @version 1.1
 * Create Time: 2024/10/12_1:17
 */
public class MouseDraw {

    private static Robot robot;

    // 定义移动延迟（速度控制）
    private static int moveDelay = 1;

    // 确保Robot类只实例化一次
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("无法创建Robot实例，程序终止", e);
        }
    }

    // 符号路径存储
    private static final Map<Character, ArrayList<Point>> map = new HashMap<>();

    // 使用 java.awt.Point 代替自定义 Point
    private static final Point homePoint = new Point(0, 0);
    private static Point cachePoint = null;

    static {
        // < 的绘制路径
        ArrayList<Point> posArr0 = new ArrayList<>();
        posArr0.add(new Point(-30, 15));
        posArr0.add(new Point(10, 30));
        map.put('<', posArr0);

        // > 的绘制路径
        ArrayList<Point> posArr1 = new ArrayList<>();
        posArr1.add(new Point(30, 15));
        posArr1.add(new Point(-10, 30));
        map.put('>', posArr1);

        // 0 的绘制路径
        ArrayList<Point> posArr2 = new ArrayList<>();
        posArr2.add(new Point(15, 0));
        posArr2.add(new Point(20, 10));
        posArr2.add(new Point(20, 20));
        posArr2.add(new Point(15, 30));
        posArr2.add(new Point(5, 30));
        posArr2.add(new Point(0, 20));
        posArr2.add(new Point(0, 0));
        map.put('0', posArr2);

        // 1
        ArrayList<Point> posArr3 = new ArrayList<>();
        posArr3.add(new Point(0, 40));
        map.put('1', posArr3);

        ArrayList<Point> posArr4 = new ArrayList<>();
        posArr4.add(new Point(20, 0));
        posArr4.add(new Point(20, 20));
        posArr4.add(new Point(0, 20));
        posArr4.add(new Point(0, 40));
        posArr4.add(new Point(25, 40));
        map.put('2', posArr4);

        ArrayList<Point> posArr5 = new ArrayList<>();
        posArr5.add(new Point(12, 8));
        posArr5.add(new Point(0, 20));
        posArr5.add(new Point(13, 35));
        posArr5.add(new Point(-10, 45));
        map.put('3', posArr5);

        ArrayList<Point> posArr6 = new ArrayList<>();
        posArr6.add(new Point(-20, 30));
        posArr6.add(new Point(10, 30));
        posArr6.add(new Point(0, 15));
        posArr6.add(new Point(0, 50));
        map.put('4', posArr6);

        ArrayList<Point> posArr7 = new ArrayList<>();
        posArr7.add(new Point(25, 0));
        posArr7.add(new Point(0, 0));
        posArr7.add(new Point(0, 25));
        posArr7.add(new Point(25, 25));
        posArr7.add(new Point(25, 50));
        posArr7.add(new Point(-10, 50));
        map.put('5', posArr7);

        ArrayList<Point> posArr8 = new ArrayList<>();
        posArr8.add(new Point(0, 40));
        posArr8.add(new Point(20, 25));
        posArr8.add(new Point(0, 25));
        map.put('6', posArr8);

        ArrayList<Point> posArr9 = new ArrayList<>();
        posArr9.add(new Point(20, 0));
        posArr9.add(new Point(20, 40));
        map.put('7', posArr9);

        ArrayList<Point> posArr10 = new ArrayList<>();
        posArr10.add(new Point(-20, 0));
        posArr10.add(new Point(0, 40));
        posArr10.add(new Point(-25, 38));
        posArr10.add(new Point(0, 0));
        map.put('8', posArr10);

        ArrayList<Point> posArr11 = new ArrayList<>();
        posArr11.add(new Point(-30, 20));
        posArr11.add(new Point(0, 20));
        posArr11.add(new Point(0, 80));
        map.put('9', posArr11);

        ArrayList<Point> posArr12 = new ArrayList<>();
        posArr12.add(new Point(5, 5));
        map.put('.', posArr12);
        map.put('-', posArr12);
    }

    /**
     * 绘制一排符号到指定位置
     *
     * @param x       起始点 x 坐标
     * @param y       起始点 y 坐标
     * @param symbols 要绘制的符号
     */
    public static void drawSymbolsAtPos(int x, int y, String symbols) {
        for (int i = 0; i < symbols.length(); i++) {
            if ('.' == symbols.charAt(i)) {
                drawSymbolAtPos(x + (55 * i), y + 40, symbols.charAt(i));
            } else {
                drawSymbolAtPos(x + (55 * i), y, symbols.charAt(i));
            }
        }
    }

    /**
     * 绘制符号到指定位置
     *
     * @param x      起始点 x 坐标
     * @param y      起始点 y 坐标
     * @param symbol 要绘制的符号
     */
    public static void drawSymbolAtPos(int x, int y, char symbol) {
        // 检查符号路径是否定义
        ArrayList<Point> path = map.get(symbol);
        if (path == null) {
            throw new IllegalArgumentException("符号未定义: " + symbol);
        }

        // 移动鼠标到指定起始点并按住鼠标
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 绘制路径
        path.forEach(point -> smoothMouseMoveToByCache(point, x, y));

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 释放鼠标
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        smoothMouseMoveToByCache(homePoint, x, y);

        // 清除缓存
        cachePoint = null;
    }

    /**
     * 模拟鼠标点击指定的屏幕坐标
     *
     * @param x X 坐标
     * @param y Y 坐标
     */
    public static void clickAt(int x, int y) {
        // 将鼠标移动到指定位置
        robot.mouseMove(x, y);

        // 模拟鼠标按下（左键）
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // 模拟鼠标松开（左键）
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * 根据缓存的最后位置移动鼠标
     *
     * @param p    目标点
     * @param absX 起始绝对 X 坐标
     * @param absY 起始绝对 Y 坐标
     */
    private static void smoothMouseMoveToByCache(Point p, int absX, int absY) {
        if (cachePoint == null) cachePoint = homePoint;
        smoothMouseMove(cachePoint, p, absX, absY);
        cachePoint = p;
    }

    /**
     * 平滑移动鼠标，从起点到终点，每次移动1像素
     *
     * @param from 起始点
     * @param to   目标点
     * @param absX 起始绝对 X 坐标
     * @param absY 起始绝对 Y 坐标
     */
    private static void smoothMouseMove(Point from, Point to, int absX, int absY) {
        // 计算直线距离
        double distance = from.distance(to);
        if (distance == 0) return; // 如果起点和终点重合，不需要移动

        // 计算每次移动的单位向量
        double dirX = (to.x - from.x) / distance;
        double dirY = (to.y - from.y) / distance;

        // 当前鼠标位置
        double currentX = from.x;
        double currentY = from.y;

        // 移动鼠标直到到达目标点
        while (to.distance(currentX, currentY) >= 0.5) {
            currentX += dirX;
            currentY += dirY;

            // 将鼠标移动到新的位置
            robot.mouseMove((int) round(currentX) + absX, (int) round(currentY) + absY);

            // 休眠，控制移动速度
            try {
                Thread.sleep(moveDelay);  // 使用动态延迟控制速度
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态设置移动延迟时间，控制鼠标移动速度
     *
     * @param delay 延迟时间（毫秒）
     */
    public static void setMoveDelay(int delay) {
        moveDelay = delay;
    }

    public static void main(String[] args) {
        map.get('-').forEach(point -> smoothMouseMoveToByCache(point, 385, 521));
    }
}
