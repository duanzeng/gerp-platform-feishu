package com.gerp.platform.feishu.model.entity.group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 飞书绑定表
 * </p>
 *
 * @author duanzengqiang
 * @since 2025-12-31
 */
@Getter
@Setter
@TableName("e_feishu_bind")
@ApiModel(value = "FeishuBind对象", description = "飞书绑定表")
public class FeishuBind implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("app_id")
    @TableField("app_id")
    private String appId;

    @ApiModelProperty("app_key")
    @TableField("app_key")
    private String appKey;

    @ApiModelProperty("飞书id")
    @TableField("base_user_id")
    private String baseUserId;

    @ApiModelProperty("飞书昵称")
    @TableField("base_user_name")
    private String baseUserName;

    @ApiModelProperty("创建人")
    @TableField("create_by")
    private Long createBy;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("最后修改人")
    @TableField("update_by")
    private Long updateBy;

    @ApiModelProperty("最后修改时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
