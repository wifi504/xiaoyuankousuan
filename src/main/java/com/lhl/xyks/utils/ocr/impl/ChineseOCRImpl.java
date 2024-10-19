package com.lhl.xyks.utils.ocr.impl;

import com.lhl.xyks.utils.ocr.OCR;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 屏幕识别工具类(Tesseract chi_sim 模型)
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_2:45
 */
public class ChineseOCRImpl implements OCR {

    private static final Tesseract tesseract = new Tesseract();

    /**
     * 构造方法，每个 OCR 对应一个 Tesseract 对象
     */
    public ChineseOCRImpl() {
        File tessdata = new File("tessdata");
        String dataPath = tessdata.getAbsolutePath().replaceAll("\\\\", "/");
        tesseract.setDatapath(dataPath);
        // 设置使用的语言模型
        tesseract.setLanguage("chi_sim");

        // 设置页面分割模式 (PSM)
        tesseract.setPageSegMode(6);  // 假设输入为单块文本

        tesseract.setVariable("tessedit_write_unlv", "0");  // 仅输出最高置信度字符
        tesseract.setVariable("classify_enable_learning", "0");  // 禁用学习候选字符
//        tesseract.setVariable("tessedit_char_whitelist", "0123456789.+-x=?");  // 设置识别白名单
    }

    @Override
    public String recognize(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            return e.getMessage();
        }
    }

    @Override
    public String recognize(File file) {
        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            return e.getMessage();
        }
    }
}
