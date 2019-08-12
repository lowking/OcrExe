package main.java.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
