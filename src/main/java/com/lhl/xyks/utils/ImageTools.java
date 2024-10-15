package com.lhl.xyks.utils;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.convolution.FGaussianConvolve;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
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

    /**
     * 将 PNG 图片缩放到指定倍数
     *
     * @param image 原始图片
     * @param sizeX 水平方向缩放倍数，1.0不缩放
     * @param sizeY 竖直方向缩放倍数，1.0不缩放
     * @return 缩放后的图片
     */
    public static BufferedImage resizePNG(BufferedImage image, double sizeX, double sizeY) {
        // 计算新的宽度和高度
        int newWidth = (int) (image.getWidth() * sizeX);
        int newHeight = (int) (image.getHeight() * sizeY);

        // 创建一个新的空白BufferedImage，用于存储缩放后的图像
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // 获取Graphics2D对象，用于进行图像缩放操作
        Graphics2D g2d = resizedImage.createGraphics();

        // 使用高质量的缩放方式
        g2d.drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose(); // 释放Graphics2D资源

        // 返回缩放后的图片
        return resizedImage;
    }

    /**
     * 锐化 PNG 图片
     *
     * @param image 原始图片
     * @param level 锐化强度，0 表示不锐化，数字越大锐化越强
     * @return 锐化后的图片
     */
    public static BufferedImage sharpenPNG(BufferedImage image, float level) {
        // 确保 sharpnessLevel 合理，避免异常值
        if (level < 0) {
            level = 0;
        }
        // 锐化卷积核 (Sharpen Kernel)，根据 sharpnessLevel 调整中心值
        float[] sharpenKernel = {
                0.0f, -1.0f,  0.0f,
                -1.0f,  4.0f + level, -1.0f,
                0.0f, -1.0f,  0.0f
        };
        // 创建 Kernel 对象
        Kernel kernel = new Kernel(3, 3, sharpenKernel);
        // 使用卷积操作 ConvolveOp 来应用锐化滤镜
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        // 将锐化滤镜应用于图片返回
        return convolveOp.filter(image, null);
    }

    /**
     * 高斯模糊 PNG 图片
     *
     * @param image 原始图片
     * @param level 模糊强度，0 表示不模糊，数字越大模糊越强
     * @return 模糊后的图片
     */
    public static BufferedImage blurPNG(BufferedImage image, float level) {
        // 确保 blurLevel 合理，避免异常值
        if (level < 0) {
            level = 0;
        }

        // 将 BufferedImage 转换为 FImage（灰度图像）
        FImage fImage = ImageUtilities.createFImage(image);

        // 使用 FGaussianConvolve 进行高斯模糊处理，模糊半径由 blurLevel 控制
        FGaussianConvolve gaussianConvolve = new FGaussianConvolve(level);
        fImage.processInplace(gaussianConvolve);

        // 将处理后的 FImage 转换回 BufferedImage 并返回
        return ImageUtilities.createBufferedImage(fImage);
    }
}
