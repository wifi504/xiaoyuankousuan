package com.lhl.xyks.solve;

import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.ocr.OCR;
import com.lhl.xyks.utils.ocr.OCRService;

import java.awt.image.BufferedImage;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/22_23:33
 */
public class PiSquareOCR implements QuestionOCR {

    OCR paddleOCR = OCRService.getModel("paddle");

    private BufferedImage getPaddleImage(BufferedImage image) {
        return ImageTools.blurPNG(image, 1.5f);
    }

    @Override
    public String multiOCR(BufferedImage image) {
        return singleOCR(image);
    }

    @Override
    public String singleOCR(BufferedImage image) {
        String paddle = paddleOCR.optimize(paddleOCR.recognize(getPaddleImage(image)));
        return ExpressionOptimizer.optimizePiSquare(paddle);
    }
}
