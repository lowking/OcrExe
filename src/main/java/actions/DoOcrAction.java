package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JLabel;
import main.java.util.OcrUtil;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class DoOcrAction extends JFrame implements ActionListener {
    private JLabel snArea;

    public DoOcrAction(JLabel snArea) {
        this.snArea = snArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File img = new File("c:/1.jpg");
        OcrUtil.showOcrResult(img, snArea);
    }
}
