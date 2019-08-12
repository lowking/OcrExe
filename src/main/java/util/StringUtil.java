package main.java.util;

import java.awt.FontMetrics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;

/**
 * @Author: htc
 * @Date: 2019/8/12
 */
public class StringUtil {

    /**
     * 预编译加快匹配速度
     */
    private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 去除空白符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 根据jlabel自动换行
     * @param snArea
     * @param str
     * @return
     */
    public static String getWarpString(JLabel snArea, String str) {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = str.toCharArray();
        FontMetrics fontMetrics = snArea.getFontMetrics(snArea.getFont());
        for (int i = 0, j = 1;; j++) {
            if (fontMetrics.charsWidth(chars, i, j) < snArea.getWidth()) {
                if (i + j < chars.length) {
                    continue;
                }
                builder.append(chars, i, j);
                break;
            }
            builder.append(chars, i, j - 1).append("<br/>");
            i = j - 1;
            j = 0;
        }
        builder.append("</html>");
        return builder.toString();
    }
}
