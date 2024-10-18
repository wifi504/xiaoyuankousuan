package com.lhl.xyks.utils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 屏幕识别工具类
 *
 * @author WIFI连接超时
 * @version 2.0
 * Create Time: 2024/10/14_16:04
 */
public class OCR {

    private final Tesseract tesseract = new Tesseract();

    /**
     * 构造方法，每个 OCR 对应一个 Tesseract 对象
     */
    public OCR() {
        File tessdata = new File("tessdata");
        String dataPath = tessdata.getAbsolutePath().replaceAll("\\\\", "/");
        tesseract.setDatapath(dataPath);
        // 设置使用的语言模型
        tesseract.setLanguage("xyks2");  // 修改为你自定义的语言

        // 设置页面分割模式 (PSM)
        tesseract.setPageSegMode(6);  // 假设输入为单块文本

        tesseract.setVariable("tessedit_write_unlv", "0");  // 仅输出最高置信度字符
        tesseract.setVariable("classify_enable_learning", "0");  // 禁用学习候选字符
        tesseract.setVariable("tessedit_char_whitelist", "0123456789.+-*/=?");  // 设置识别白名单
    }

    /**
     * 从 BufferedImage 中识别
     *
     * @param image 目标图片
     * @return 识别结果
     */
    public String recognize(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            return e.getMessage();
        }
    }

    /**
     * 从 File 中识别
     *
     * @param file 目标图片
     * @return 识别结构
     */
    public String recognize(File file) {
        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            return e.getMessage();
        }
    }
}
