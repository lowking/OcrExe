package main.java;

import ch.randelshofer.quaqua.QuaquaManager;
import com.alee.laf.WebLookAndFeel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import main.java.actions.DoOcrAction;
import main.java.actions.DoSelectFileAction;
import main.java.util.OcrUtil;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        //设置窗口大小
        setSize(480, 100);
        //设置标题
        setTitle("单号识别");
        ImageIcon imageIcon = new ImageIcon("src/main/resources/icon2.jpg");
        //设置图片
        setIconImage(imageIcon.getImage());
        //设置里面控件的布局方式
        this.setLayout(null);
        //设置点击关闭对出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();

        //添加label显示文本
        JLabel snArea = new JLabel("");
        mainFrame.add(snArea);
        snArea.setBounds(10, 10, 370, 45);

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
                        theme = WebLookAndFeel.class.getCanonicalName();;
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
        mainFrame.add(doOcrBtn);
        doOcrBtn.setBounds(270, 10, 100, 45);

        //添加文件选择器按钮
        JButton doSelectFile = new JButton("选择");
        mainFrame.add(doSelectFile);
        doSelectFile.setBounds(380, 10, 70, 45);

        //按钮添加点击事件
        doOcrBtn.addActionListener(new DoOcrAction(snArea));
        doSelectFile.addActionListener(new DoSelectFileAction(mainFrame, snArea));

        //显示窗口
        mainFrame.setVisible(true);

       //初始化ocr实例
        OcrUtil.init();
    }
}