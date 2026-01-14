package com.gerp.platform.feishu.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 14:33
 */
@NoArgsConstructor
@Data
public class TableRecordRspDTO implements Serializable {

    private String nextPageToken;
    private Boolean hasMore;
    private List<RecordsBean> records;

    @NoArgsConstructor
    @Data
    public static class RecordsBean {
        private String primaryID;
        private Map<String,Object> data;
    }
}
