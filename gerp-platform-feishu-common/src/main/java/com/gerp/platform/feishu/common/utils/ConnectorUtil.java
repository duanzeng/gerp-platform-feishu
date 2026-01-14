package com.gerp.platform.feishu.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gerp.platform.feishu.common.config.TargetField;

import java.util.*;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/11 14:22
 */
public class ConnectorUtil {
    // Swagger类型 -> 目标字段类型映射
    private static final Map<String, Integer> TYPE_MAPPING = new HashMap<>();
    // 枚举/特殊字段扩展属性配置（仅保留格式类配置，名称不再硬编码）
    private static final Map<String, JSONObject> SPECIAL_PROPERTY_MAP = new HashMap<>();


    static {
        // 1. 初始化类型映射（核心规则不变）
        TYPE_MAPPING.put("string", 1);        // 多行文本
        TYPE_MAPPING.put("integer", 2);       // 数字
        TYPE_MAPPING.put("number", 2);        // 数字
        TYPE_MAPPING.put("array", 4);        // 多选
        TYPE_MAPPING.put("boolean", 7);       // 复选框
        TYPE_MAPPING.put("date", 1);          // 日期
        TYPE_MAPPING.put("date-time", 1);     // 日期
        TYPE_MAPPING.put("url", 10);          // 超链接
        TYPE_MAPPING.put("currency", 8);      // 货币（自定义类型）

        // 2. 初始化特殊字段扩展属性（仅格式/规则，无名称）
        // 日期格式配置（通用）
//        JSONObject dateProperty = new JSONObject();
//        dateProperty.put("formatter", "yyyy-MM-dd HH:mm");
//        SPECIAL_PROPERTY_MAP.put("date-time", dateProperty);
//        SPECIAL_PROPERTY_MAP.put("date", dateProperty);

        // 货币字段配置（通用）
        JSONObject currencyProperty = new JSONObject();
        currencyProperty.put("currencyCode", "CNY");
        /**
         * formatter 默认值保留2位小数，可选值：
         * "#,##0":      整数(千分位),
         * "#,##0.0":    保留1位小数(千分位),
         * "#,##0.00":   保留2位小数(千分位),
         * "#,##0.000":  保留3位小数(千分位),
         * "#,##0.0000": 保留4位小数(千分位),
         */
        currencyProperty.put("formatter", "#,##0.00");
        SPECIAL_PROPERTY_MAP.put("currency", currencyProperty);

        // 数字字段非负配置（通用）
        JSONObject numberProperty = new JSONObject();
        numberProperty.put("formatter", "0.00");
        SPECIAL_PROPERTY_MAP.put("number", numberProperty);

        // 数字字段非负配置（通用）
        JSONObject integerProperty = new JSONObject();
        integerProperty.put("formatter", "0");
        SPECIAL_PROPERTY_MAP.put("integer", integerProperty);
    }

    /**
     * 读取并解析Swagger JSON文件（指定节点版本）
     * @param jsonContent JSON内容
     * @param jsonPath 指定的JSON节点路径（如: #/components/schemas/User）
     * @return 解析后的目标字段列表
     * @throws Exception 读取/解析异常
     */
    public static List<TargetField> parseSwaggerJson(String jsonContent, String jsonPath) throws Exception {
        JSONObject swaggerRoot = JSON.parseObject(jsonContent);

        // 如果指定了节点路径，则直接解析该节点
        if (StrUtil.isNotBlank(jsonPath)) {
            JSONObject targetSchema = getSchemaByPath(swaggerRoot, jsonPath);
            if (targetSchema == null) {
                throw new RuntimeException("未找到指定路径的Schema: " + jsonPath);
            }

            Map<String, JSONObject> schemaCache = buildSchemaCache(swaggerRoot);
            List<TargetField> targetFields = new ArrayList<>();
            parseSchemaWithRef(targetSchema, "", schemaCache, targetFields);
            return targetFields;
        }

        // 否则使用原有逻辑
        return parseSwaggerJson(jsonContent);
    }

    /**
     * 根据路径获取Schema节点
     * @param swaggerRoot Swagger根节点
     * @param jsonPath JSON路径（如: #/components/schemas/User）
     * @return 对应的Schema对象
     */
    private static JSONObject getSchemaByPath(JSONObject swaggerRoot, String jsonPath) {
        if (jsonPath == null || !jsonPath.startsWith("#/")) {
            return null;
        }

        String[] paths = jsonPath.substring(2).split("/");
        JSONObject current = swaggerRoot;

        for (String path : paths) {
            if (current == null || !current.containsKey(path)) {
                return null;
            }
            current = current.getJSONObject(path);
        }

        return current;
    }




    /**
     * 读取并解析Swagger JSON文件（核心入口）
     * @param jsonContent JSON内容
     * @return 解析后的目标字段列表
     */
    public static List<TargetField> parseSwaggerJson(String jsonContent)  {
        // 1. 读取JSON文件内容
//        String jsonContent = FileUtil.readString(jsonFilePath, StandardCharsets.UTF_8);
        JSONObject swaggerRoot = JSON.parseObject(jsonContent);

        // 2. 缓存所有components/schemas（用于解析$ref）
        Map<String, JSONObject> schemaCache = buildSchemaCache(swaggerRoot);

        // 3. 获取根响应Schema
        JSONObject rootSchema = getResponseSchema(swaggerRoot);
        if (rootSchema == null) {
            throw new RuntimeException("未找到响应Schema，请检查Swagger结构");
        }

        // 4. 递归解析Schema（含$ref）
        List<TargetField> targetFields = new ArrayList<>();
        parseSchemaWithRef(rootSchema, "", schemaCache, targetFields);

        return targetFields;
    }

    /**
     * 解析$ref引用，返回真实的Schema对象
     * @param refStr $ref值（如#/components/schemas/Product）
     * @param schemaCache Schema缓存
     * @return 引用指向的真实Schema
     */
    private static JSONObject resolveRef(String refStr, Map<String, JSONObject> schemaCache) {
        if (refStr == null || !refStr.startsWith("#/")) {
            return null;
        }

        // 拆分ref路径：#/components/schemas/Product → ["components","schemas","Product"]
        String[] refPaths = refStr.substring(2).split("/");
        if (refPaths.length < 3 || !"components".equals(refPaths[0]) || !"schemas".equals(refPaths[1])) {
            return null;
        }

        // 取模型名（如Product），从缓存中获取Schema
        String schemaName = refPaths[2];
        return schemaCache.getOrDefault(schemaName, null);
    }

    /**
     * 递归解析Schema（支持$ref、嵌套对象、数组）
     * @param schema 当前Schema（可能是引用/对象/数组）
     * @param parentFieldID 父字段ID（嵌套拼接）
     * @param schemaCache Schema缓存（解析$ref用）
     * @param targetFields 目标字段列表
     */
    private static void parseSchemaWithRef(JSONObject schema, String parentFieldID,
                                           Map<String, JSONObject> schemaCache, List<TargetField> targetFields) {
        // 第一步：如果是$ref，先解析为真实Schema
        if (schema.containsKey("$ref")) {
            String refStr = schema.getString("$ref");
            JSONObject realSchema = resolveRef(refStr, schemaCache);
            if (realSchema != null) {
                schema = realSchema; // 替换为真实Schema继续解析
            } else {
                System.err.println("无法解析$ref：" + refStr + "，跳过该字段");
                return;
            }
        }

        // 第二步：处理数组类型
        if ("array".equals(schema.getString("type"))) {
            JSONObject itemsSchema = schema.getJSONObject("items");
            if (itemsSchema != null && itemsSchema.get("type")!=null) {
                if(!StrUtil.equalsIgnoreCase("string", Convert.toStr(itemsSchema.get("type")) )) {
                    parseSchemaWithRef(itemsSchema, parentFieldID, schemaCache, targetFields);
                }else{
                    TargetField targetField = buildTargetField(parentFieldID, schema);
                    targetFields.add(targetField);
                }
            }
            return;
        }

        // 第三步：处理对象类型
        if ("object".equals(schema.getString("type"))) {
            JSONObject properties = schema.getJSONObject("properties");
            if (properties == null || properties.isEmpty()) {
                return;
            }

            // 遍历对象的所有字段
            for (String fieldName : properties.keySet()) {
                JSONObject fieldSchema = properties.getJSONObject(fieldName);
                if(StrUtil.contains(parentFieldID,"_") && StrUtil.split(parentFieldID,"_").size()>2){
                    continue;
                }
                String currentFieldID = parentFieldID.isEmpty() ? fieldName : parentFieldID + "_" + fieldName;

                // 递归解析字段（可能是$ref/对象/数组）
                parseSchemaWithRef(fieldSchema, currentFieldID, schemaCache, targetFields);
            }
            return;
        }

        // 第四步：处理基础类型字段（非对象/数组/引用）
        if (!parentFieldID.isEmpty()) {
            TargetField targetField = buildTargetField(parentFieldID, schema);
            targetFields.add(targetField);
        }
    }

    /**
     * 构建Schema缓存：将#/components/schemas下的所有模型缓存，方便解析$ref
     */
    private static Map<String, JSONObject> buildSchemaCache(JSONObject swaggerRoot) {
        Map<String, JSONObject> schemaCache = new HashMap<>();
        if (!swaggerRoot.containsKey("components")) {
            return schemaCache;
        }

        JSONObject components = swaggerRoot.getJSONObject("components");
        if (!components.containsKey("schemas")) {
            return schemaCache;
        }

        JSONObject schemas = components.getJSONObject("schemas");
        // 遍历所有schemas，key为模型名，value为Schema
        for (String schemaName : schemas.keySet()) {
            schemaCache.put(schemaName, schemas.getJSONObject(schemaName));
        }
        return schemaCache;
    }

    /**
     * 适配不同Swagger结构，获取根响应Schema
     */
    private static JSONObject getResponseSchema(JSONObject swaggerRoot) {
        // 方式1：从paths中取第一个接口的200响应Schema
        if (swaggerRoot.containsKey("paths")) {
            JSONObject paths = swaggerRoot.getJSONObject("paths");
            for (String path : paths.keySet()) {
                JSONObject pathObj = paths.getJSONObject(path);
                for (String method : Arrays.asList("get", "post", "put", "delete")) {
                    if (pathObj.containsKey(method)) {
                        JSONObject responses = pathObj.getJSONObject(method).getJSONObject("responses");
                        if (responses.containsKey("200")) {
                            return responses.getJSONObject("200")
                                    .getJSONObject("content")
                                    .getJSONObject("application/json")
                                    .getJSONObject("schema");
                        }
                    }
                }
            }
        }

        // 方式2：直接取components/schemas中的第一个模型
//        if (swaggerRoot.containsKey("components") && swaggerRoot.getJSONObject("components").containsKey("schemas")) {
//            JSONObject schemas = swaggerRoot.getJSONObject("components").getJSONObject("schemas");
//            return schemas.entrySet().iterator().next().getValue();
//        }

        return null;
    }


    /**
     * 递归解析Schema字段（核心：自动解析中文名称）
     * @param schema Schema对象
     * @param parentFieldID 父字段ID（嵌套时拼接）
     * @param targetFields 目标字段列表
     */
    private static void parseSchema(JSONObject schema, String parentFieldID, List<TargetField> targetFields) {
        // 处理数组类型（items为数组元素的Schema）
        if ("array".equals(schema.getString("type"))) {
            JSONObject itemsSchema = schema.getJSONObject("items");
            parseSchema(itemsSchema, parentFieldID, targetFields);
            return;
        }

        // 处理对象类型（properties为字段列表）
        if ("object".equals(schema.getString("type"))) {
            JSONObject properties = schema.getJSONObject("properties");
            if (properties == null || properties.isEmpty()) {
                return;
            }

            // 遍历所有字段，自动解析每个字段的中文名称
            for (String fieldName : properties.keySet()) {
                JSONObject fieldSchema = properties.getJSONObject(fieldName);
                String currentFieldID = parentFieldID.isEmpty() ? fieldName : parentFieldID + "_" + fieldName;

                // 递归处理嵌套对象
                if ("object".equals(fieldSchema.getString("type"))) {
                    parseSchema(fieldSchema, currentFieldID, targetFields);
                    continue;
                }

                // 生成目标字段配置（核心：自动解析中文名称）
                TargetField targetField = buildTargetField(currentFieldID, fieldSchema);
                targetFields.add(targetField);
            }
            return;
        }

        // 基础类型字段（非对象/数组）
        if (!parentFieldID.isEmpty()) {
            TargetField targetField = buildTargetField(parentFieldID, schema);
            targetFields.add(targetField);
        }
    }


    /**
     * 构建目标字段配置（自动解析中文名称）
     */
    private static TargetField buildTargetField(String fieldID, JSONObject fieldSchema) {
        TargetField targetField = new TargetField();

        // 1. 字段ID
        targetField.setFieldID(fieldID);


        // 2. 中文名称（优先级：title > description > 字段名）
        String chineseName = getChineseFieldName(fieldSchema, fieldID);
        targetField.setFieldName(chineseName);

        // 3. 字段类型
        String swaggerType = fieldSchema.getString("type");
        String format = fieldSchema.getString("format");
        targetField.setFieldType(getTargetFieldType(swaggerType, format, fieldSchema));

        // 4. 是否索引列（仅id字段）
        targetField.setIsPrimary("id".equals(fieldID));

        // 5. 字段描述
        String description = fieldSchema.getString("description");
        targetField.setDescription(description == null || description.isEmpty() ? chineseName : description);

        // 6. 扩展属性
        targetField.setProperty(getSpecialProperty(swaggerType, format, fieldSchema));

        return targetField;
    }

    /**
     * 自动解析中文字段名
     */
    private static String getChineseFieldName(JSONObject fieldSchema, String fieldID) {
        // 优先级1：title
        String title = fieldSchema.getString("title");
        if (title != null && !title.isEmpty()) {
            return title.trim();
        }

        // 优先级2：description截取
        String desc = fieldSchema.getString("description");
        if (desc != null && !desc.isEmpty()) {
            String cleanDesc = desc.trim().replaceAll("[\\[\\]()（）:：,，]", "");
            return cleanDesc.length() > 20 ? cleanDesc.substring(0, 20) : cleanDesc;
        }

        // 优先级3：字段ID最后一段（驼峰转中文可选）
        String[] parts = fieldID.split("_");
        return camelToChinese(parts[parts.length - 1]);
    }

    /**
     * 驼峰命名转中文（可选扩展）
     */
    private static String camelToChinese(String camelStr) {
        if (camelStr == null || camelStr.isEmpty()) {
            return camelStr;
        }

        StringBuilder sb = new StringBuilder();
        // 常见英文词映射（可扩展）
        Map<String, String> wordMap = new HashMap<>();
        wordMap.put("id", "ID");
        wordMap.put("sku", "SKU");
        wordMap.put("name", "名称");
        wordMap.put("state", "状态");
        wordMap.put("url", "链接");
        wordMap.put("cost", "成本");
        wordMap.put("weight", "重量");
        wordMap.put("height", "高度");
        wordMap.put("length", "长度");
        wordMap.put("width", "宽度");
        wordMap.put("date", "时间");
        wordMap.put("type", "类型");
        wordMap.put("remark", "备注");

        // 拆分驼峰
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(" ");
            }
            sb.append(Character.toLowerCase(c));
        }

        // 替换英文词为中文
        String lowerStr = sb.toString();
        for (Map.Entry<String, String> entry : wordMap.entrySet()) {
            lowerStr = lowerStr.replace(entry.getKey(), entry.getValue());
        }

        return lowerStr.replace(" ", "");
    }

    /**
     * 匹配目标字段类型
     */
    private static Integer getTargetFieldType(String swaggerType, String format, JSONObject fieldSchema) {
        // 枚举类型强制单选
        if (fieldSchema.containsKey("enum")) {
            return 3;
        }

        // 特殊格式优先
        if (format != null && TYPE_MAPPING.containsKey(format)) {
            return TYPE_MAPPING.get(format);
        }

        // 基础类型映射
        return TYPE_MAPPING.getOrDefault(swaggerType, 1);
    }



    /**
     * 获取字段扩展属性（根据类型/格式匹配通用配置）
     */
    private static JSONObject getSpecialProperty(String swaggerType, String format, JSONObject fieldSchema) {
        JSONObject property = new JSONObject();

        // 1. 匹配格式相关配置（如date-time的格式、currency的精度）
        if (format != null && SPECIAL_PROPERTY_MAP.containsKey(format)) {
            property.putAll(SPECIAL_PROPERTY_MAP.get(format));
        } else if (swaggerType != null && SPECIAL_PROPERTY_MAP.containsKey(swaggerType)) {
            property.putAll(SPECIAL_PROPERTY_MAP.get(swaggerType));
        }

        // 2. 枚举字段自动生成选项配置（从Swagger的enum和enumDesc解析）
        if (fieldSchema.containsKey("enum")) {
            JSONArray enumValues = fieldSchema.getJSONArray("enum");
            JSONArray enumDescs = fieldSchema.getJSONArray("enumDesc"); // 假设Swagger有enumDesc字段描述枚举含义
            List<JSONObject> options = new ArrayList<>();

            for (int i = 0; i < enumValues.size(); i++) {
                JSONObject option = new JSONObject();
                option.put("value", enumValues.get(i));
                // 枚举描述优先，无则用值本身
                String label = enumDescs != null && enumDescs.size() > i ? enumDescs.getString(i) : enumValues.get(i).toString();
                option.put("label", label);
                options.add(option);
            }
            property.put("options", options);
        }

        return property;
    }
}
