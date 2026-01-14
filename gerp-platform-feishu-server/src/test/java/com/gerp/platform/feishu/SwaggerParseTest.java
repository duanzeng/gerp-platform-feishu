package com.gerp.platform.feishu;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.gerp.platform.feishu.common.config.TargetField;
import com.gerp.platform.feishu.common.utils.ConnectorUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/11 14:11
 */
public class SwaggerParseTest {

    @SneakyThrows
    @Test
    public void schemaTest(){
//        String swagger = FileUtil.readUtf8String("./productSwagger.json");
        String swagger = FileUtil.readUtf8String("./purchaseSrm.json");
//        String swagger = FileUtil.readUtf8String("./fulfillmentShipment.json");
        List<TargetField> targetFields = ConnectorUtil.parseSwaggerJson(swagger);
        List<TargetField> tf=new ArrayList<>();
        targetFields.forEach(c->{
            if(c.getFieldID().startsWith("data_rows_")){
                c.setFieldID(c.getFieldID().replace("data_rows_",""));
                if(StrUtil.equals("id", c.getFieldID())){
                    c.setIsPrimary(true);
                }
                tf.add(c);
            }
        });
        System.out.println(tf);
    }
}
