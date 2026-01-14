package com.gerp.platform.feishu.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/10 16:41
 */
@Data
public class BaseResponse<T>  implements Serializable {

    @ApiModelProperty("返回码 0-成功")
    private Integer code;

    @ApiModelProperty("返回信息")
    private MsgBean msg;

    @Data
    public static class MsgBean{
        private String zh;
        private String en;
    }

    @ApiModelProperty("返回数据")
    private T data;

    public BaseResponse(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(Integer code, T data, MsgBean msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse(0, data);
    }


    public static <T> BaseResponse<T> error(MsgBean message) {
        return new BaseResponse(1, (Object)null, message);
    }


}
