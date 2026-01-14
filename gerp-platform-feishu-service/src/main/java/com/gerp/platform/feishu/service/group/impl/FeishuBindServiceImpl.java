package com.gerp.platform.feishu.service.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gerp.platform.feishu.model.dto.OpenApiBase;
import com.gerp.platform.feishu.model.entity.group.FeishuBind;
import com.gerp.platform.feishu.dao.group.FeishuBindMapper;
import com.gerp.platform.feishu.service.group.FeishuBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 飞书绑定表 服务实现类
 * </p>
 *
 * @author duanzengqiang
 * @since 2025-12-31
 */
@Service
public class FeishuBindServiceImpl extends ServiceImpl<FeishuBindMapper, FeishuBind> implements FeishuBindService {

    @Override
    public boolean bindFeishu(OpenApiBase reqDTO) {
        FeishuBind feishuBind = new FeishuBind();
        feishuBind.setAppId(reqDTO.getAppId());
        feishuBind.setAppKey(reqDTO.getAppKey());
        feishuBind.setBaseUserId(reqDTO.getBaseUserId());
        feishuBind.setBaseUserName(reqDTO.getBaseUserName());
        feishuBind.setCreateBy(1L);
        feishuBind.setUpdateBy(1L);

        // 使用 appId、appKey、baseUserId 作为条件进行更新或插入
        LambdaUpdateWrapper<FeishuBind> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FeishuBind::getAppId, reqDTO.getAppId())
                .eq(FeishuBind::getAppKey, reqDTO.getAppKey())
                .eq(FeishuBind::getBaseUserId, reqDTO.getBaseUserId())
                .set(FeishuBind::getBaseUserName, reqDTO.getBaseUserName());

        return saveOrUpdate(feishuBind, updateWrapper);
    }

    @Override
    public boolean changeUserName(OpenApiBase reqDTO) {
        LambdaUpdateWrapper<FeishuBind> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(FeishuBind::getBaseUserId, reqDTO.getBaseUserId())
                .set(FeishuBind::getBaseUserName, reqDTO.getBaseUserName());
        return update(updateWrapper);
    }

    @Override
    public FeishuBind get(String baseUserId) {
        LambdaQueryWrapper<FeishuBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FeishuBind::getBaseUserId, baseUserId)
                .orderByDesc(FeishuBind::getId)
                .last("limit 1");
        return getOne(queryWrapper);
    }
}
