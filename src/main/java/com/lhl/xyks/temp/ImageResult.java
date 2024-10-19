package com.lhl.xyks.temp;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2024/10/19_21:20
 */
class ImageResult {
    String ModelType;
    String ImageType;
    int correct = 0;
    int all = 0;

    public ImageResult(String modelType, String imageType) {
        ModelType = modelType;
        ImageType = imageType;
    }

    public ImageResult correct() {
        correct++;
        all++;
        return this;
    }

    public ImageResult wrong() {
        all++;
        return this;
    }

    public double getScore() {
        return (double) correct / all;
    }

    @Override
    public String toString() {
        return "识别结果集{" +
                "模型类型='" + ModelType + '\'' +
                ", 图片类型='" + ImageType + '\'' +
                ", 正确数量=" + correct +
                ", 总数=" + all +
                ", 正确率=" + getScore() +
                '}';
    }
}
