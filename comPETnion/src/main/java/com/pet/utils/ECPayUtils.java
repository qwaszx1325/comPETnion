package com.pet.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

public class ECPayUtils {

    // 验证 CheckMacValue 的方法
    public static boolean verifyCheckMacValue(Map<String, String> params, String hashKey, String hashIV) {
        // 排除 CheckMacValue 本身，按参数名排序并拼接字符串
        String rawData = params.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("CheckMacValue"))
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));

        rawData = "HashKey=" + hashKey + "&" + rawData + "&HashIV=" + hashIV;

        // 计算 SHA256 散列值并转换为大写
        String calculatedMacValue = hash(rawData).toUpperCase();

        // 比较计算值与返回的 CheckMacValue
        return calculatedMacValue.equals(params.get("CheckMacValue"));
    }

    // 计算 SHA256 散列值的方法
    private static String hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
