package com.gerp.platform.feishu;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

    private static final String tablePrefix = "e_";//生成实体时去掉表前缀

    @Test
    public void testCodeGen() {
        //代码生成类所在模块名称
        //TODO 修改成自己代码生成类所在模块名称
        String codeGenModelName = "gerp-platform-feishu-server";
        //获取项目目录
        String parentDir = System.getProperty("user.dir").replace("\\"+codeGenModelName, "");
        System.out.println(parentDir);
        //微服务通用包路径名称，例: gerp-platform-browser 微服务项目中通用的路径名为demo
        //TODO 修改成自己微服务通用包路径名称
        String microservicePathName = "platform-feishu";
        String microservicePath="platform/feishu";
        String parentPackageName="platform.feishu";
        //TODO 修改成自己的表名
        String tableName="e_feishu_bind";
        //模块名称
        //TODO 修改自己模块名
        String modelName = "group";
        //获取pojo目录
        String pojoDir = parentDir + "/gerp-"+microservicePathName+"-model/src/main/java/com/gerp/"+microservicePath+"/model/entity/" + modelName + "/";
        //获取mapper目录
        String mapperDir = parentDir + "/gerp-"+microservicePathName+"-dao/src/main/java/com/gerp/"+microservicePath+"/dao/" + modelName + "/";
        String mapperXmlDir = parentDir + "/gerp-"+microservicePathName+"-dao/src/main/resources/mybatis-mapper/" + modelName + "/";
        //获取service目录
        String serviceDir = parentDir + "/gerp-"+microservicePathName+"-service/src/main/java/com/gerp/"+microservicePath+"/service/" + modelName + "/";
        String serviceImplDir = parentDir + "/gerp-"+microservicePathName+"-service/src/main/java/com/gerp/"+microservicePath+"/service/" + modelName + "/impl/";
        //获取controller目录
        String controllerDir = parentDir + "/gerp-"+microservicePathName+"-web/src/main/java/com/gerp/"+microservicePath+"/web/controller/" + modelName + "/";

        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.entity, pojoDir);
        pathInfo.put(OutputFile.mapper, mapperDir);
        pathInfo.put(OutputFile.mapperXml, mapperXmlDir);
        pathInfo.put(OutputFile.service, serviceDir);
        pathInfo.put(OutputFile.serviceImpl, serviceImplDir);
        //controller不进行生成
//        pathInfo.put(OutputFile.controller, controllerDir);
        FastAutoGenerator.create(
                "jdbc:mysql://192.168.250.115:23313/gerp_browser?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true",
                "dev_erp",
                "61vyw9NkPOUe")
                .globalConfig(builder -> {
                    //TODO 修改作者信息
                    builder.author("duanzengqiang") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .dateType(DateType.ONLY_DATE)
                            .fileOverride()
                            .disableOpenDir(); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.gerp."+parentPackageName) // 设置父包名
                            .entity("model.entity." + modelName)
                            .service("service." + modelName)
                            .serviceImpl("service." + modelName + ".impl")
                            .controller("web.controller." + modelName)
                            .mapper("dao." + modelName)
                            .pathInfo(pathInfo);
                })
                .strategyConfig(builder -> {
                    // 设置需要生成的表名
                    //TODO 修改表名
                    builder.addInclude(tableName)
                            //表前缀
                            .addTablePrefix(tablePrefix)
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .controllerBuilder()
                            // 开启rest
                            .enableRestStyle()
                            .mapperBuilder()
                            .enableBaseResultMap();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}
