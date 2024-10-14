package com.lhl.xyks.utils;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.pojo.Color;
import com.lhl.xyks.pojo.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 屏幕工具类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_14:37
 */
public class Screen {

    // 单例对象
    private static Screen screen = null;
    private static Robot robot = null;

    // 默认更新检测的时间间隔（ms）
    private long defaultUpdateInterval = 10;


    /**
     * 构造方法私有化
     */
    private Screen() {
        // 初始化 Robot 实例
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("无法创建Robot实例，程序终止", e);
        }
    }


    // ------------ 静态方法 ------------ //

    /**
     * 返回一个单例的 Screen 对象，可以执行一系列需要屏幕交互的工作
     *
     * @return 单例的 Screen 对象
     */
    public static Screen getScreen() {
        if (screen != null) return screen;
        screen = new Screen();
        return screen;
    }


    // ------------ 屏幕信息 ------------ //

    /**
     * 获取指定区域的屏幕截图
     *
     * @param area 区域
     * @return 截取到的图片
     */
    public BufferedImage capture(Area area) {
        Rectangle screenRect = new Rectangle(area.x, area.y, area.width, area.height);
        return robot.createScreenCapture(screenRect);
    }

    /**
     * 获取指定区域的屏幕截图，并保存为文件
     *
     * @param file 目标图片文件
     */
    public void captureToFile(Area area, File file) {
        BufferedImage image = capture(area);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            System.out.println("无法保存图片：" + file.getName() + ", err: " + e.getMessage());
        }
    }

    /**
     * 获取指定点的颜色
     *
     * @param point 指定点
     * @return Color
     */
    public Color getColorAt(Point point) {
        java.awt.Color pxColor = robot.getPixelColor(point.x, point.y);
        return new Color(pxColor.getRGB());
    }


    // ------------ 线程管理 ------------ //


    /**
     * 设置默认更新检测的时间间隔（ms）
     *
     * @param interval 间隔（ms）
     */
    public void setDefaultUpdateInterval(long interval) {
        defaultUpdateInterval = interval;
    }

    /**
     * 指定点的颜色跟这个颜色相似吗？
     *
     * @param point 指定点
     * @param color 颜色
     * @return boolean
     */
    public boolean isPointColorLike(Point point, Color color) {
        return getColorAt(point).like(color);
    }


    /**
     * 永久阻塞当前线程，直到指定点颜色与该颜色相似
     *
     * @param point 指定点
     * @param color 匹配颜色
     */
    public void waitUntilPointColorLike(Point point, Color color) {
        waitUntilPointColorLike(point, color, Long.MAX_VALUE);
    }

    /**
     * 超时阻塞当前线程，直到指定点颜色与该颜色相似
     * 如果达到超时时间颜色也没有相似，取消阻塞
     *
     * @param point   指定点
     * @param color   匹配颜色
     * @param timeout 超时等待时间
     */
    public void waitUntilPointColorLike(Point point, Color color, long timeout) {
        long start = System.currentTimeMillis();
        while (!isPointColorLike(point, color)) {
            if (System.currentTimeMillis() - start > timeout) return;
            try {
                Thread.sleep(defaultUpdateInterval);
            } catch (InterruptedException ignore) {
            }
        }
    }

    /**
     * 永久阻塞当前线程，直到指定点颜色与该颜色不再相似
     *
     * @param point 指定点
     * @param color 匹配颜色
     */
    public void waitWhilePointColorLike(Point point, Color color) {
        waitWhilePointColorLike(point, color, Long.MAX_VALUE);
    }

    /**
     * 超时阻塞当前线程，直到指定点颜色与该颜色不再相似
     * 如果达到超时时间颜色也仍然相似，取消阻塞
     *
     * @param point   指定点
     * @param color   匹配颜色
     * @param timeout 超时等待时间
     */
    public void waitWhilePointColorLike(Point point, Color color, long timeout) {
        long start = System.currentTimeMillis();
        while (isPointColorLike(point, color)) {
            if (System.currentTimeMillis() - start > timeout) return;
            try {
                Thread.sleep(defaultUpdateInterval);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
