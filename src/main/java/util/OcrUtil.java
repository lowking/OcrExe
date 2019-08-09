package main.java.util;

import main.java.global.GlobalHotKey;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.swing.*;
import java.io.File;

/**
 * @Author: htc
 * @Date: 2019/8/8
 */
public class OcrUtil {

    public static ITesseract instance;

    private static final String datapath = "src/main/resources/tessdata";

    public static void init() {
        instance = new Tesseract();
        instance.setDatapath(new File(datapath).getPath());
    }

    public static void showOcrResult(File img, JLabel snArea){
        if (!img.exists()) {
            snArea.setText("未找到要识别的文件");
        } else {
            String result;
            try {
                result = instance.doOCR(img);
                ClipBoardUtil.setSysClipboardText(result);
                String labelString =
                        "<html><body>" + result.replaceAll("\\r\\n|\\n", "<br>") + "<body></html>";
                snArea.setText(labelString);
            } catch (TesseractException e1) {
                e1.printStackTrace();
                snArea.setText("识别错误!");
            }
        }
    }

    public static void showOcrResultForImg(GlobalHotKey.Images images, JLabel snArea) {
        if (images == null) {
            snArea.setText("未获取到截图,请重新截图");
        } else {
            String result;
            try {
                result = instance.doOCR(images.getBufferedImg());
                ClipBoardUtil.setSysClipboardText(result);
                String labelString =
                        "<html><body>" + result.replaceAll("\\r\\n|\\n", "<br>") + "<body></html>";
                snArea.setText(labelString);
            } catch (TesseractException e1) {
                e1.printStackTrace();
                snArea.setText("识别错误!");
            }
        }
    }
}
