package com.lhl.xyks.utils;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图像处理工具类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_20:12
 */
public class ImageTools {

    /**
     * 工具类，构造方法私有化
     */
    private ImageTools() {
    }

    /**
     * 从文件获取图片缓存
     *
     * @param file 图片文件
     * @return BufferedImage
     * @throws IOException IOException
     */
    public static BufferedImage readImageFromFile(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * 把图片存为文件
     *
     * @param image 图片缓存
     * @param file  目标文件
     * @throws IOException IOException
     */
    public static void saveImageToFile(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }

    /**
     * PNG 图片二值化
     *
     * @param png       图片缓存
     * @param threshold 阈值(0-1)
     * @return 二值化后的图片缓存
     */
    public static BufferedImage pngToBinary(BufferedImage png, float threshold) {
        // 将输入图像转换为 FImage (浮点灰度图像)
        FImage fImage = ImageUtilities.createFImage(png);
        // 根据 threshold 进行二值化处理
        FImage binaryImage = fImage.threshold(threshold);
        return ImageUtilities.createBufferedImage(binaryImage);
    }
}
