package com.lhl.xyks;

import com.lhl.xyks.temp.HandleTrainingData;

/**
 * @author WIFI连接超时
 * @version 2.0
 * Create Time: 2024/10/14_0:50
 */
public class Main {
    public static void main(String[] args) {
        // 程序入口
        System.out.println("hello world!");
        if (args[1].equals("step1")) {
            new HandleTrainingData().setFolderPath(args[0]).step1();
        } else if (args[1].equals("step2")) {
            new HandleTrainingData().setFolderPath(args[0]).step2();
        } else if (args[1].equals("step3")) {
            new HandleTrainingData().setFolderPath(args[0]).step3();
        } else if (args[1].equals("step4")) {
            new HandleTrainingData().setFolderPath(args[0]).step4();
        } else if (args[1].equals("step5")) {
            new HandleTrainingData().setFolderPath(args[0]).step5();
        } else if (args[1].equals("step6")) {
            new HandleTrainingData().setFolderPath(args[0]).step6();
        }
    }
}
