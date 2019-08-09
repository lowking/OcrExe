package main.java;

import com.melloware.jintellitype.JIntellitype;
import main.java.global.GlobalHotKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScreenShot {
    private static JFrame jf;

    public static void main(String[] args) {
        new ScreenShot();
    }

    public static void setJf(JFrame jf) {
        ScreenShot.jf = jf;
    }

    public static class ShotE implements ActionListener {
        private JLabel snArea;
        boolean first = false;

        ShotE(JLabel snArea) {
            this.snArea = snArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            GlobalHotKey globalHotKey = new GlobalHotKey();
            try {
                globalHotKey.shotProcess(this.snArea);
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

        private void createTray() {
            PopupMenu pm = new PopupMenu();
            MenuItem mi = new MenuItem("exit");
            mi.addActionListener(e -> {JIntellitype.getInstance().unregisterHotKey(GlobalHotKey.shotHotKey);System.exit(0);});
            pm.add(mi);
            ImageIcon img = new ImageIcon(ScreenShot.class.getResource("trayIcon.png"));
            TrayIcon ti = new TrayIcon(img.getImage(), "screenShot", pm);
            ti.addActionListener(e -> jf.setVisible(true));
            try {
                SystemTray.getSystemTray().add(ti);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }
}