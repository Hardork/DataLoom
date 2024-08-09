package com.hwq.bi.utils;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 19:26
 * @Description:
 **/
public class MoneyUtils {
    // 将数据库中的钱转为真实的钱
    public static long getRealMoney(Long money) {
        if (money == null) {
            return 0;
        }
        return money / 100;
    }

    // 将真实的钱转为存入数据库的金钱格式
    public static long saveToDatabaseMoney(Long money) {
        if (money == null) {
            return 0;
        }
        return money * 100;
    }


}
