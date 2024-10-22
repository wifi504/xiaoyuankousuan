package com.lhl.xyks.utils;

import com.lhl.xyks.pojo.Point;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.round;

/**
 * 鼠标工具类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_0:53
 */
public class Mouse {

    // 单例对象
    private static Mouse mouse = null;
    private static Robot robot = null;

    // 鼠标点击状态
    private static boolean isLeftPress = false;
    private static boolean isRightPress = false;

    // 默认的鼠标进行每单位移动时的时间间隔（ms）
    private long defaultMoveInterval = 1;

    // 连续绘制符号时，每个符号间的时间间隔（ms）
    private long drawSymbolInterval = 5;

    // 关于落笔与停笔时间：
    // 防止因为鼠标按下瞬间开始移动和移动到终点瞬间就松开时，
    // 引起的因画板程序反应不过来导致的丢失轨迹的问题
    private long startDrawDuration = 10; // 绘制单个符号的落笔时间（ms）
    private long endDrawDuration = 10; // 绘制单个符号的停笔时间（ms）

    // 绘制单个符号的宽度（px）
    private int drawSymbolWidth = 50;

    // 存放每个绘制符号的点轨迹，
    // 只有实心点才会绘制出痕迹，
    // 空心点会让鼠标修改落笔位置，
    // 这个Map会在初始化Mouse实例时根据"symbol-mapper.json"自动生成，
    // 符号映射文件中的坐标是相对坐标，在一个宽100高200的范围内，依次存放每个笔画起止点
    private static HashMap<Character, ArrayList<Point>> symbolMap = null;

    // 对外提供一个笔迹检查点，位置是第一次开始绘制时，两个路径点的中点
    public static Point checkHandwriting = null;

    /**
     * 构造方法私有化
     */
    private Mouse() {
        // 初始化 Robot 实例
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("无法创建Robot实例，程序终止", e);
        }
        // 初始化 symbolMap 符号映射
        try {
            symbolMap = ConfigParser.loadSymbolMap();
        } catch (IOException e) {
            throw new RuntimeException("无法初始化绘制符号映射，程序终止", e);
        }
    }


    // ------------ 静态方法 ------------ //

    /**
     * 返回一个单例的 Mouse 对象，可以执行一系列鼠标动作
     *
     * @return 单例的 Mouse 对象
     */
    public static Mouse getMouse() {
        if (mouse != null) return mouse;
        mouse = new Mouse();
        return mouse;
    }


    // ------------ 移动相关 ------------ //

    /**
     * 返回鼠标当前所在的绝对坐标点
     *
     * @return Point 鼠标的绝对位置
     */
    public Point getCurrentPoint() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        java.awt.Point location = pointerInfo.getLocation();
        return new Point(location.x, location.y);
    }

    /**
     * 设置默认鼠标单位移动间隔（ms）
     *
     * @param interval 间隔（ms）
     */
    public void setDefaultMoveInterval(long interval) {
        defaultMoveInterval = interval;
    }

    /**
     * 鼠标立刻移动到点point
     *
     * @param point 目标点
     */
    public void moveTo(Point point) {
        robot.mouseMove(point.x, point.y);
    }

    /**
     * 鼠标从当前位置以默认间隔平滑移动到点point
     *
     * @param point 目标点
     */
    public void smoothMoveTo(Point point) {
        smoothMoveTo(point, defaultMoveInterval);
    }

    /**
     * 鼠标从当前位置以指定间隔平滑移动到点point
     *
     * @param point    目标点
     * @param interval 间隔
     */
    public void smoothMoveTo(Point point, long interval) {
        smoothMoveFromTo(getCurrentPoint(), point, interval);
    }

    /**
     * 从指定起点到指定终点以默认间隔平滑移动鼠标
     *
     * @param from 起始点
     * @param to   目标点
     */
    public void smoothMoveFromTo(Point from, Point to) {
        smoothMoveFromTo(from, to, defaultMoveInterval);
    }

    /**
     * 从指定起点到指定终点以指定间隔平滑移动鼠标
     *
     * @param from     起始点
     * @param to       目标点
     * @param interval 间隔
     */
    public void smoothMoveFromTo(Point from, Point to, long interval) {
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
            robot.mouseMove((int) round(currentX), (int) round(currentY));

            // 鼠标移动间隔
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignore) {
            }
        }
    }


    // ------------ 按键相关 ------------ //

    /**
     * 鼠标左键按下
     */
    public void leftPress() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        isLeftPress = true;
    }

    /**
     * 鼠标左键释放
     */
    public void leftRelease() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        isLeftPress = false;
    }

    /**
     * 鼠标右键按下
     */
    public void rightPress() {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        isRightPress = true;
    }

    /**
     * 鼠标右键释放
     */
    public void rightRelease() {
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        isRightPress = false;
    }

    /**
     * 单击鼠标左键
     */
    public void leftClick() {
        leftPress();
        leftRelease();
    }

    /**
     * 在指定点单击鼠标左键
     */
    public void leftClickAt(Point point) {
        moveTo(point);
        leftClick();
    }

    /**
     * 单击鼠标右键
     */
    public void rightClick() {
        rightPress();
        rightRelease();
    }

    /**
     * 在指定点单击鼠标右键
     */
    public void rightClickAt(Point point) {
        moveTo(point);
        rightClick();
    }


    // ------------ 符号绘制 ------------ //

    /**
     * 设置每个符号间的时间间隔
     *
     * @param interval 间隔
     */
    public void setDrawSymbolInterval(long interval) {
        drawSymbolInterval = interval;
    }

    /**
     * 设置落笔时间
     *
     * @param duration 落笔时间
     */
    public void setStartDrawDuration(long duration) {
        startDrawDuration = duration;
    }

    /**
     * 设置停笔时间
     *
     * @param duration 停笔时间
     */
    public void setEndDrawDuration(long duration) {
        endDrawDuration = duration;
    }

    /**
     * 设置绘制单个符号的宽度（px）
     *
     * @param width 宽度（px）
     */
    public void setDrawSymbolWidth(int width) {
        drawSymbolWidth = width;
    }

    /**
     * 在当前鼠标位置绘制指定的符号
     *
     * @param symbol 符号
     */
    public void drawSymbol(char symbol) {
        drawSymbol(symbol, getCurrentPoint());
    }

    /**
     * 在指定鼠标位置（point）绘制指定的符号
     *
     * @param symbol 符号
     * @param point  指定鼠标位置
     */
    public void drawSymbol(char symbol, Point point) {
        // 鼠标移到指定位置
        moveTo(point);
        // 鼠标绘制路径
        ArrayList<Point> points = symbolMap.get(symbol);
        points.forEach(p0 -> {
            Point p = p0.resize(drawSymbolWidth / 100.0);
            // 判断这个点是不是实心路径
            if (p.isSolid) {
                // 绘制过去
                if (!isLeftPress) {
                    leftPress();
                    try {
                        Thread.sleep(startDrawDuration);
                    } catch (InterruptedException ignore) {
                    }
                }

                if (checkHandwriting == null) {
                    // 对外提供的检查点
                    Point startPoint = getCurrentPoint();
                    Point endPoint = point.overlay(p);
                    checkHandwriting = new Point((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);
                }

                smoothMoveTo(point.overlay(p));
            } else {
                // 移动过去
                if (isLeftPress) {
                    try {
                        Thread.sleep(endDrawDuration);
                    } catch (InterruptedException ignore) {
                    }
                    leftRelease();
                }
                moveTo(point.overlay(p));
            }
        });
        // 完成绘制
        if (isLeftPress) {
            try {
                Thread.sleep(endDrawDuration);
            } catch (InterruptedException ignore) {
            }
            leftRelease();
        }
    }

    /**
     * 在当前鼠标位置开始横向绘制一系列符号
     *
     * @param symbols 符号
     */
    public void drawSymbols(String symbols) {
        drawSymbols(symbols, getCurrentPoint());
    }

    /**
     * 在指定鼠标位置（p点）开始横向绘制一系列符号
     *
     * @param symbols 符号
     * @param point   指定鼠标位置
     */
    public void drawSymbols(String symbols, Point point) {
        // 递增向量
        Point p = new Point(drawSymbolWidth, 0);
        for (int i = 0; i < symbols.length(); i++) {
            char symbol = symbols.charAt(i);
            drawSymbol(symbol, point);
            point = point.overlay(p);
            try {
                Thread.sleep(drawSymbolInterval);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
