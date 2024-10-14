package com.lhl.xyks.test.utils;

import com.lhl.xyks.utils.OCR;
import org.junit.Test;

import java.io.*;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_16:07
 */
public class OCRTest {

    @Test
    public void testRecognizeFormula() {
        OCR ocr = new OCR();
        System.out.println(ocr.recognize(new File("1.png")));
    }

    @Test
    public void testRecognizeImages() {
        OCR ocr = new OCR();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 200; i++) {
            System.out.println("----------------------------------");
            System.out.println("识别：" + i);
            System.out.println(ocr.recognize(new File("training-data/compare20_n" + i + ".png")));
            System.out.println();
            System.out.print("预期：");
            try (FileReader fr = new FileReader("training-data/compare20_n" + i + ".txt")) {
                // 开始读
                char[] chars = new char[512];
                int readCount = 0;
                while ((readCount = fr.read(chars)) != -1) {
                    String s = new String(chars, 0, readCount);
                    sb.append(s);
                    System.out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter fr = new FileWriter("training-data/text.txt")) {
            fr.write(sb.toString());
            fr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
