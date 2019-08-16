package main.java;

import static main.java.util.QrCodeUtil.getQrCode;

import ch.randelshofer.quaqua.QuaquaManager;
import com.alee.laf.WebLookAndFeel;
import com.google.zxing.WriterException;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import main.java.actions.DoOcrAction;
import main.java.actions.DoSelectFileAction;
import main.java.util.OcrUtil;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class EnterFrame extends JFrame implements HotkeyListener {
    /**
     * 防止重复截图
     */
    public static volatile boolean shotBusy = false;
    private static final int SHOT_HOT_KEY = 88;
    private JButton cutScreenBtn;

    EnterFrame(String[] args) {
        //默认文本框一行字符
        int columns = 32;
        getContentPane().setBackground(Color.WHITE);
        //设置窗口大小
        setSize(295, 370);
        //设置标题
        setTitle("");
        ImageIcon imageIcon = new ImageIcon("src/main/resources/favicon.png");
        //设置图片
        setIconImage(imageIcon.getImage());
        //设置里面控件的布局方式
        this.setLayout(null);
        //设置点击关闭对出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //添加label显示文本
        JLabel snArea = new JLabel("");
        add(snArea);
        snArea.setBounds(0, 55, 280, 280);
        snArea.setFont(new Font("",Font.PLAIN,18));
        snArea.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            String theme = null;
            boolean isSetBySelf = true;
            if (args.length > 0) {
                switch (args[0]) {
                    case "mac":
                        columns = 22;
                        theme = QuaquaManager.getLookAndFeelClassName();
                        break;
                    case "metal":
                        columns = 23;
                        theme = "javax.swing.plaf.metal.MetalLookAndFeel";
                        break;
                    case "nimbus":
                        columns = 22;
                        theme = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
                        break;
                    case "weblaf":
                        columns = 44;
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

        //添加文本框
        JTextArea textArea = new JTextArea("", 2, columns);
        add(textArea);
        textArea.setBounds(100, 100, 260, 45);
        //激活自动换行功能
        textArea.setLineWrap(true);
        // 激活断行不断字功能
        textArea.setWrapStyleWord(true);
        JPanel panelOutput;
        panelOutput = new JPanel();
        panelOutput.add(new JScrollPane(textArea));
        add(panelOutput);
        panelOutput.setBounds(0, 0, 280, 55);
        panelOutput.setBackground(Color.white);
        //监听内容变化
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            Timer timer = null;

            private void createQrCode() {
                try {
                    String text = textArea.getText();
                    if (text == null || text.length() <= 0) {
                        return;
                    }
                    ImageIcon icon = new ImageIcon(getQrCode(text));
                    icon = new ImageIcon(
                            icon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT));
                    snArea.setIcon(icon);
                    snArea.setText("");
                } catch (WriterException | IOException ignored) {
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        createQrCode();
                        timer.cancel();
                    }
                };
                timer.schedule(task, 500, 500);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        createQrCode();
                        timer.cancel();
                    }
                };
                timer.schedule(task, 500, 500);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        //添加识别按钮
        JButton doOcrBtn = new JButton("识别并复制");
        doOcrBtn.setBounds(10, 10, 100, 45);

        //添加文件选择器按钮
        JButton doSelectFile = new JButton("选择");
        doSelectFile.setBounds(120, 10, 70, 45);

        //添加文件选择器按钮
        cutScreenBtn = new JButton("截图");
        cutScreenBtn.setBounds(200, 10, 70, 45);

        //按钮添加点击事件
        snArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    //右键点击触发截图
                    cutScreenBtn.doClick();
                } else if (e.getButton() == 2) {
                    //中间点击窗口切换置顶
                    EnterFrame.super.setAlwaysOnTop(!EnterFrame.super.isAlwaysOnTop());
                }
            }
        });
        doOcrBtn.addActionListener(new DoOcrAction(snArea));
        doSelectFile.addActionListener(new DoSelectFileAction(this, snArea));
        cutScreenBtn.addActionListener(new ScreenShot.ShotE(this, snArea, textArea));

        //显示窗口
        setVisible(true);

        //注册全局快捷键
        initHotkey(textArea);

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
        if (SHOT_HOT_KEY == key && !shotBusy) {
            shotBusy = true;
            cutScreenBtn.doClick();
        }
    }

    private void initHotkey(JTextArea textArea) {
        try {
            JIntellitype.getInstance().registerHotKey(SHOT_HOT_KEY, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, 69);
            textArea.setText(
                    ("".equals(textArea.getText()) ?
                            textArea.getText() + "Ctrl+Alt+E快捷键注册成功" : textArea.getText() + "\nCtrl+Alt+E快捷键注册成功")
            );
        } catch (JIntellitypeException e) {
            textArea.setText(
                    ("".equals(textArea.getText()) ?
                            textArea.getText() + "Ctrl+Alt+E快捷键注册失败" : textArea.getText() + "\nCtrl+Alt+E快捷键注册失败")
            );
        }
        try {
            JIntellitype.getInstance().registerHotKey(SHOT_HOT_KEY, JIntellitype.MOD_ALT, 90);
            textArea.setText(
                    ("".equals(textArea.getText()) ?
                            textArea.getText() + "Alt+Z快捷键注册成功" : textArea.getText() + "\nAlt+Z快捷键注册成功")
            );
        } catch (JIntellitypeException e) {
            textArea.setText(
                    ("".equals(textArea.getText()) ?
                            textArea.getText() + "Alt+Z快捷键注册失败" : textArea.getText() + "\nAlt+Z快捷键注册失败")
            );
        }
        JIntellitype.getInstance().addHotKeyListener(this);
    }
}