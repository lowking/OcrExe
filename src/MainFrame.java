import actions.DoOcrAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        //设置窗口大小
        setSize(400, 100);
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
        //添加识别按钮
        JButton doOcrBtn = new JButton("识别并复制");
        mainFrame.add(doOcrBtn);
        doOcrBtn.setBounds(270, 10, 100, 45);
        //添加label显示文本
        JLabel snArea = new JLabel("");
        mainFrame.add(snArea);
        snArea.setBounds(10, 10, 370, 45);
        //按钮添加点击事件
        doOcrBtn.addActionListener(new DoOcrAction(snArea));
        //显示窗口
        mainFrame.setVisible(true);
    }
}