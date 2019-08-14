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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

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
    public static BufferedImage getQrCode(String text) throws WriterException, IOException {
        Map hints = new HashMap();
        //设置编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置容错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置边距默认是5
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix=new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            return drawImageMark(MatrixToImageWriter.toBufferedImage(bitMatrix), 3, 0xFF475272, 0x00FFFFFF);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 给图片加中间logo
     * @param buf
     * @return
     * @throws IOException
     */
    public static BufferedImage drawImageMark(BufferedImage buf, int scale, int frontColor, int backColor) throws IOException {
        BufferedImage buf2 = ImageIO.read(new File("src/main/resources/logo.png"));
        int w = buf.getWidth() * 7, h = buf.getHeight() * 7;
        int iconW = w / scale, iconH = h / scale;

        BufferedImage newimage = new BufferedImage(w , h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = newimage.createGraphics();
        g2d.drawImage(buf, 0, 0, w, h, null);
        //修改二维码颜色
        int r, g, b, c;
        double distance;
        int a;
        ColorModel cm = ColorModel.getRGBdefault();
        for (int x = 0; x < newimage.getWidth(); x++) {
            for (int y = 0; y < newimage.getHeight(); y++) {
                newimage.setRGB(x, y, newimage.getRGB(x, y) == 0xffffffff ? backColor : frontColor);
                distance = getDistanceToCenter(w / 2, h / 2, x, y);
                c = newimage.getRGB(x, y);
                r = cm.getRed(c);
                g = cm.getGreen(c);
                b = cm.getBlue(c);
                //决定渐变的半径
                double jbr = iconW * 1.5;
                //点离圆心距离小于半径开始渐变
                if (distance < jbr) {
                    //计算渐变的alpha值
                    a = new BigDecimal(distance).multiply(new BigDecimal(255))
                            .divide(new BigDecimal(jbr), 0, BigDecimal.ROUND_HALF_UP)
                            .intValue();
                    newimage.setRGB(x, y, new Color(r, g, b, a).getRGB());
                }
            }
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        //上水印logo
        g2d.drawImage(buf2, w / 2 - (iconW / 2), h / 2 - (iconW / 2), iconW, iconH, null);
        g2d.dispose();

        return newimage;
    }

    /**
     * 判断点是否在圆内
     * @param r         圆半径
     * @param centerX1  圆心x
     * @param centerY1  圆心y
     * @param x2        坐标x
     * @param y2        坐标y
     * @return true:在圆内 false:不在
     */
    private static boolean isInCircle(double r, float centerX1, float centerY1, float x2, float y2) {
        double distance = Math.sqrt((y2 - centerY1) * (y2 - centerY1) + (x2 - centerX1) * (x2 - centerX1));
        if (!(distance > r)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取点到圆心的距离
     * @param centerX1 圆心x
     * @param centerY1 圆心y
     * @param x2       坐标x
     * @param y2       坐标y
     * @return 距离
     */
    private static double getDistanceToCenter(float centerX1, float centerY1, float x2, float y2) {
        return Math.sqrt((y2 - centerY1) * (y2 - centerY1) + (x2 - centerX1) * (x2 - centerX1));
    }
}
