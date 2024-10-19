package com.lhl.xyks.utils.ocr;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图像识别接口
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_2:32
 */
public interface OCR {

    /**
     * 从 BufferedImage 中识别
     *
     * @param image 目标图片
     * @return 识别结果
     */
    String recognize(BufferedImage image);

    /**
     * 从 File 中识别
     *
     * @param file 目标图片
     * @return 识别结构
     */
    String recognize(File file);

    /**
     * 识别结果优化器
     * 把算式尽量往结果上靠
     *
     * @param text 原文本
     * @return 优化文本
     */
    String optimize(String text);
}
