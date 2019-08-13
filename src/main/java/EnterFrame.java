package main.java;

import ch.randelshofer.quaqua.QuaquaManager;
import com.alee.laf.WebLookAndFeel;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import main.java.actions.DoOcrAction;
import main.java.actions.DoSelectFileAction;
import main.java.util.OcrUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class EnterFrame extends JFrame implements HotkeyListener {
    //防止重复截图。
    public static volatile boolean shotBusy = false;
    private static final int shotHotKey = 88;
    private JButton cutScreenBtn;

    EnterFrame(String[] args) {
        getContentPane().setBackground(Color.WHITE);
        //设置窗口大小
        setSize(295, 370);
        //设置标题
        setTitle("单号识别");
        ImageIcon imageIcon = new ImageIcon("src/main/resources/icon2.jpg");
        //设置图片
        setIconImage(imageIcon.getImage());
        //设置里面控件的布局方式
        this.setLayout(null);
        //设置点击关闭对出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //添加label显示文本
        JLabel snArea = new JLabel("");
        add(snArea);
        snArea.setBounds(0, 53, 280, 280);
        snArea.setFont(new Font("",Font.PLAIN,18));
        snArea.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            String theme = null;
            boolean isSetBySelf = true;
            if (args.length > 0) {
                switch (args[0]) {
                    case "mac":
                        theme = QuaquaManager.getLookAndFeelClassName();
                        break;
                    case "metal":
                        theme = "javax.swing.plaf.metal.MetalLookAndFeel";
                        break;
                    case "nimbus":
                        theme = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
                        break;
                    case "weblaf":
                        theme = WebLookAndFeel.class.getCanonicalName();
                        break;
                    case "beautyeye":
                        try {
                            isSetBySelf = false;
                            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
                        } catch (Exception e) {
                            theme = UIManager.getSystemLookAndFeelClassName();
                        }
                        break;
                    default:
                        theme = UIManager.getSystemLookAndFeelClassName();
                        break;
                }
            } else {
                theme = UIManager.getSystemLookAndFeelClassName();
            }
            if (isSetBySelf) {
                UIManager.setLookAndFeel(theme);
            }
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (Exception e1) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e2) {
                snArea.setText("初始化样式失败");
            }
        }

        //添加识别按钮
        JButton doOcrBtn = new JButton("识别并复制");
        add(doOcrBtn);
        doOcrBtn.setBounds(10, 10, 100, 45);

        //添加文件选择器按钮
        JButton doSelectFile = new JButton("选择");
        add(doSelectFile);
        doSelectFile.setBounds(120, 10, 70, 45);

        //添加文件选择器按钮
        cutScreenBtn = new JButton("截图");
        add(cutScreenBtn);
        cutScreenBtn.setBounds(200, 10, 70, 45);

        //按钮添加点击事件
        doOcrBtn.addActionListener(new DoOcrAction(snArea));
        doSelectFile.addActionListener(new DoSelectFileAction(this, snArea));
        cutScreenBtn.addActionListener(new ScreenShot.ShotE(this, snArea));

        //显示窗口
        setVisible(true);

        //注册全局快捷键
        initHotkey();

        //初始化ocr实例
        OcrUtil.init();
        
        //初始化参数
        //这个是强制缩放到与组件(Label)大小相同
        //icon=new ImageIcon(icon.getImage().getScaledInstance(getWidth(), getHeight()-25, Image.SCALE_DEFAULT));
        //这个是按等比缩放
    }

    @Override
    public void onHotKey(int key) {
        //ctrl+alt+e
        if (88 == key && !shotBusy) {
            shotBusy = true;
            cutScreenBtn.doClick();
        }
    }

    private void initHotkey() {
        JIntellitype.getInstance().registerHotKey(shotHotKey, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, 69);
        JIntellitype.getInstance().addHotKeyListener(this);
    }
}