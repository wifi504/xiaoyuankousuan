package com.lhl.xyks.utils.ocr.impl;

import com.benjaminwan.ocrlibrary.OcrResult;
import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.ocr.OCR;
import io.github.mymonstercat.Model;
import io.github.mymonstercat.ocr.InferenceEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 屏幕识别工具类(使用 RapidOcr 载入 Paddle OCR 模型)
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_2:37
 */
public class PaddleOCRImpl implements OCR {

    private static InferenceEngine engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V3);

    public PaddleOCRImpl() {
    }

    @Override
    public String recognize(BufferedImage image) {
        File file = new File(UUID.randomUUID() + ".png");
        try {
            ImageTools.saveImageToFile(image, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String recognize = recognize(file);
        file.delete();
        return recognize;
    }

    @Override
    public String recognize(File file) {
        OcrResult ocrResult = engine.runOcr(file.getAbsolutePath());
        return ocrResult.getStrRes();
    }

    @Override
    public String optimize(String text) {
        String result = text.replaceAll("\\s+", "")
                .replaceAll("×", "*")
                .replaceAll("÷", "/");
        result = toHalfWidth(result);

        return result.startsWith("?") ? result.substring(1) + "?" : result;
    }

    /**
     * 将全角字符转换为半角字符
     *
     * @param input 输入字符串
     * @return 转换后的字符串
     */
    private static String toHalfWidth(String input) {
        StringBuilder output = new StringBuilder();
        for (char c : input.toCharArray()) {
            // 全角空格的unicode为12288，半角空格的unicode为32
            if (c == 12288) {
                output.append((char) 32);
            }
            // 其他全角字符与半角字符的差距为65248
            else if (c >= 65281 && c <= 65374) {
                output.append((char) (c - 65248));
            } else {
                output.append(c);  // 其他字符不变
            }
        }
        return output.toString();
    }
}
