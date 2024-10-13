package com.lhl.xyks.pk;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/12_1:01
 */
public class OCR {


    // 从一个流 BufferedImage 识别图片的算式，返回 String
    public static String doOCRFromBufferedImage(BufferedImage bi) {
        ITesseract instance = new Tesseract();
        // 设置 Tesseract
        instance.setLanguage("eng");
        instance.setDatapath("E:\\lhl_work\\xiaoyuankousuan\\src\\main\\resources\\tessdata");
        instance.setTessVariable("tessedit_char_whitelist", ".x0123456789");
        instance.setTessVariable("classify_bln_numeric_mode", "1");  // 启用数字模式
        instance.setTessVariable("user_defined_dpi", "72");
        String s = "";
        try {
            s = instance.doOCR(bi);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return s.trim();
    }
}
