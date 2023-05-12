package pycro.usts.common.config.exception;

import lombok.Data;
import pycro.usts.common.result.ResultCodeEnum;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 6:14 PM
 */
@Data
public class PycroException extends RuntimeException {
    private Integer code; // 状态码
    private String msg; // 状态信息

    /**
     * 通过状态码的错误消息创建
     *
     * @param code
     * @param msg
     */
    public PycroException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum
     */
    public PycroException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "PycroException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
