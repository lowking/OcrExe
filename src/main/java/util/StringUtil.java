package main.java.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
        snArea.setText("处理中...");
        StringBuffer builder = new StringBuffer("<html>");
        char[] chars = str.toCharArray();
        FontMetrics fontMetrics = snArea.getFontMetrics(snArea.getFont());
        for (int beginIndex = 0, limit = 1;beginIndex < chars.length; limit++) {
            try {
                if (fontMetrics.charsWidth(chars, beginIndex, limit) < snArea.getWidth()) {
                    if (beginIndex + limit < chars.length) {
                        continue;
                    }
                    builder.append(chars, beginIndex, limit);
                    break;
                }
                builder.append(chars, beginIndex, limit - 1).append("<br/>");
                beginIndex += limit - 1;
                limit = 1;
            } catch (IndexOutOfBoundsException e) {
                builder.append(chars, beginIndex, chars.length - beginIndex);
                break;
            }
        }
        builder.append("</html>");
        return builder.toString();
    }

    public static void main(String[] args) {
        JLabel snArea = new JLabel("");
        snArea.setBounds(0, 53, 280, 280);
        snArea.setFont(new Font("",Font.PLAIN,18));
        snArea.setHorizontalAlignment(SwingConstants.CENTER);
        System.out.println(getWarpString(new JLabel(), "4200000399201908137880019422"));
    }
}
