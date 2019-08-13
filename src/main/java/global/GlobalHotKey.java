package main.java.global;

import static main.java.util.QrCodeUtil.getQrCode;
import static main.java.util.StringUtil.getWarpString;

import com.google.zxing.NotFoundException;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import main.java.EnterFrame;
import main.java.util.ClipBoardUtil;
import main.java.util.OcrUtil;
import main.java.util.QrCodeUtil;
import main.java.util.StringUtil;

public class GlobalHotKey implements HotkeyListener {
    public static final int shotHotKey = 88;
    //防止和全局热键冲突。
    private volatile boolean enterBusy = false;
    //第一次拖拽完成后，需要进行处理，这时候需要重新利用click,drag和release函数。
    private volatile boolean isProcess = false;
    /**
     * 拖拽参数
     **/
    //边界拉伸范围
    private static final int BREADTH = 7;
    //边界拉伸范围
    private static final int BREADTH2 = 14;
    private int dragType;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_MOVE = 1;
    private static final int DRAG_UP = 2;
    private static final int DRAG_UPLEFT = 3;
    private static final int DRAG_UPRIGHT = 4;
    private static final int DRAG_LEFT = 5;
    private static final int DRAG_RIGHT = 6;
    private static final int DRAG_BOTTOM = 7;
    private static final int DRAG_BOTTOMLEFT = 8;
    private static final int DRAG_BOTTOMRIGHT = 9;
    private JLabel snArea;
    private EnterFrame enterFrame;
    private JTextArea textArea;

    public GlobalHotKey() {
    }

    @Override
    public void onHotKey(int key) {

    }

    private AWTEventListener al;

    public void registerESC() {
        al = event -> {
            KeyEvent ke = (KeyEvent) event;
            if (ke.getID() == KeyEvent.KEY_PRESSED) {
                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    //System.out.println("当前AWT事件监听器数量"+ Toolkit.getDefaultToolkit().getAWTEventListeners().length);
                    clean();
                    unregisterESC();
                    jp.setDrag(false);
                } else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (enterBusy) {
                        return;
                    }
                    pastePic();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(al, AWTEvent.KEY_EVENT_MASK);
    }

    private void unregisterESC() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(al);
    }

    private JFrame jf;
    private MyContentPane jp;
    private BufferedImage bi;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public void shotProcess(EnterFrame enterFrame, JLabel snArea, JTextArea textArea) throws AWTException {
        this.snArea = snArea;
        this.enterFrame = enterFrame;
        this.textArea = textArea;
        jf = new JFrame();
        jf.setUndecorated(true);
        jf.setBounds(0, 0, screenSize.width, screenSize.height);
        jf.setAlwaysOnTop(true);
        jp = new MyContentPane(this);
        jp.setOpaque(false);
        jp.setLayout(null);
        Robot rb;
        try {
            rb = new Robot();
        } catch (AWTException e) {
            throw e;
        }
        bi = rb.createScreenCapture(new Rectangle(screenSize));
        drawMouse(bi);
        img = new ImageIcon(bi);
        MouseEvent e = new MouseEvent(this);
        jp.addMouseListener(e);
        jp.addMouseMotionListener(e);
        jf.add(jp);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    /**
     * 图像添加鼠标:http://blog.csdn.net/eguid_1/article/details/52973508
     **/
    private void drawMouse(BufferedImage bi) {
        Graphics2D g2d = (Graphics2D) bi.getGraphics();
        g2d.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
        g2d.dispose();
    }

    private ImageIcon img;

    private class MyContentPane extends JPanel {
        GlobalHotKey ek;

        MyContentPane(GlobalHotKey ek) {
            this.ek = ek;
        }

        volatile boolean moveFlag = false;

        synchronized boolean isMove() {
            return moveFlag;
        }

        synchronized void setMove(boolean f) {
            moveFlag = f;
        }

        volatile boolean dragFlag = false;

        public synchronized boolean isDrag() {
            return dragFlag;
        }

        synchronized void setDrag(boolean f) {
            dragFlag = f;
        }

        @Override
        protected void paintComponent(Graphics g) {
            /*
             * 设置透明度 https://wenku.baidu.com/view/d90f110d227916888486d7ee.html
             */
            if (img != null) {
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            Composite old = ((Graphics2D) g).getComposite();
            ((Graphics2D) g).setComposite(ac);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, screenSize.width, screenSize.height);
            ((Graphics2D) g).setComposite(old);

            g.drawImage(img.getImage(), start.x, start.y, end.x, end.y, start.x, start.y, end.x, end.y, this);
            g.setColor(new Color(2, 169, 255));
            if (isMove()) {
                Stroke oldS = ((Graphics2D) g).getStroke();
                ((Graphics2D) g).setStroke(new BasicStroke(4.0f));
                g.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
                ((Graphics2D) g).setStroke(oldS);
                setMove(false);
            } else {
                drawBound(g);
            }
            super.paintComponent(g);
        }

        Point myStart = new Point(0, 0);

        public void correctMyStart() {
            if ((start.x <= end.x) && (start.y <= end.y)) {
                myStart.x = start.x;
                myStart.y = start.y;
            } else if ((start.x <= end.x) && (start.y > end.y)) {
                //右上角
                myStart.x = start.x;
                myStart.y = end.y;
            } else if ((start.x > end.x) && (start.y <= end.y)) {
                //左下角
                myStart.x = end.x;
                myStart.y = start.y;
            } else {
                myStart.x = end.x;
                myStart.y = end.y;
            }
        }

        int offset = 3;
        int[][] point;

        private void drawBound(Graphics g) {
            myStart = new Point(0, 0);
            /*纠正反方向错误**/
            correctMyStart();
            g.drawRect(myStart.x, myStart.y, Math.abs(end.x - start.x), Math.abs(end.y - start.y));
            point = new int[][]{{start.x, start.y}, {(start.x + end.x) / 2, start.y}, {end.x, start.y}, {end.x, (start.y + end.y) / 2}, {end.x, end.y}, {(start.x + end.x) / 2, end.y}, {start.x, end.y}, {start.x, (start.y + end.y) / 2}};
            for (int[] aPoint : point) {
                g.fillRect(aPoint[0] - offset, aPoint[1] - offset, 2 * offset, 2 * offset);
            }
        }
    }

    private Point start = new Point(0, 0), end = new Point(0, 0);
    private Point prePos, startCopy, endCopy;

    /**
     * 根据矩形的start和end两点裁剪出要截取的图形。
     */
    private BufferedImage clipArea() {
        BufferedImage shotArea = new BufferedImage(end.x - start.x, end.y - start.y, BufferedImage.TYPE_INT_RGB);
        Graphics g = shotArea.getGraphics();
        g.drawImage(bi, 0, 0, end.x - start.x, end.y - start.y, start.x, start.y, end.x, end.y, null);
        return shotArea;
    }

    /**
     * 将图像复制到系统剪贴板上
     **/
    private void pastePic() {
        /*将图片复制到剪贴板,参考链接:http://blog.csdn.net/u010982856/article/details/44747029**/
        Images images;
        String text = null;
        try {
            images = new Images((new ImageIcon(clipArea())).getImage());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(images, null);
            text = OcrUtil.showOcrResultForImg(images, snArea);

            if (text.toUpperCase().startsWith("QR") && text.length() > 2) {
                //显示识别出来的文字生成的二维码
                //去除空格之类的
                text = StringUtil.replaceBlank(text).substring(2);
                ImageIcon icon = new ImageIcon(getQrCode(text));
                icon = new ImageIcon(
                        icon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT));
                snArea.setIcon(icon);
                snArea.setText("");
                textArea.setText(text);
                textArea.setCaretPosition(0);
                //识别成功置顶窗口
                enterFrame.setAlwaysOnTop(true);
                enterFrame.setAlwaysOnTop(false);
            } else {
                //如果图片是二维码,复制其内容
                try {
                    text = QrCodeUtil.getString(images.getBufferedImg());
                    textArea.setText(text);
                    ClipBoardUtil.setSysClipboardText(text);
                    //snArea.setText(getWarpString(snArea, text));
                    //snArea.setIcon(null);
                } catch (NotFoundException ignored) {
                    snArea.setIcon(null);
                    snArea.setText(getWarpString(snArea, text));
                }
            }
        } catch (IllegalArgumentException e1) {
            snArea.setText(text);
        } catch (Exception e) {
            snArea.setIcon(null);
            snArea.setText("截屏区域太小了");
        }

        clean();
    }

    private void clean() {
        jf.dispose();
        EnterFrame.shotBusy = false;
        isProcess = false;
        start = new Point(0, 0);
        end = new Point(0, 0);
    }

    private class MouseEvent extends MouseAdapter {
        GlobalHotKey ss;

        MouseEvent(GlobalHotKey screenShot) {
            this.ss = screenShot;
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (isProcess) {
                /*在处理状态下press时，记录press的点，作为起点。*/
                prePos = e.getPoint();
                startCopy = new Point(start);
                endCopy = new Point(end);
            } else {
                pressPro(e);
            }
        }

        private void pressPro(java.awt.event.MouseEvent e) {
            isSelExist();
            start = e.getPoint();
            end = new Point(start.x, start.y);
            ss.jp.setDrag(true);
            ss.jp.updateUI();
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (isProcess) {
                /*修正在反方向拖动时的区域修正及更新面板**/
                correctDir();
            } else {
                releaseProcess(e);
            }
        }

        private void releaseProcess(java.awt.event.MouseEvent e) {
            /* 如果只有点击没有拖拽，就进行窗体检测。*/
            correctDir();
            isSelExist();
            jf.dispose();
            pastePic();
            isDrag = false;
            isProcess = true;
        }

        private void isSelExist() {
            ss.jp.removeAll();
            ss.jp.updateUI();
        }

        volatile boolean isDrag = false;

        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
            if (isProcess) {
                /* 判断拖拽拉伸类型，然后处理。*/
                dragPro(e);
            } else {
                /*获取的新的拖拽点必须加工后才能送到绘图板。**/
                end = e.getPoint();
                ss.jp.updateUI();//这个必须要。
                isDrag = true;
            }
        }

        private void dragPro(java.awt.event.MouseEvent e) {
            Point curPos = e.getPoint();
            switch (dragType) {
                case DRAG_MOVE:
                    start.x = startCopy.x + curPos.x - prePos.x;
                    start.y = startCopy.y + curPos.y - prePos.y;
                    end.x = endCopy.x + curPos.x - prePos.x;
                    end.y = endCopy.y + curPos.y - prePos.y;
                    break;
                case DRAG_UPLEFT:
                    /*  start更新为拖动点         **/
                    start = new Point(curPos);
                    break;
                case DRAG_UP:
                    /*   只需要更新start.y          **/
                    start.y = curPos.y;
                    break;
                case DRAG_UPRIGHT:
                    /* 只需要更新start.y和end.x**/
                    start.y = curPos.y;
                    end.x = curPos.x;
                    break;
                case DRAG_RIGHT:
                    end.x = curPos.x;
                    break;
                case DRAG_BOTTOMRIGHT:
                    end = new Point(curPos);
                    break;
                case DRAG_BOTTOM:
                    end.y = curPos.y;
                    break;
                case DRAG_BOTTOMLEFT:
                    start.x = curPos.x;
                    end.y = curPos.y;
                    break;
                case DRAG_LEFT:
                    start.x = curPos.x;
                    break;
                default:
            }
            ss.jp.updateUI();
        }

        /**
         * 纠正方向使得start始终是矩形左上角，stop始终是矩形右下角。
         **/
        private void correctDir() {
            Point p;
            //一共四个方向。
            //右下角
            if ((start.x <= end.x) && (start.y <= end.y)) {
                return;
            } else if ((start.x <= end.x) && (start.y > end.y)) {
                //右上角
                p = new Point(start);
                start.y = end.y;
                end.y = p.y;
            } else if ((start.x > end.x) && (start.y <= end.y)) {
                //左下角
                p = new Point(end);
                end.y = start.y;
                start.y = p.y;
                swap();
            } else {
                swap();
            }
        }

        private void swap() {
            Point p = start;
            start = end;
            end = p;
        }

        @Override
        public void mouseMoved(java.awt.event.MouseEvent e) {
            /*鼠标移动自动判断当前哪个矩形中**/
            if (isProcess) {
                Point p = e.getPoint();
                if (new Rectangle(start.x - BREADTH, start.y - BREADTH, BREADTH2, BREADTH2).contains(p)) {
                    /*stretch upper-left**/
                    dragType = DRAG_UPLEFT;
                    jf.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                } else if (new Rectangle(start.x + BREADTH, start.y - BREADTH, end.x - start.x - BREADTH2, BREADTH2).contains(p)) {
                    /*stretch upper**/
                    dragType = DRAG_UP;
                    jf.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                } else if (new Rectangle(end.x - BREADTH, start.y - BREADTH, BREADTH2, BREADTH2).contains(p)) {
                    /*stretch upper-right**/
                    dragType = DRAG_UPRIGHT;
                    jf.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                } else if (new Rectangle(end.x - BREADTH, start.y + BREADTH, BREADTH2, end.y - start.y - BREADTH2).contains(p)) {
                    /*stretch right**/
                    dragType = DRAG_RIGHT;
                    jf.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                } else if (new Rectangle(end.x - BREADTH, end.y - BREADTH, BREADTH2, BREADTH2).contains(p)) {
                    /*stretch bottom-right**/
                    dragType = DRAG_BOTTOMRIGHT;
                    jf.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                } else if (new Rectangle(start.x + BREADTH, end.y - BREADTH, end.x - start.x - BREADTH2, BREADTH2).contains(p)) {
                    /*stretch bottom**/
                    dragType = DRAG_BOTTOM;
                    jf.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
                } else if (new Rectangle(start.x - BREADTH, end.y - BREADTH, BREADTH2, BREADTH2).contains(p)) {
                    /*stretch bottom-left**/
                    dragType = DRAG_BOTTOMLEFT;
                    jf.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
                } else if (new Rectangle(start.x - BREADTH, start.y + BREADTH, BREADTH2, end.y - start.y - BREADTH2).contains(p)) {
                    /*stretch left**/
                    dragType = DRAG_LEFT;
                    jf.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                } else if (new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y).contains(p)) {
                    /*如果在矩形内部，那么就是拖动**/
                    dragType = DRAG_MOVE;
                    jf.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                } else {
                    dragType = DRAG_NONE;
                }
            }
        }
    }


    public class Images implements Transferable {
        private Image image;

        public Images(Image image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }

        public BufferedImage getBufferedImg() {
            if (image instanceof BufferedImage) {
                return (BufferedImage) image;
            }
            image = new ImageIcon(image).getImage();
            BufferedImage bimage = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                int transparency = Transparency.OPAQUE;
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gs.getDefaultConfiguration();
                bimage = gc.createCompatibleImage(
                        image.getWidth(null), image.getHeight(null), transparency);
            } catch (HeadlessException ignored) {
            }

            if (bimage == null) {
                int type = BufferedImage.TYPE_INT_RGB;
                bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            }
            Graphics g = bimage.createGraphics();

            g.drawImage(image, 0, 0, null);
            g.dispose();

            return bimage;
        }
    }

    public void initHotkey() {
        // JIntellitype.getInstance().registerHotKey(shotHotKey, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int)(ScreenShot.jtf.getText().toCharArray()[0]));
        JIntellitype.getInstance().registerHotKey(shotHotKey, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT,
                101);
        JIntellitype.getInstance().addHotKeyListener(this);
    }
}