package main.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import main.java.MainFrame;
import main.java.filefilter.PicFilter;
import main.java.util.OcrUtil;

/**
 * @Author: htc
 * @Date: 2019/8/7
 */
public class DoSelectFileAction extends JFrame implements ActionListener {

    private final MainFrame mainFrame;
    private final JLabel snArea;
    private String defaultDirectory = "c:/";

    public DoSelectFileAction(MainFrame mainFrame, JLabel snArea) {
        this.mainFrame = mainFrame;
        this.snArea = snArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        //设置文件类型过滤
        jfc.setFileFilter(new PicFilter());
        //设置默认目录
        jfc.setCurrentDirectory(new File(defaultDirectory));
        int flag = jfc.showOpenDialog(mainFrame);
        //若选择了文件，则打印选择了什么文件
        String fileName;
        if (flag == JFileChooser.APPROVE_OPTION) {
            fileName = jfc.getSelectedFile().getName();
            defaultDirectory = jfc.getSelectedFile().getPath();
        }else{
            snArea.setText("请重新选择!");
            return;
        }

        if (!fileName.endsWith(".jpg")) {
            snArea.setText("请选择jpg格式的文件");
            return;
        }

        OcrUtil.showOcrResult(jfc.getSelectedFile(), snArea);
    }
}
