package main.java;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import main.java.global.GlobalHotKey;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class ScreenShot {

    public static void main(String[] args) {
        new ScreenShot();
    }

    public static class ShotE implements ActionListener {
        private JLabel snArea;
        private EnterFrame enterFrame;
        private JTextArea textArea;
        boolean first = false;

        ShotE(EnterFrame enterFrame, JLabel snArea, JTextArea textArea) {
            this.snArea = snArea;
            this.enterFrame = enterFrame;
            this.textArea = textArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GlobalHotKey globalHotKey = new GlobalHotKey();
            try {
                globalHotKey.shotProcess(this.enterFrame, this.snArea, this.textArea);
            } catch (AWTException e1) {
                snArea.setText("截图未能启动");
            }
            globalHotKey.registerESC();//注册窗体全局热键
            if (!first) {
                setHotKey();
                first = true;
            }
        }

        GlobalHotKey key;

        private void setHotKey() {
            key = new GlobalHotKey();
            key.initHotkey();
        }
    }
}