package com.gerp.platform.feishu.service.group;

import com.gerp.platform.feishu.model.dto.OpenApiBase;
import com.gerp.platform.feishu.model.entity.group.FeishuBind;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 飞书绑定表 服务类
 * </p>
 *
 * @author duanzengqiang
 * @since 2025-12-31
 */
public interface FeishuBindService extends IService<FeishuBind> {

    boolean bindFeishu(OpenApiBase reqDTO);

    boolean changeUserName(OpenApiBase reqDTO);

    FeishuBind get(String baseUserId);
}
