package main.java.util;

import static main.java.util.StringUtil.getWarpString;

import java.io.File;
import javax.swing.JLabel;
import main.java.global.GlobalHotKey;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

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
                snArea.setText(getWarpString(snArea, result.replaceAll("\\r\\n|\\n", "<br>")));
            } catch (TesseractException e1) {
                e1.printStackTrace();
                snArea.setText("识别错误!");
            }
        }
    }

    public static String showOcrResultForImg(GlobalHotKey.Images images, JLabel snArea) {
        String result = "";
        if (images == null) {
            snArea.setText("未获取到截图,请重新截图");
        } else {
            try {
                result = instance.doOCR(images.getBufferedImg()).trim();
                ClipBoardUtil.setSysClipboardText(result);
                snArea.setText(getWarpString(snArea, result));
            } catch (TesseractException e1) {
                e1.printStackTrace();
                snArea.setText("识别错误!");
            }
        }
        return result;
    }
}
