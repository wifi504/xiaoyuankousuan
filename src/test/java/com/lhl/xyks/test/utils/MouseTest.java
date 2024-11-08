package com.lhl.xyks.test.utils;

import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.utils.Mouse;
import org.junit.Test;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_0:53
 */
public class MouseTest {

    @Test
    public void testGetMouse() {
        Mouse mouse1 = Mouse.getMouse();
        Mouse mouse2 = Mouse.getMouse();
        System.out.println(mouse1);
        System.out.println(mouse2);
    }

    @Test
    public void testGetCurrentPoint() {
        Mouse mouse = Mouse.getMouse();
        Point currentPoint = mouse.getCurrentPoint();
        System.out.println(currentPoint);
    }

    @Test
    public void testSmoothMoveFromTo() {
        Mouse mouse = Mouse.getMouse();
        Point point1 = new Point(100, 100);
        Point point2 = new Point(800, 800);
        System.out.println("默认速度移动");
        mouse.smoothMoveFromTo(point1, point2);
        System.out.println(mouse.getCurrentPoint());
        System.out.println("间隔0速度移动");
        mouse.smoothMoveFromTo(point1, point2, 0);
        System.out.println(mouse.getCurrentPoint());
        System.out.println("间隔2速度移动");
        mouse.smoothMoveFromTo(point1, point2, 2);
        System.out.println(mouse.getCurrentPoint());
    }

    @Test
    public void testSmoothMoveTo() {
        Mouse mouse = Mouse.getMouse();
        Point point1 = new Point(666, 666);
        System.out.println("鼠标从当前位置开始移动，间隔default");
        mouse.smoothMoveTo(point1);
        System.out.println(mouse.getCurrentPoint());
        Point point2 = new Point(1000, 20);
        mouse.moveTo(point2);
        System.out.println("鼠标从当前位置开始移动，间隔2");
        mouse.smoothMoveTo(point1, 2);
        System.out.println(mouse.getCurrentPoint());
    }

    @Test
    public void testMousePress() throws InterruptedException {
        Mouse mouse = Mouse.getMouse();
        Point showDesktopBtn = new Point(1919, 1079);
        Point top = new Point(1028, 17);
        Point mid = new Point(1028, 500);
        System.out.println("把IDEA窗口往中间拖");
        mouse.moveTo(top);
        mouse.leftPress();
        mouse.smoothMoveTo(mid);
        mouse.leftRelease();
        Thread.sleep(500);
        System.out.println("显示桌面");
        mouse.leftClickAt(showDesktopBtn);
        Thread.sleep(500);
        System.out.println("在中间那个地方右键");
        mouse.rightClickAt(mid);
    }

    @Test
    public void testDrawSymbol() throws InterruptedException {
        Thread.sleep(1000);
        Mouse mouse = Mouse.getMouse();
        mouse.drawSymbols("18.969994");
    }

    @Test
    public void testDrawSymbols() throws InterruptedException {
        Thread.sleep(2000);
        Mouse mouse = Mouse.getMouse();
        mouse.setDefaultMoveInterval(1);
        mouse.setDrawSymbolWidth(40);

    }
}
