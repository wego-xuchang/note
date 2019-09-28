package com.hashtech.idata.common.monitor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xuchang
 */
public class CommonUtils {
    private static final String CAPITAL_AND_LOWER_CASE_LETTER = "[A-z]+-?[A-z]+";
    private static final String BEGIN_HTTP_LETTER = "http([\\d\\D])*";
    private static final String SPECIAL_CHARACTER = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    /**
     * 校验大小写字符
     * @param aName
     * @return
     */
    public static boolean validateName(String aName){
        return aName.matches(CAPITAL_AND_LOWER_CASE_LETTER);
    }

    /**
     * 校验HTTP开头的字符
     * @param aName
     * @return
     */
    public static boolean validateHost(String aName){
        return aName.matches(BEGIN_HTTP_LETTER);
    }

    /**
     * 过滤特殊字符
     * @param str
     * @return
     */
    public static String filterCharacter(String str){
        Pattern p = Pattern.compile(SPECIAL_CHARACTER);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断字符串是否存有特殊字符
     * @param str
     * @return
     */
    public static boolean checkCharacter(String str){
        Pattern p = Pattern.compile(SPECIAL_CHARACTER);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static void main(String[] args) {
        boolean flag = CommonUtils.checkCharacter(" dasd");
        System.out.println(flag);
    }
}
