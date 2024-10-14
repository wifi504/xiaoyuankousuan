package com.lhl.xyks.test.utils;

import com.lhl.xyks.pojo.Area;
import com.lhl.xyks.pojo.Color;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.utils.Mouse;
import com.lhl.xyks.utils.Screen;
import org.junit.Test;

import java.io.File;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_14:40
 */
public class ScreenTest {
    @Test
    public void testCapture() {
        // 测试截图功能
        Screen screen = Screen.getScreen();
        Mouse mouse = Mouse.getMouse();
        Point point = mouse.getCurrentPoint();
        Area fullScreen = new Area(0, 0, 1920, 1080);
        Area mouseArea = new Area(point.x - 150, point.y - 150, 300, 300);
        screen.captureToFile(fullScreen, new File("captured_full.png"));
        screen.captureToFile(mouseArea, new File("captured_mouse.png"));
    }

    @Test
    public void testGetColor() {
        // 测试坐标颜色功能
        Screen screen = Screen.getScreen();
        Mouse mouse = Mouse.getMouse();
        Point point = mouse.getCurrentPoint();
        System.out.println(point + "; " + screen.getColorAt(point));
    }

    @Test
    public void testThreadControl() throws InterruptedException {
        // 测试线程管理
        Screen screen = Screen.getScreen();
        // 当右侧打开了Windows资源管理器（找到了白色），就输出
        Color white = Color.hexToColor("#ffffff");
        screen.waitUntilPointColorLike(new Point(1626, 700), white);
        System.out.println("Windows资源管理器打开了...");
        // 当鼠标移动到红色，就退出
        Color color = Color.hexToColor("#fb0200");
        Mouse mouse = Mouse.getMouse();
        while (!screen.isPointColorLike(mouse.getCurrentPoint(), color)){
            Thread.sleep(10);
        }
        System.out.println("鼠标移动到红色了...");
    }
}
