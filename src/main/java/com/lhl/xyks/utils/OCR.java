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
        tesseract.setLanguage("eng");
        tesseract.setTessVariable("tessedit_char_whitelist", ".x?0123456789");
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
