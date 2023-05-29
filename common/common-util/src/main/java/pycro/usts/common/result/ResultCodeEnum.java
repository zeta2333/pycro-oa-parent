package pycro.usts.common.result;

import lombok.Getter;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 10:29 AM
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    LOGIN_ERROR(208, "认证失败");

    private Integer code;
    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
