package com.hwq.dataloom.framework.result;

import com.hwq.dataloom.framework.errorcode.ErrorCode;

/**
 * 返回工具类
 *
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
public class ResultUtils {

    /**
     * 成功
     * @return
     * @param <T> 无返回值
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(0, null, "ok");
    }


    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
