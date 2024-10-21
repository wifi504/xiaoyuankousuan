package com.lhl.xyks.solve;

import java.awt.image.BufferedImage;

/**
 * 不同题目类型对应一个OCR接口
 *
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_17:44
 */
public interface QuestionOCR {

    /**
     * 多线程识别扫描题目
     *
     * @param image 题目图片
     * @return 题目表达式 & 模型信息
     */
    String multiOCR(BufferedImage image);

    /**
     * 单线程识别扫描题目
     *
     * @param image 题目图片
     * @return 题目表达式 & 模型信息
     */
    String singleOCR(BufferedImage image);
}
