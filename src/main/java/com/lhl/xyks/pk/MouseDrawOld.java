package com.lhl.xyks.pk;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_1:17
 */
public class MouseDrawOld {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static final HashMap<Character, ArrayList<Point>> map = new HashMap<>();

    private static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private static final Point homePoint = new Point(0, 0);
    private static Point cachePoint = null;

    static {
        // < 的绘制路径
        ArrayList<Point> posArr0 = new ArrayList<>();
        posArr0.add(new Point(-20, -20));
        posArr0.add(new Point(0, -40));
        map.put('<', posArr0);

        // > 的绘制路径
        ArrayList<Point> posArr1 = new ArrayList<>();
        posArr1.add(new Point(20, -20));
        posArr1.add(new Point(0, -40));
        map.put('>', posArr1);


    }

    // 根据指定的坐标点绘制指定符号
    public static void drawSymbolAtPos(int x, int y, char symbol) {
        // 到指定点按住
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        // 绘制路径
        map.get(symbol).forEach(point -> smoothMouseMoveToByCache(point, x, y));
        // 释放
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        // 复位
        cachePoint = null;
    }

    private static void smoothMouseMoveToByCache(Point p, int absX, int absY) {
        if (cachePoint == null) cachePoint = homePoint;
        smoothMouseMove(cachePoint, p, absX, absY);
        cachePoint = p;
    }

    private static void smoothMouseMove(Point from, Point to, int absX, int absY) {
        // 计算直线距离
        double distance = sqrt(pow(to.getX() - from.getX(), 2) + pow(to.getY() - from.getY(), 2));
        // 计算每次移动的单位向量（dirX, dirY）
        double dirX = (to.getX() - from.getX()) / distance;
        double dirY = (to.getY() - from.getY()) / distance;
        // 当前鼠标位置
        double currentX = from.getX();
        double currentY = from.getY();
        // 移动鼠标直到到达目标点
        while (round(currentX) != to.getX() || round(currentY) != to.getY()) {
            // 更新当前的鼠标位置
            currentX += dirX;
            currentY += dirY;
            // 将鼠标移动到新的位置
            robot.mouseMove((int) round(currentX) + absX, (int) round(currentY) + absY);
            // 休眠，控制移动速度
            try {
                Thread.sleep(5);  // 调整此值来控制移动的流畅性
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws AWTException {

        // 移动鼠标到起始位置 (起点 x, 起点 y)
        robot.mouseMove(1160, 1067);

        // 按住鼠标左键
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        // 模拟拖动 - 移动鼠标到目标位置 (终点 x, 终点 y)
        robot.mouseMove(1543, 1067);

        // 松开鼠标左键
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        System.out.println("鼠标拖动完成");
    }
}
