package com.lhl.xyks.pk;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_0:49
 */
public class ScreenCapture {

    public static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // 截取指定区域屏幕，得到 BufferedImage
    public static BufferedImage getBufferedImageByArea(Area area) {
        Rectangle screenRect = new Rectangle(area.x, area.y, area.width, area.height);
        return robot.createScreenCapture(screenRect);
    }

    public static void main(String[] args) {

        try {
            // 定义截取区域 (左上角x坐标, 左上角y坐标, 宽度, 高度)
//            Rectangle screenRect = new Rectangle(1438, 94, 414, 920);
            Rectangle screenRect = new Rectangle(2796, 382, 140, 58);

            // 截取指定区域的屏幕
            BufferedImage screenFullImage = getBufferedImageByArea(DoMathMultiply.current);

            // 保存为文件
            File imageFile = new File("captured_formula.png");
            ImageIO.write(screenFullImage, "png", imageFile);

            System.out.println("屏幕截图保存成功: " + imageFile.getAbsolutePath());
        } catch (Exception ex) {
            System.err.println("截屏失败: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
