package cn.lzs.ymcc.util;

import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.util.*;

/**
 * @author 
 * @date 
 */
public class StrUtils {

    //把参数转成map 如：  aa=bb&cc=xx
    //扩展参数
    public static Map<String,Object> params2Map(String passback_params){
        if(!StringUtils.hasLength(passback_params))return null;

        Map<String,Object> map = new HashMap<>();
        try{
            ///aaaa=bbb&ccc=1,2
            if(passback_params.indexOf("&") < 0){
                String[] params = passback_params.split("=");
                map.put(params[0],params[1]);
            }else{
                String[] params = passback_params.split("&");
                for (String param : params) {
                    String[] paramKeyValue = param.split("=");
                    map.put(paramKeyValue[0],paramKeyValue[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 把逗号分隔的字符串转换字符串数组
     *
     * @param str
     * @return
     */
    public static String[] splitStr2StrArr(String str,String split) {
        if (str != null && !str.equals("")) {
            return str.split(split);
        }
        return null;
    }


    /**
     * 把逗号分隔字符串转换List的Long
     *
     * @param str
     * @return
     */
    public static List<Long> splitStr2LongArr(String str) {
        String[] strings = splitStr2StrArr(str,",");
        if (strings == null) return null;

        List<Long> result = new ArrayList<>();
        for (String string : strings) {
            result.add(Long.parseLong(string));
        }

        return result;
    }
    /**
     * 把逗号分隔字符串转换List的Long
     *
     * @param str
     * @return
     */
    public static List<Long> splitStr2LongArr(String str,String split) {
        String[] strings = splitStr2StrArr(str,split);
        if (strings == null) return null;

        List<Long> result = new ArrayList<>();
        for (String string : strings) {
            result.add(Long.parseLong(string));
        }

        return result;
    }

    public static String getRandomString(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();

    }

    public static String getComplexRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String convertPropertiesToHtml(String properties){
        //1:容量:6:32GB_4:样式:12:塑料壳
        StringBuilder sBuilder = new StringBuilder();
        String[] propArr = properties.split("_");
        for (String props : propArr) {
            String[] valueArr = props.split(":");
            sBuilder.append(valueArr[1]).append(":").append(valueArr[3]).append("<br>");
        }
        return sBuilder.toString();
    }

}
