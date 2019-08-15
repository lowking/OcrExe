package main.java.filefilter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * @Author: htc
 * @Date: 2019/8/8
 */
public class PicFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".jpg");
    }

    @Override
    public String getDescription() {
        return ".jpg";
    }
}
