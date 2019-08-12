package main.java.util;

import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: htc
 * @Date: 2019/8/12
 */
public class QrCodeUtil {

    public static final String CHARTSET = "utf-8";

    public static String getString(BufferedImage bufferedImage) throws NotFoundException {
        Result result;
        try {
            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));

            HashMap hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, CHARTSET);
            result = new MultiFormatReader().decode(bitmap, hints);
        } catch (NotFoundException e) {
            throw e;
        }
        return result.getText();
    }

    /**
     * 如果用的jdk是1.9，需要配置下面这一行。
     * System.setProperty("java.specification.version", "1.9");
     */
    public static BufferedImage getQrCode(String text) throws WriterException {
        Map hints = new HashMap();
        //设置编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置容错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置边距默认是5
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            throw e;
        }
    }
}
