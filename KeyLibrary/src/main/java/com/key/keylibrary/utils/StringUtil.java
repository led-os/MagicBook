package com.key.keylibrary.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by key  on 2019/5/21
 */
public class StringUtil {


    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    public static boolean copy(Context context, String copyStr) {
        try {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText(null, copyStr);
            manager.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     *
     * @param phone
     * @param checkType 0 :phone, 1: fixPhone 2 : all
     * @return
     */
    public static boolean isPhone(String phone,int checkType) {
        if (TextUtils.isEmpty(phone))
            return false;
        boolean flag = false;
        String[] regexs = {"((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))",
                "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$"};

        if(checkType == 2){
           for(String regex : regexs){
               Pattern p = Pattern.compile(regex);
               Matcher m = p.matcher(phone);
               flag = m.matches();
               if(flag){
                   return  true;
               }
           }
        }else{
            String regex = regexs[checkType];
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            flag = m.matches();
        }

        return flag;
    }



    /**
     * 判断字符串的每个字符是否相等
     *
     * @param str
     * @return
     */
    public static boolean isCharEqual(String str) {
        return str.replace(str.charAt(0), ' ').trim().length() == 0;
    }

    /**
     * 确定字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     * @author lzf
     */
    public static Map<String, String> urlSplit(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            if (arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }


    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }


    /**
     * @param name
     * @return 中文  名字必须是中文
     */
    public static boolean isName(String name) {
        if (name.length() == 0) {
            return false;
        }

        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
            Matcher m = p.matcher(name);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     *   检查名字是否存在特殊字符串
     * @param name
     * @return
     */
    public static boolean isNameSp(String name) {
        if (name.length() == 0) {
            return false;
        }

        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^(?:[\\u4e00-\\u9fa5]+)(?:●[\\u4e00-\\u9fa5]+)*$|^[a-zA-Z0-9]+\\s?[\\.·\\-()a-zA-Z]*[a-zA-Z]+$");
            Matcher m = p.matcher(name);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 身份证规范
     *
     * @param IDCard
     * @return
     */
    public static boolean isIDCard(String IDCard) {
        if (IDCard != null) {
            String IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";
            return IDCard.matches(IDCardRegex);
        }
        return false;
    }


    public static boolean isIDCardOrPassport(String IDCard) {
        boolean isCard = false;
        boolean isHuz = false;
        boolean isJp = false;
        boolean isUs = false;
        boolean isCana = false;
        boolean isFran = false;
        boolean isXi = false;
        boolean isDe = false;
        boolean isSixNum = false;
        if (IDCard != null) {
            String IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";
            isCard = IDCard.matches(IDCardRegex);
        }

        //大陆
        if (IDCard != null) {
            String IDCardRegex = "^1[45][0-9]{7}|([P|p|S|s]\\d{7})|([S|s|G|g]\\d{8})|([Gg|Tt|Ss|Ll|Qq|Dd|Aa|Ff]\\d{8})|([H|h|M|m]\\d{8,10})$";
            isHuz = IDCard.matches(IDCardRegex);
        }

        //日本
        if (IDCard != null) {
            String IDCardRegex = "^[a-zA-Z]{2}\\d{7}$";
            isJp = IDCard.matches(IDCardRegex);
        }

        //美国，英国 ：\d{9}
        if (IDCard != null) {
            String IDCardRegex = "^\\d{9}";
            isUs = IDCard.matches(IDCardRegex);
        }

        if (IDCard != null) {
            String IDCardRegex = "^\\d{8}";
            isSixNum = IDCard.matches(IDCardRegex);
        }
        //加拿大：[a-zA-Z]{2}[0-9]{6}
        if (IDCard != null) {
            String IDCardRegex = "^[a-zA-Z]{2}\\d{6}$";
            isCana = IDCard.matches(IDCardRegex);
        }


        //法国：[0-9]{2}[a-zA-Z]{2}[0-9]{5}
        if (IDCard != null) {
            String IDCardRegex = "^[0-9]{2}[a-zA-Z]{2}[0-9]{5}$";
            isFran = IDCard.matches(IDCardRegex);
        }

        //西班牙：[a-zA-Z]{3}[0-9]{6}
        if (IDCard != null) {
            String IDCardRegex = "^[a-zA-Z]{3}\\d{6}$";
            isXi = IDCard.matches(IDCardRegex);
        }
        //德国：[a-zA-Z][a-zA-Z0-9]{7}[a-zA-Z]
        if (IDCard != null) {
            String IDCardRegex = "^[a-zA-Z][a-zA-Z0-9]{7}[a-zA-Z]$";
            isDe = IDCard.matches(IDCardRegex);
        }
        return isCard || isHuz || isJp || isUs || isCana || isFran || isXi || isDe || isSixNum;
    }


    /**
     * 判断忽略大小写情况下，字符串是否equals
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }


    /**
     * 判断字符串，手机号码隐藏中间四位
     *
     * @param phone
     * @return
     */

    public static String getSafePhone(String phone) {
        if (phone == null || phone.length() == 0) {
            return "";
        } else {
            String phoneNumber2 = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            return phoneNumber2;
        }
    }


    /**
     * 判断字符串，银行卡号隐藏中间八位
     *
     * @param bankCard
     * @return
     */

    public static String getSafeCardNum(String bankCard) {

        if (bankCard == null || bankCard.length() == 0) {
            return "";
        } else {
            int hideLength = 8;//替换位数
            int sIndex = bankCard.length() / 2 - hideLength / 2;
            String replaceSymbol = "*";
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < bankCard.length(); i++) {
                char number = bankCard.charAt(i);
                if (i >= sIndex - 1 && i < sIndex + hideLength) {
                    sBuilder.append(replaceSymbol);
                } else {
                    sBuilder.append(number);
                }
            }
            return sBuilder.toString();
        }
    }


    /**
     * 校验过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
     *
     * @param nonCheckCodeBankCard
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        int luh = luhmSum % 10;
        char luhmSub = (char) ((10 - luhmSum % 10) + '0');
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    /**
     * 保留两位小数
     *
     * @param d
     * @return
     */
    public static String m2(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        String len = ".00";

        if (df.format(d).equals(".00")) {
            return "0.00";
        } else if (df.format(d).length() == len.length()) {
            return "0" + df.format(d);
        } else {
            return df.format(d);
        }

    }


    /**
     * 保留两位小数
     *
     * @param d
     * @return
     */
    public static double m2D(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        String len = ".00";

        if (df.format(d).equals(".00")) {
            return Double.valueOf("0.00");
        } else if (df.format(d).length() == len.length()) {
            return Double.valueOf("0" + df.format(d));
        } else {
            return Double.valueOf(df.format(d));
        }

    }


    /**
     * 保留两位小数
     *
     * @param d
     * @return
     */
    public static double m2D(String d) {
        if (d.replace(" ", "").trim().isEmpty()) {
            return 0.00;
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            String len = ".00";

            Double aDouble = Double.valueOf(d);
            if (df.format(aDouble).equals(".00")) {
                return Double.valueOf("0.00");
            } else if (df.format(aDouble).length() == len.length()) {
                return Double.valueOf("0" + df.format(aDouble));
            } else {
                return Double.valueOf(df.format(aDouble));
            }
        }

    }


    public static String m2DS(String d) {
        if (d.replace(" ", "").trim().isEmpty()) {
            return "0.00";
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            String len = ".00";

            Double aDouble = Double.valueOf(d);
            if (df.format(aDouble).equals(".00")) {
                return "0.00";
            } else if (df.format(aDouble).length() == len.length()) {
                return "0" + df.format(aDouble);
            } else {
                return df.format(aDouble);
            }
        }
    }


    public static String Decimal2(String d) {
        if (d.replace(" ", "").trim().isEmpty()) {
            return "0.00";
        } else {
            BigDecimal bigDecimal = new BigDecimal(m2D(Double.valueOf(d)));
            return m2DS(bigDecimal.toString());
        }
    }


    /**
     * 特殊字符串的个数
     */
    public static int getConSpeCharacters(String string) {
        String s = string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*\\s*", "");
        return s.length();
    }


    /**
     * 大写字母的个数
     *
     * @param string
     * @return
     */
    public static int getBigLetter(String string) {
        int length = string.length();
        String s = string.replaceAll("[A-Z]*", "");
        int i = length - s.length();
        return i;
    }

    /**
     * 小写字母的个数
     *
     * @param string
     * @return
     */
    public static int getLetter(String string) {
        int length = string.length();
        String s = string.replaceAll("[a-z]*", "");
        int i = length - s.length();
        return length - s.length();
    }

    /**
     * 数字的个数
     *
     * @param string
     * @return
     */
    public static int getNum(String string) {
        int length = string.length();
        String s = string.replaceAll("[0-9]*", "");
        int i = length - s.length();
        return length - s.length();
    }





    /**
     * String数组转换为http后面参数
     *
     * @param params
     * @return
     */
    private static String paramsConvertUrl(Map<String, String> params) {
        StringBuilder urlParams = new StringBuilder("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlParams.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String urlParamsStr = urlParams.toString();
        return urlParamsStr.substring(0, urlParamsStr.length() - 1);
    }


    public static int floatToInteger(Float f) {
        String number = String.valueOf(f);
        if (number.isEmpty()) {
            return 0;
        } else {
            int i = number.indexOf(".");
            String substring = number.substring(0, i);
            if (Integer.valueOf(number.substring(i + 1, i + 2)) > 5) {
                return Integer.valueOf(substring) + 1;
            } else {
                return Integer.valueOf(substring);
            }

        }
    }


    /**
     *   获取文件的MD5值
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private static String nums[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    private static String pos_units[] = {"", "十", "百", "千"};

    private static String weight_units[] = {"", "万", "亿"};


    /**
     * 数字转汉字【新】
     *
     * @param num
     * @return
     */
    public static String numberToChinese(int num) {
        if (num == 0) {
            return "零";
        }

        int weigth = 0;
        String chinese = "";
        String chinese_section = "";
        boolean setZero = false;
        while (num > 0) {
            int section = num % 10000;
            if (setZero) {
                chinese = nums[0] + chinese;
            }
            chinese_section = sectionTrans(section);
            if (section != 0) {
                chinese_section = chinese_section + weight_units[weigth];
            }
            chinese = chinese_section + chinese;
            chinese_section = "";
            setZero = (section < 1000) && (section > 0);
            num = num / 10000;
            weigth++;
        }
        if ((chinese.length() == 2 || (chinese.length() == 3)) && chinese.contains("一十")) {
            chinese = chinese.substring(1, chinese.length());
        }
        if (chinese.indexOf("一十") == 0) {
            chinese = chinese.replaceFirst("一十", "十");
        }

        return chinese;
    }


    /**
     * 将每段数字转汉子
     *
     * @param section
     * @return
     */
    public static String sectionTrans(int section) {
        StringBuilder section_chinese = new StringBuilder();
        int pos = 0;
        boolean zero = true;
        while (section > 0) {
            int v = section % 10;
            if (v == 0) {
                if (!zero) {
                    zero = true;
                    section_chinese.insert(0, nums[0]);
                }
            } else {
                zero = false;
                section_chinese.insert(0, pos_units[pos]);
                section_chinese.insert(0, nums[v]);
            }
            pos++;
            section = section / 10;
        }

        return section_chinese.toString();
    }
}
