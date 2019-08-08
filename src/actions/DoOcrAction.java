package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JLabel;
import main.java.util.ClipBoardUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class DoOcrAction extends JFrame implements ActionListener {
    ITesseract instance;

    private final String datapath = "src/main/resources/tessdata";

    JLabel snArea;

    public DoOcrAction(JLabel snArea) {
        this.snArea = snArea;
        instance = new Tesseract();
        instance.setDatapath(new File(datapath).getPath());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File img = new File("c:/1.jpg");
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
}
