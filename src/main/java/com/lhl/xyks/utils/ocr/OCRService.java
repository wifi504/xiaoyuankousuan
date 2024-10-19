package com.lhl.xyks.utils.ocr;

import com.lhl.xyks.utils.ocr.impl.ChineseOCRImpl;
import com.lhl.xyks.utils.ocr.impl.EngOCRImpl;
import com.lhl.xyks.utils.ocr.impl.PaddleOCRImpl;
import com.lhl.xyks.utils.ocr.impl.XyksOCRImpl;

/**
 * OCR 实例服务
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_17:53
 */
public class OCRService {

    private static final OCR xyks = new XyksOCRImpl();
    private static final OCR eng = new EngOCRImpl();
    private static final OCR zh = new ChineseOCRImpl();
    private static final OCR paddle = new PaddleOCRImpl();


    public static OCR getModel(String model) {
        return switch (model) {
            case "xyks" -> xyks;
            case "eng" -> eng;
            case "zh" -> zh;
            case "paddle" -> paddle;
            default -> null;
        };
    }
}
