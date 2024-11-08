package com.lhl.xyks.solve;

import com.lhl.xyks.utils.ImageTools;
import com.lhl.xyks.utils.ocr.OCR;
import com.lhl.xyks.utils.ocr.OCRService;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/22_23:21
 */
public class Compare100OCR implements QuestionOCR {
    OCR xyksOCR = OCRService.getModel("xyks");
    OCR engOCR = OCRService.getModel("eng");
    OCR paddleOCR = OCRService.getModel("paddle");

    private BufferedImage getXyksImage(BufferedImage image) {
        return ImageTools.resizePNG(image, 1.25, 1.0);
    }

    private BufferedImage getEngImage(BufferedImage image) {
        return ImageTools.blurPNG(image, 1.5f);
    }

    private BufferedImage getPaddleImage(BufferedImage image) {
        return image;
    }


    @Override
    public String multiOCR(BufferedImage image) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<String> result1 = executor.submit(() -> xyksOCR.optimize(xyksOCR.recognize(getXyksImage(image))));
        Future<String> result3 = executor.submit(() -> engOCR.optimize(engOCR.recognize(getEngImage(image))));
        Future<String> result2 = executor.submit(() -> paddleOCR.optimize(paddleOCR.recognize(getPaddleImage(image))));
        try {
            String xyks = result1.get();
            String eng = result3.get();
            String paddle = result2.get();
            return ExpressionOptimizer.optimizeCompare100(xyks, eng, paddle);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("多线程识别异常：", e);
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public String singleOCR(BufferedImage image) {
        String xyks = xyksOCR.optimize(xyksOCR.recognize(getXyksImage(image)));
        String eng = engOCR.optimize(engOCR.recognize(getEngImage(image)));
        String paddle = paddleOCR.optimize(paddleOCR.recognize(getPaddleImage(image)));
        return ExpressionOptimizer.optimizeCompare100(xyks, eng, paddle);
    }
}
