package com.gerp.platform.feishu.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 14:38
 */
@NoArgsConstructor
@Data
public class PageDataRspDTO implements Serializable {

    private Integer total;
    private Integer pagesize;
    private Integer page;

    public boolean hasMore(){
        return total > page * pagesize;
    }

    public Integer getNextPageToken(){
        return page + 1;
    }


}
