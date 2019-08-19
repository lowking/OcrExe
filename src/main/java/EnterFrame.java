package main.java;

import static main.java.util.QrCodeUtil.getQrCode;

import ch.randelshofer.quaqua.QuaquaManager;
import com.alee.laf.WebLookAndFeel;
import com.google.zxing.WriterException;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeException;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class EnterFrame extends JFrame implements HotkeyListener, ActionListener {
    /**
     * 防止重复截图
     */
    public static volatile boolean shotBusy = false;
    private static final int SHOT_HOT_KEY = 88;
    private JButton cutScreenBtn;
    private static ImageIcon imageIcon = new ImageIcon("src/main/resources/favicon.png");
    private static ImageIcon imageIcon_red = new ImageIcon("src/main/resources/favicon-red.png");
    private static ImageIcon imageIcon16x16 = new ImageIcon("src/main/resources/favicon16x16.png");
    private static JFrame jFrame;

    EnterFrame(String[] args) {
        jFrame = this;
        //隐藏最大化最小化按钮,但是窗体主题改变了
        //getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        //默认文本框一行字符
        int columns = 32;
        getContentPane().setBackground(Color.WHITE);
        //设置窗口大小
        setSize(286, 374);
        setResizable(false);
        //设置标题
        setTitle("");
        //设置图片
        setIconImage(imageIcon_red.getImage());
        //设置里面控件的布局方式
        this.setLayout(null);
        this.setAlwaysOnTop(true);
        //设置点击关闭对出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //添加label显示文本
        JLabel snArea = new JLabel("");
        snArea.setBounds(0, 65, 280, 280);
        snArea.setFont(new Font("",Font.PLAIN,18));
        snArea.setHorizontalAlignment(SwingConstants.CENTER);
        add(snArea);

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
        //激活自动换行功能
        textArea.setLineWrap(true);
        // 激活断行不断字功能
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(5, 5, 270, 55);
        add(scrollPane, BorderLayout.CENTER);
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
        textArea.setText("右键点击下面二维码截图");
        textArea.setText(textArea.getText() + "\n中键(滚轮)点击二维码置顶窗口(默认置顶)");
        snArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    //右键点击触发截图
                    cutScreenBtn.doClick();
                } else if (e.getButton() == 2) {
                    //中间点击窗口切换置顶
                    switchTop();
                }
            }
        });
        doOcrBtn.addActionListener(new DoOcrAction(snArea));
        doSelectFile.addActionListener(new DoSelectFileAction(this, snArea));
        cutScreenBtn.addActionListener(new ScreenShot.ShotE(this, snArea, textArea));

        //注册全局快捷键
        initHotkey(textArea);

        //初始化ocr实例
        OcrUtil.init();

        //显示窗口
        setVisible(true);
        
        timer.start();
        moveFrame();
        createTray();
    }

    /**
     * 切换置顶窗口
     */
    private void switchTop() {
        jFrame.setAlwaysOnTop(!jFrame.isAlwaysOnTop());
        if (jFrame.isAlwaysOnTop()) {
            jFrame.setIconImage(imageIcon_red.getImage());
        } else {
            jFrame.setIconImage(imageIcon.getImage());
        }
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


    private static Rectangle rect;
    /**
     * 窗体离屏幕左边的距离
     */
    private static int left;
    /**
     * 窗体离屏幕右边的距离
     */
    private static int right;
    /**
     * 屏幕的宽度
     */
    private static int screenXX;
    /**
     * 窗体离屏幕顶部的距离
     */
    private static int top;
    /**
     * 窗体的宽
     */
    private static int width;
    /**
     * 窗体的高
     */
    private static int height;
    /**
     * 鼠标在窗体的位置
     */
    private static Point point;
    private javax.swing.Timer timer = new javax.swing.Timer(10, this);
    private static int xx, yy;
    private static boolean isDraging = false;
    @Override
    public void actionPerformed(ActionEvent e) {
        //变相隐藏最小化按钮
        jFrame.setState(Frame.NORMAL);
        left = jFrame.getLocationOnScreen().x;
        top = jFrame.getLocationOnScreen().y;
        width = jFrame.getWidth();
        height = jFrame.getHeight();
        screenXX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        right = screenXX - left - width;
        // 获取窗体的轮廓
        rect = new Rectangle(0, 0, width, height);
        // 获取鼠标在窗体的位置
        point = jFrame.getMousePosition();
        if (left < 0 && isPtInRect(rect, point)) {
            //隐藏在左边，鼠标指到后显示窗体
            jFrame.setLocation(0, top);
        } else if (left > -5 && left < 5 && !(isPtInRect(rect, point))) {
            //窗体移到左边便边缘隐藏到左边
            jFrame.setLocation(left - width + 1, top);
        } else if ((top < 0 && left < 0) && isPtInRect(rect, point)) {
            //窗体在左上角
            //窗口隐藏了，鼠标指到他，就显示出来
            jFrame.setLocation(0, 0);
        } else if ((top > -5 && top < 5) && (left > -5 && left < 5)
                && !(isPtInRect(rect, point))) {
            // 当窗体的上边框与屏幕的顶端的距离小于5，
            // 并且鼠标不再窗体上将窗体隐藏到屏幕的顶端
            jFrame.setLocation(left - width + 1, 1);
        } else if ((top < 0) && isPtInRect(rect, point)) {
            //窗口隐藏了，鼠标指到他，就显示出来
            jFrame.setLocation(left, 0);
        } else if (top > -5 && top < 5 && !(isPtInRect(rect, point))) {
            // 当窗体的上边框与屏幕的顶端的距离小于5时，
            // 并且鼠标不再窗体上将窗体隐藏到屏幕的顶端
            jFrame.setLocation(left, 1 - height);
        } else if (right < 0 && isPtInRect(rect, point)) {
            //隐藏在右边，鼠标指到后显示
            jFrame.setLocation(screenXX - width + 1, top);
        } else if (right > -5 && right < 5 && !(isPtInRect(rect, point))) {
            //窗体移到屏幕右边边缘隐藏到右边
            jFrame.setLocation(screenXX - 1, top);
        } else if (right < 0 && top < 0 && isPtInRect(rect, point)) {
            //窗体在右上角
            //隐藏在右边，鼠标指到后显示
            jFrame.setLocation(screenXX - width + 1, 0);
        } else if ((right > -5 && right < 5) && (top > -5 && top < 5)
                && !(isPtInRect(rect, point))) {
            //窗体移到屏幕右边边缘隐藏到右边
            jFrame.setLocation(screenXX - 1, 1);
        }
    }

    private boolean isPtInRect(Rectangle rect, Point point) {
        if (rect != null && point != null) {
            int x0 = rect.x;
            int y0 = rect.y;
            int x1 = rect.width;
            int y1 = rect.height;
            int x = point.x;
            int y = point.y;
            return x >= x0 && x < x1 && y >= y0 && y < y1;
        }
        return false;
    }

    private void moveFrame() {
        jFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDraging = true;
                xx = e.getX();
                yy = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDraging = false;
            }
        });
        jFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDraging) {
                    int left = jFrame.getLocation().x;
                    int top = jFrame.getLocation().y;
                    jFrame.setLocation(left + e.getX() - xx, top + e.getY()
                            - yy);
                    jFrame.repaint();
                }
            }
        });
    }

    private void createTray() {
        PopupMenu pm = new PopupMenu();
        //窗口置顶
        MenuItem switchTop = new MenuItem("\u5207\u6362\u7a97\u53e3\u7f6e\u9876");
        switchTop.addActionListener(e -> {
            switchTop();
        });
        pm.add(switchTop);
        //退出
        MenuItem mi = new MenuItem("\u9000\u51fa");
        mi.addActionListener(e -> {
            JIntellitype.getInstance().unregisterHotKey(SHOT_HOT_KEY);
            System.exit(0);
        });
        pm.add(mi);
        TrayIcon ti = new TrayIcon(imageIcon16x16.getImage(), "ocr", pm);
        ti.addActionListener(e -> jFrame.setVisible(true));
        try {
            SystemTray.getSystemTray().add(ti);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }
}