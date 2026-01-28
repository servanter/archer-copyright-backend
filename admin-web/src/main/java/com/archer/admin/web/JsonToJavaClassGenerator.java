package com.archer.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.bridge.Message;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class JsonToJavaClassGenerator {


    // 用于传入包名前缀的参数类
    public static class GenerationParams {
        String parentPackage;
        String basePackagePrefix;
        String webPackagePrefix;

        public GenerationParams(String parentPackage) {
            this.parentPackage = parentPackage;
            this.basePackagePrefix = parentPackage + ".base";
            this.webPackagePrefix = parentPackage + ".web";
        }
    }

    @Data
    public static class Struct {
        private String name;
        private String tableName;
        private String desc;
        private List<StructAttr> fields;

        public String getLowerName() {
            return name.toLowerCase();
        }
    }

    @Data
    public static class StructAttr {
        private String name;
        private String type;
        private boolean query;
        private boolean primary;
        private String desc;
        private String fullDesc;
        private boolean enumClass;
        private String ref;

        private String timeFormat;

        private String file;

        public String getRefClassName() {
            List<String> strings = Splitter.on(".").splitToList(ref);
            if (strings.size() > 0) {
                return strings.get(strings.size() - 1).replace("@", "");
            }
            return "";
        }

        public boolean isDigit() {
            if (type.toLowerCase().equals("integer")) {
                return true;
            }

            return false;
        }

        public boolean isDateClass() {
            return this.getType().equals("LocalDateTime") || this.getType().equals("LocalDate");
        }

        public String dateOrTime() {
            return this.getTimeFormat().equals("yyyy-MM-dd") ? "date" : "datetime";
        }

        public boolean isFile() {
            return StringUtils.isNotBlank(this.file);
        }

        public boolean isFileImage() {
            return StringUtils.isNotBlank(this.file) && this.file.equals("image");
        }

        private List<RefAttr> refAttrs;
    }

    @Data
    public static class RefAttr {
        private int value;
        private String desc;
        private String label;
    }

    // 实体类模板
    private static final String ENTITY_CLASS_TEMPLATE =
            "package {0}.entities;\n\n" +
                    "import com.baomidou.mybatisplus.annotation.IdType;\n" +
                    "import com.baomidou.mybatisplus.annotation.TableId;\n" +
                    "import com.baomidou.mybatisplus.annotation.TableName;\n" +
                    "import com.fasterxml.jackson.annotation.JsonFormat;\n" +
                    "import java.time.LocalDateTime;\n" +
                    "import java.time.LocalDate;\n" +
                    "import lombok.Data;\n\n" +
                    "@Data\n" +
                    "@TableName(\"{1}\")\n" +
                    "public class {2} '{'\n" +
                    "{3}\n" +
                    "'}'\n";

    // 实体类字段模板
    private static final String FIELD_TEMPLATE =
            "        {0}\n        private {1} {2};";

    // Mapper接口模板
    private static final String MAPPER_TEMPLATE =
            "package {0}.repository;\n\n" +
                    "import {0}.entities.{1};\n" +
                    "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n\n" +
                    "public interface {1}Mapper extends BaseMapper<{1}> '{'\n" +
                    "'}'\n";

    // Service层接口模板
    private static final String SERVICE_INTERFACE_TEMPLATE =
            "package {0}.service;\n\n" +
                    "import {0}.entities.{1};\n" +
                    "import com.baomidou.mybatisplus.extension.service.IService;\n" +
                    "import org.springframework.stereotype.Repository;\n\n" +
                    "@Repository\n" +
                    "public interface {1}Service extends IService<{1}> '{'\n" +
                    "'}'\n";

    // Service层接口实现类模板
    private static final String SERVICE_IMPL_TEMPLATE =
            "package {0}.service.impl;\n\n" +
                    "import {0}.entities.{1};\n" +
                    "import {0}.repository.{1}Mapper;\n" +
                    "import {0}.service.{1}Service;\n" +
                    "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n" +
                    "import org.springframework.stereotype.Service;\n\n" +
                    "@Service\n" +
                    "public class {1}ServiceImpl extends ServiceImpl<{1}Mapper, {1}> implements {1}Service '{'\n" +
                    "'}'\n";

    // WEB层 Entity实体类模板
    private static final String WEB_ENTITY_TEMPLATE =
            "package {0}.web.{1}.entities;\n" +
                    "\n" +
                    "import {0}.base.common.Page.PageReq;\n" +
                    "import {0}.base.common.Page.PageRes;\n" +
                    "import {0}.base.entities.{2};\n" +
                    "import com.archer.admin.web.common.ValidEnum;\n" +
                    "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                    "import com.fasterxml.jackson.annotation.JsonFormat;\n" +
                    "import java.time.LocalDateTime;\n" +
                    "import java.time.LocalDate;\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "import lombok.Builder;\n" +
                    "import lombok.Data;\n" +
                    "import lombok.Getter;\n" +
                    "import lombok.Builder.Default;\n" +
                    "import lombok.experimental.SuperBuilder;\n\n" +
                    "public class {2}Transform '{'\n {3} \n'}'";

    // WEB层 Entity实体类Res类模板
    private static final String WEB_ENTITY_INNER_RES_TEMPLATE =
            "\n    @Getter\n" +
                    "    @Builder\n" +
                    "    @JsonInclude(JsonInclude.Include.NON_NULL)\n" +
                    "    public static class {0}Res '{'\n{1}\n'    }'";

    // WEB层 Entity实体类Res类parse模板
    private static final String WEB_ENTITY_INNER_RES_BUILDER_TEMPLATE =
            "\n    public static {0}Res parseRes({1} {2}) '{'\n{3}\n    '}'";

    // WEB层 Entity实体类Req查询类模板
    private static final String WEB_ENTITY_INNER_QUERY_REQ_TEMPLATE =
            "\n    @Data\n" +
                    "public static class {0}QueryReq extends PageReq '{'\n{1}\n    '}'";

    // WEB层 Entity实体类Res查询类模板
    private static final String WEB_ENTITY_INNER_QUERY_RES_TEMPLATE =
            "\n    @SuperBuilder\n" +
                    "@Getter\n" +
                    "public static class {0}QueryRes extends PageRes '{'\n    private List<{0}Res> list;\n  {1}\n  '}'";


    // WEB层 Service类类模板
    private static final String WEB_SERVICE_TEMPLATE =
            "package {2}.web.{0}.service;\n"
                    + "\n"
                    + "import {2}.base.entities.{1};\n"
                    + "import {2}.base.service.{1}Service;\n"
                    + "import {2}.web.component.Result;\n"
                    + "import {2}.web.{0}.entities.{1}Transform.{1}QueryReq;\n"
                    + "import {2}.web.{0}.entities.{1}Transform.{1}QueryRes;\n"
                    + "import {2}.web.{0}.entities.{1}Transform.{1}Res;\n"
                    + "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n"
                    + "import java.util.List;\n"
                    + "import java.util.stream.Collectors;\n"
                    + "import javax.annotation.Resource;\n"
                    + "import org.apache.commons.lang3.StringUtils;\n"
                    + "import org.springframework.stereotype.Component;\n"
                    + "\n"
                    + "@Component\n"
                    + "public class Biz{1}Service '{'\n"
                    + "\n"
                    + "    @Resource\n"
                    + "    private {1}Service {0}Service;\n"
                    + "\n"
                    + "    public {1}Res query(int {0}Id) '{'\n"
                    + "        return {1}Res.parseRes({0}Service.getById({0}Id));\n"
                    + "    }\n"
                    + "\n"
                    + "    public Result modify({1} {0}) '{'\n"
                    + "        return {0}Service.updateById({0}) ? Result.success() : Result.error();\n"
                    + "    '}'\n"
                    + "\n"
                    + "    public {1}QueryRes list({1}QueryReq {0}QueryReq) '{'\n"
                    + "        Page<{1}> page = {0}Service.lambdaQuery()\n{3}"
                    + "\n"
                    + "        List<{1}Res> list = page.getRecords().stream().map({1}Res::parseRes).collect(Collectors.toList());\n"
                    + "        return {1}QueryRes.builder()\n"
                    + "                .total(page.getTotal())\n"
                    + "                .totalPage(page.getPages())\n"
                    + "                .list(list)\n"
                    + "                .build();\n"
                    + "    '}'\n"
                    + "\n"
                    + "    public Result remove(int {0}Id) '{'\n"
                    + "        {1} {0} = new {1}();\n"
                    + "        {0}.setId({0}Id);\n"
                    + "        {0}.setValid(-1);\n"
                    + "        return {0}Service.updateById({0}) ? Result.success() : Result.error();\n"
                    + "    '}'\n"
                    + "\n"
                    + "    public Result save({1} {0}) '{'\n"
                    + "        return {0}Service.save({0}) ? Result.success() : Result.error();\n"
                    + "    '}'\n"
                    + "}\n";


    // WEB层 Service类 Condition模板
    private static final String WEB_SERVICE_QUERY_CONDITION_TEMPLATE = ""
            + "                {0}"
            + "                .page(new Page<>({1}QueryReq.getPageNo(), {1}QueryReq.getPageSize()));\n";

    // WEB层 Controller类模板
    private static final String WEB_CONTROLLER_TEMPLATE = "package {0}.web.{1}.controller;\n"
            + "\n"
            + "import {0}.base.entities.{2};\n"
            + "import {0}.web.component.Result;\n"
            + "import {0}.web.{1}.entities.{2}Transform.{2}QueryReq;\n"
            + "import {0}.web.{1}.entities.{2}Transform.{2}QueryRes;\n"
            + "import {0}.web.{1}.entities.{2}Transform.{2}Res;\n"
            + "import {0}.web.{1}.service.Biz{2}Service;\n"
            + "import javax.annotation.Resource;\n"
            + "import org.springframework.web.bind.annotation.PathVariable;\n"
            + "import org.springframework.web.bind.annotation.RequestBody;\n"
            + "import org.springframework.web.bind.annotation.RequestMapping;\n"
            + "import org.springframework.web.bind.annotation.RequestMethod;\n"
            + "import org.springframework.web.bind.annotation.RestController;\n"
            + "import com.archer.admin.web.component.ResponseResultBody;\n"
            + "import com.archer.admin.web.component.WebContext;\n"
            + "\n"
            + "@RequestMapping(\"/{1}\")\n"
            + "@RestController\n"
            + "@ResponseResultBody\n"
            + "public class {2}Controller '{'\n"
            + "\n"
            + "    @Resource\n"
            + "    private Biz{2}Service biz{2}Service;\n"
            + "\n"
            + "    @RequestMapping(\"/detail/'{'{1}Id'}'\")\n"
            + "    public {2}Res detail(WebContext webContext, @PathVariable(\"{1}Id\") int {1}Id) '{'\n"
            + "        return biz{2}Service.query({1}Id);\n"
            + "    '}'\n"
            + "\n"
            + "    @RequestMapping(value = \"/modify\", method = RequestMethod.POST)\n"
            + "    public Result modify(WebContext webContext, @RequestBody {2} {1}) '{'\n"
            + "        return biz{2}Service.modify({1});\n"
            + "    '}'\n"
            + "\n"
            + "    @RequestMapping(\"/list\")\n"
            + "    public {2}QueryRes list(WebContext webContext, {2}QueryReq {1}QueryReq) '{'\n"
            + "        return biz{2}Service.list({1}QueryReq);\n"
            + "    '}'\n"
            + "\n"
            + "    @RequestMapping(\"/remove\")\n"
            + "    public Result remove(WebContext webContext, int id) '{'\n"
            + "        return biz{2}Service.remove(id);\n"
            + "    '}'\n"
            + "\n"
            + "    @RequestMapping(value = \"/add\", method = RequestMethod.POST)\n"
            + "    public Result add(WebContext webContext, @RequestBody {2} {1}) '{'\n"
            + "        return biz{2}Service.save({1});\n"
            + "    '}'\n"
            + "}\n";

    private static final String SQL_TEMPLATE = "CREATE TABLE `{0}` (\n {1}" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";


    private static final String ENUM_TEMPLATE = "package {0}.{1}.entities;\n"
            + "\n"
            + "import java.util.ArrayList;\n"
            + "import java.util.HashMap;\n"
            + "import java.util.List;\n"
            + "import java.util.Map;\n"
            + "import java.util.Arrays;\n"
            + "import lombok.Getter;\n"
            + "import java.util.stream.Collectors;\n"
            + "\n"
            + "@Getter\n"
            + "public enum {2} '{'\n"
            + "\n"
            + "    EMPTY(0, \"全部\"),\n"
            + "    {3}"
            + "    ;\n"
            + "\n"
            + "    private int value;\n"
            + "    private String label;\n"
            + "    public static List<Map<String, Object>> TOTALS = totals();\n"
            + "    private {2}(int value, String label) '{'\n"
            + "        this.value = value;\n"
            + "        this.label = label;\n"
            + "    '}'\n"
            + "\n"
            + "    public static {2} of(int value) '{'\n"
            + "        return Arrays.stream({2}.values())\n"
            + "                .filter(e -> e.getValue() == value)\n"
            + "                .findAny()\n"
            + "                .orElse({2}.EMPTY);\n"
            + "    '}'\n"
            + "    private static List<Map<String, Object>> totals() '{'\n"
            + "        return Arrays.stream({2}.values())\n"
            + "                .map({2}::totals0)\n"
            + "                .collect(Collectors.toList());\n"
            + "    '}'\n"
            + "    private static Map<String, Object> totals0({2} e) '{'\n"
            + "        Map<String, Object> map = new HashMap<>();\n"
            + "        map.put(\"value\", e.getValue());\n"
            + "        map.put(\"label\", String.valueOf(e.getLabel()));\n"
            + "        return map;\n"
            + "    '}'\n"
            + "'}'";

    private static final String VUE_TEMPLATE = "<template>\n"
            + "    <div>\n"
            + "        <el-form :inline=\"true\" :model=\"searchForm\" class=\"search-form\">\n" +
            "        <el-row class=\"shadow-md p-4 bg-white\" >\n" +
            "                <el-col :span=\"22\" >\n" +
            "                    {1} " +
            "                    \n" +
            "                </el-col>\n" +
            "                <el-col :span=\"2\">\n" +
            "                    <div class=\"flex justify-end\">\n" +
            "                        <el-button type=\"primary\" @click=\"clickSearch\">搜索</el-button>\n" +
            "                    </div>\n" +
            "                </el-col>\n" +
            "        </el-row>\n" +
            "        </el-form>\n"
            + "    </div>\n"
            + "\n"
            + "\n"
            + "    <div class=\"w-full mt-4 bg-white shadow-md px-4 pb-4\">\n"
            + "    <div class=\"mb-2 flex justify-start items-center h-12\"> \n" +
            "               <el-button type=\"success\" @click=\"handleAdd\">\n" +
            "            <el-icon size=\"16\">\n" +
            "                <Plus />\n" +
            "            </el-icon>\n" +
            "            <span>新增</span>\n" +
            "        </el-button>\n" +
            "        \n" +
            "            </div>\n"
            + "        <el-table :data=\"{11}List\" style=\"width: 100%\" v-loading=\"loadStatus\" empty-text=\"没有更多了~\" border>\n"
            + "         <div class=\"mb-2 flex justify-start items-center h-12\"> \n" +
            "               <el-button type=\"success\" @click=\"handleAdd\">\n" +
            "                   <el-icon size=\"16\">\n" +
            "                    <Plus />\n" +
            "                   </el-icon>\n" +
            "                   <span>新增</span>\n" +
            "               </el-button>\n" +
            "        \n" +
            "            </div>"
            + "{2}"

            + "            <el-table-column fixed=\"right\" label=\"操作\">\n"
            + "                <template #default=\"scope\">\n"
            + "                 <div class=\"grid grid-cols-2 gap-2\">\n"
            + "                    <el-button type=\"primary\" size=\"small\" @click=\"clickEdit(scope.row)\" class=\"!ml-0\"\">编辑</el-button>"
            + "                    <el-button type=\"danger\" size=\"small\" @click=\"clickDelete(scope.row)\" class=\"!ml-0\"\">删除</el-button>\n"
            + "                 </div>\n"
            + "                </template>\n"
            + "            </el-table-column>\n"
            + "        </el-table>\n"
            + "        <el-pagination background class=\"mt-4 w-full flex justify-end\" layout=\"prev, pager, next\" :total=\"config.total\"\n"
            + "            @current-change=\"handleClick\" />\n"
            + "    </div>\n"
            + "\n"
            + "<el-drawer v-model=\"dialogVisible\" direction=\"rtl\" @close=\"handleCancel\">\n"
            + "        <template #header>\n"
            + "        <h4>'{{' action === ''add'' ? ''新增{3}'' : ''编辑{3}'' '}}'</h4>\n"
            + "        </template>\n"
            + "        <template #default>\n"
            + "        <el-form :model=\"submitForm\" ref=\"form\" label-width=\"130px\" label-position=\"left\">"
            + "{4}"
            + "        </el-form>\n"
            + "        </template>\n"
            + "        <template #footer>\n"
            + "            <div class=\"dialog-footer\">\n"
            + "                <el-button @click=\"handleCancel\">取消</el-button>\n"
            + "                <el-button type=\"primary\" @click=\"onSubmit\">确定</el-button>\n"
            + "            </div>\n"
            + "        </template>\n"
            + "    </el-drawer>\n"
            + "</template>\n"
            + "\n"
            + "<script setup>\n"
            + "import '{' onMounted, getCurrentInstance, ref, reactive '}' from ''vue'';\n"
            + "\n"
            + "const '{' proxy '}' = getCurrentInstance()\n"
            + "\n"
            + "// 请求参数\n"
            + "const config = reactive('{'\n"
            + "    pageNo: 1,\n"
            + "    pageSize: 10,\n"
            + "    total: 0,\n"
            + "'}')\n"
            + "\n"
            + "// 枚举\n"
            + "{5}\n"
            + "\n"
            + "// dialog展示控制\n"
            + "const dialogVisible = ref(false)\n"
            + "\n"
            + "// 搜索参数\n"
            + "const searchForm = reactive('{'\n"
            + "{6}"
            + "'}')\n"
            + "\n"
            + "// table加载状态\n"
            + "const loadStatus = ref(true)\n"
            + "\n"
            + "// list列表\n"
            + "const {11}List = ref([])\n"
            + "\n"
            + "// 弹窗 - 状态\n"
            + "const action = ref(''add'');\n"
            + "\n"
            + "onMounted: '{'\n"
            + "    query{0}List()\n"
            + "'}'\n"
            + "\n"
            + "// 查询\n"
            + "async function query{0}List() '{'\n"
            + "    loadStatus.value = true;\n"
            + "    const '{' data: data '}' = await proxy.$api.query{0}List(config)\n"
            + "    loadStatus.value = false;\n"
            + "    {11}List.value = data.list\n"
            + "    config.total = data.total\n"
            + "{7}"
            + "'}'\n"
            + "\n"
            + "// 分页点击\n"
            + "function handleClick(pageNo) '{'\n"
            + "    config.pageNo = pageNo\n"
            + "    query{0}List()\n"
            + "'}'\n"
            + "\n"
            + "// 搜索点击\n"
            + "function clickSearch() '{'\n"
            + "{8}"
            + "    query{0}List()\n"
            + "'}'\n"
            + "\n"
            + "// 弹窗 - 新增\n"
            + "function handleAdd() '{'\n"
            + "    dialogVisible.value = true\n"
            + "    action.value = ''add''\n"
            + "'}'\n"
            + "\n"
            + "// 弹窗 - 编辑\n"
            + "async function clickEdit(item) '{'\n"
            + "    action.value = ''edit''\n"
            + "    dialogVisible.value = true\n"
            + "\n"
            + "    proxy.$nextTick(() => '{'\n"
            + "        submitForm.id = item.id\n"
            + "{9}"
            + "    '}');\n"
            + "'}'\n"
            + "\n"
            + "// form - 新增/修改\n"
            + "let submitForm = reactive('{'\n"
            + "{10}"
            + "'}')\n"
            + "\n"
            + "// 提交 - 新增/修改\n"
            + "async function onSubmit() '{'\n"
            + "    proxy.$refs.form.validate(async (valid, fields) => '{'\n"
            + "        if (valid) '{'\n"
            + "            if (action.value === ''add'') '{'\n"
            + "                submitForm.id = null\n"
            + "                const res = await proxy.$api.add{0}(submitForm)\n"
            + "                resetAndToast(res)\n"
            + "            '}' else '{'\n"
            + "                const res = await proxy.$api.modify{0}(submitForm)\n"
            + "                resetAndToast(res)\n"
            + "            '}'\n"
            + "        '}' else '{'\n"
            + "            proxy.$toast.validerr()\n"
            + "        '}'\n"
            + "    '}')\n"
            + "'}'\n"
            + "\n"
            + "// 成功提示\n"
            + "function resetAndToast(res) '{'\n"
            + "    if (res) '{'\n"
            + "        dialogVisible.value = false\n"
            + "        proxy.$refs.form.resetFields()\n"
            + "        query{0}List()\n"
            + "        proxy.$toast.success()\n"
            + "    '}'\n"
            + "'}'\n"
            + "\n"
            + "// 按钮 - 取消\n"
            + "function handleCancel() '{'\n"
            + "    dialogVisible.value = false\n"
            + "    proxy.$nextTick(() => '{'\n"
            + "        proxy.$refs.form.resetFields();\n"
            + "           {13}  "
            + "    '}');\n"
            + "\n"
            + "'}'\n"
            + "{12}"
            + "\n"
            + "// 按钮 - 删除\n"
            + "function clickDelete(item) '{'\n"
            + "    proxy.$toast.confirm(async () => '{'\n"
            + "        const res = await proxy.$api.remove{0}('{' id: item.id '}')\n"
            + "        query{0}List()\n"
            + "        if (res) '{'\n"
            + "            proxy.$toast.success()\n"
            + "        '}'\n"
            + "    '}')\n"
            + "'}'\n"
            + "\n"
            + "</script>\n"
            + "\n"
            + "<style lang=\"less\" scoped>\n"
            + "\n"
            + ".search-form :deep(.el-form-item) '{'\n"
            + "    margin-bottom: 0;\n"
            + "    display: inline-flex;\n"
            + "    align-items: center;\n"
            + "'}'\n"
            + "\n"
            + ".search-form :deep(.el-form-item__content) '{'\n"
            + "    display: flex;\n"
            + "    align-items: center;\n"
            + "'}'\n"
            + "</style>\n";

    public static void generateJavaClassFromJson(String jsonFilePath, GenerationParams params) throws IOException {
        // 读取JSON文件内容并解析为JSONObject
        String jsonContent = readJsonFileContent(jsonFilePath);
        JSONObject jsonObject = JSON.parseObject(jsonContent);

        List<String> fieldDefinitions = new ArrayList<>();
        JSONArray fieldsArray = jsonObject.getJSONArray("fields");
        if (fieldsArray != null) {
            for (int i = 0; i < fieldsArray.size(); i++) {
                JSONObject fieldObject = fieldsArray.getJSONObject(i);
                String fieldName = fieldObject.getString("name");
                String fieldType = fieldObject.getString("type");
                String fieldDesc = fieldObject.getString("desc");

                // 将字段名转换为驼峰命名法（首字母小写）作为属性名
                String propertyName = toCamelCase(fieldName);

                if ("id".equals(fieldName)) {
                    fieldDefinitions.add("    @TableId(type = IdType.AUTO)");
                }

                String desc = "";
                if (StringUtils.isNotBlank(fieldDesc)) {
                    desc = "// " + fieldDesc;
                }
                if((fieldType.equalsIgnoreCase("LocalDateTime") || fieldType.equalsIgnoreCase("LocalDate")) && StringUtils.isNotBlank(fieldObject.getString("timeFormat"))) {
                    desc = "\n@JsonFormat(pattern = \""+fieldObject.getString("timeFormat")+"\")";
                }

                fieldDefinitions.add(MessageFormat.format(FIELD_TEMPLATE, desc, fieldType, propertyName));
            }
        }

        Struct struct = JSONObject.parseObject(jsonContent, Struct.class);

        // 生成实体类代码
        generateEntityClass(params, struct, fieldDefinitions);

        // 生成Mapper接口代码
        generateMapperInterface(params, struct);

        // 生成Service层接口代码
        generateServiceInterface(params, struct);

        // 生成Service层接口实现类代码
        generateServiceImpl(params, struct);

        // 生成WEB层transform 实体类代码
        generateWebEntityClass(params, struct);

        // 生成WEB层Service 实体类代码
        generateWebServiceClass(params, struct);

        // 生成WEB层Controller 实体类代码
        generateWebControllerClass(params, struct);

        // 生成SQL
        generateSQL(params, struct);

        // vue
        generateVue(params, struct);
    }

    private static void generateVue(GenerationParams params, Struct struct) throws IOException {
        List<StructAttr> formFields = struct.getFields().stream()
                .filter(JsonToJavaClassGenerator::isFormField)
                .collect(Collectors.toList());

        // 提交框 - 默认值
        String submitFormTemplate = formFields.stream().map(attr -> "    " + attr.getName() + ": ''")
                .collect(Collectors.joining(",\n"));

        // 提交框 - 编辑赋值
        String submitFormAssignTemplate = formFields.stream()
                .map(attr -> "        submitForm." + attr.getName() + " = item." + attr.getName() + "\n")
                .collect(Collectors.joining("\n"));

        // 普通上传文件
        int hasUploadFile = struct.getFields().stream()
                .filter(StructAttr::isFile)
                .filter(structAttr -> !structAttr.isFileImage())
                .collect(Collectors.toList())
                .size();

        if (hasUploadFile > 0) {
            String uploadFileUrlAppend = struct.getFields().stream()
                    .filter(StructAttr::isFile)
                    .filter(structAttr -> !structAttr.isFileImage())
                    .map(structAttr -> MessageFormat.format("\n{0}FileList.value = item.{0} ? ['{' name: item.{0}.split(''/'').pop(), url: item.{0} }] : []", structAttr.getName()))
                    .collect(Collectors.joining("\n"));
            submitFormAssignTemplate = submitFormAssignTemplate + uploadFileUrlAppend;
        }

        // 提交框 - 表单
        String submitFormVue = submitFormVue(formFields, struct);

        // 搜索框 - 默认值
        String searchFormTemplate = formFields.stream().map(attr -> "    " + attr.getName() + ": ''")
                .collect(Collectors.joining(",\n"));

        // 搜索框 - 赋值
        String searchFormAssignTemplate = formFields.stream()
                .map(JsonToJavaClassGenerator::searchFormAssignTemplate0)
                .collect(Collectors.joining("\n"));

        // 枚举 - 初始化
        String enumInitTemplate = formFields.stream()
                .filter(StructAttr::isEnumClass)
                .map(attr -> "const " + attr.getName() + "Options = ref([])\nconst " + attr.getName() + "SubmitOptions = ref([])")
                .collect(Collectors.joining("\n"));

        // 枚举 - 赋值
        String enumAssignTemplate = formFields.stream()
                .filter(StructAttr::isEnumClass)
                .map(attr -> attr.getName() + "Options.value = data." + attr.getName() + "s\n" + attr.getName() + "SubmitOptions.value = data." + attr.getName() + "s.filter(x=>x.value !== 0)")
                .collect(Collectors.joining("\n"));

        // 搜索框
        String searchFormVue = formFields.stream()
                .map(JsonToJavaClassGenerator::searchForm0)
                .collect(Collectors.joining("\n"));

        // 表格
        String tableVue = struct.getFields().stream()
                .filter(structAttr -> !structAttr.getName().equals("operatorId"))
                .filter(structAttr -> !structAttr.getName().equals("valid"))
                .map(JsonToJavaClassGenerator::tableVue)
                .collect(Collectors.joining("\n"));

        // 上传
        String uploadVue = struct.getFields().stream()
                .filter(StructAttr::isFile)
                .map(JsonToJavaClassGenerator::uploadVue)
                .collect(Collectors.joining("\n"));

        // 上传重置
        String uploadResetVue = struct.getFields().stream()
                .filter(StructAttr::isFile)
                .map(JsonToJavaClassGenerator::uploadResetVue)
                .collect(Collectors.joining("\n"));

        String str = MessageFormat.format(VUE_TEMPLATE, struct.getName(),
                searchFormVue,
                tableVue,
                struct.getDesc(),
                submitFormVue,
                enumInitTemplate,
                searchFormTemplate,
                enumAssignTemplate,
                searchFormAssignTemplate,
                submitFormAssignTemplate,
                submitFormTemplate,
                struct.getLowerName(),
                uploadVue,
                uploadResetVue);

        writeVUEToFile(str, struct, params);
    }

    private static String uploadResetVue(StructAttr structAttr) {
        if(structAttr.isFileImage()) {
            return "";
        }
        return MessageFormat.format("{0}FileList.value = []", structAttr.getName());
    }


    private static void writeVUEToFile(String str, Struct struct, GenerationParams params) throws IOException {
        String outputFilePath = params.parentPackage.replace('.', '/') + "/" + struct.getName() + ".vue";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(str.getBytes("UTF-8"));
        fos.close();
    }

    private static String submitFormVue(List<StructAttr> formFields, Struct struct) {
        List<StructAttr> baseStructAttrs = formFields.stream()
                .filter(structAttr -> !structAttr.getName().equals("valid"))
                .collect(Collectors.toList());
        List<List<StructAttr>> partition = Lists.partition(baseStructAttrs, 2);
        StringBuilder builder = new StringBuilder();
        for (List<StructAttr> structAttrs : partition) {
            String message = structAttrs.stream().map(JsonToJavaClassGenerator::submitFormVue0)
                    .collect(Collectors.joining("\n"));
            builder.append(message + "\n");
        }
        return builder.toString();
    }

    private static String submitFormVue0(StructAttr structAttr) {
        if (structAttr.isEnumClass()) {
            // Select
            return MessageFormat.format("                \n"
                    + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                    + "                        <el-select v-model=\"submitForm.{1}\" placeholder=\"请选择{0}\" style=\"width:148px\">\n"
                    + "                            <el-option v-for=\"item in {1}SubmitOptions\" :key=\"item.value\" :label=\"item.label\"\n"
                    + "                                :value=\"item.value\">\n"
                    + "                            </el-option>\n"
                    + "                        </el-select>\n"
                    + "                    </el-form-item>\n"
                    + "                ", structAttr.getDesc(), structAttr.getName());
        } else if (structAttr.isDateClass()) {
            // 日期
            return MessageFormat.format("                \n"
                    + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                    + "                        <el-date-picker\n" +
                    "                        v-model=\"submitForm.{1}\"\n" +
                    "                        type=\"{2}\"\n" +
                    "                        placeholder=\"请选择{0}\"\n" +
                    "                        format=\"{3}\"\n" +
                    "                        value-format=\"{3}\"\n" +
                    "                    />"
                    + "                    </el-form-item>\n"
                    + "                ", structAttr.getDesc(), structAttr.getName(), structAttr.dateOrTime(), structAttr.getTimeFormat().toUpperCase());
        } else if (structAttr.isFile()) {
            // 文件上传
            if (structAttr.isFileImage()) {
                return MessageFormat.format("                \n"
                        + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                        + "                      <el-upload\n" +
                        "                            class=\"border-2 border-dashed border-gray-300 rounded-lg cursor-pointer relative overflow-hidden hover:border-blue-500 transition-colors\"\n" +
                        "                            action=\"http://localhost:8080/api/common/upload\"\n" +
                        "                            :show-file-list=\"false\"\n" +
                        "                            :on-success=\"handle{2}FileUploadSuccess\"\n" +
                        "                            :before-upload=\"proxy.$upload.validateImageFormat\"\n" +
                        "                        >\n" +
                        "                            <img v-if=\"submitForm.{1}\" :src=\"submitForm.{1}\" class=\"w-[60px] h-[60px] object-cover\" />\n" +
                        "                            <div v-else class=\"w-[60px] h-[60px] flex items-center justify-center text-gray-400\">\n" +
                        "                                <el-icon size=\"28\"><Plus /></el-icon>\n" +
                        "                            </div>\n" +
                        "                        </el-upload>  "
                        + "                    </el-form-item>\n"
                        + "                ", structAttr.getDesc(), structAttr.getName(), toUpperCamelCase(structAttr.getName()));
            } else {
                return MessageFormat.format("                \n"
                        + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                        + "                     <el-upload\n" +
                        "                            action=\"http://localhost:8080/api/common/upload\"\n" +
                        "                            v-model:file-list=\"{1}FileList\"" +
                        "                            :show-file-list=\"true\"\n" +
                        "                            :limit=\"1\"\n" +
                        "                            :on-exceed=\"proxy.$upload.handleExceed\"\n" +
                        "                            :on-success=\"handle{2}FileUploadSuccess\"\n" +
                        "                        >\n" +
                        "                            <div class=\"flex items-center justify-center text-gray-400\">\n" +
                        "                                <span>点击上传</span>\n" +
                        "                            </div>\n" +
                        "                        </el-upload> "
                        + "                    </el-form-item>\n"
                        + "                ", structAttr.getDesc(), structAttr.getName(), toUpperCamelCase(structAttr.getName()));
            }

        } else if (structAttr.isDigit()) {
            // input
            return MessageFormat.format("                \n"
                    + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                    + "                        <el-input-number v-model=\"submitForm.{1}\" placeholder=\"请输入{0}\" />\n"
                    + "                    </el-form-item>\n"
                    + "                ", structAttr.getDesc(), structAttr.getName());
        }

        // input
        return MessageFormat.format("                \n"
                + "                    <el-form-item label=\"{0}\" prop=\"{1}\" :rules=\"['{' required: true, message: ''{0}是必填项'' '}']\">\n"
                + "                        <el-input v-model=\"submitForm.{1}\" placeholder=\"请输入{0}\" />\n"
                + "                    </el-form-item>\n"
                + "                ", structAttr.getDesc(), structAttr.getName());
    }


    private static String tableVue(StructAttr structAttr) {
        return MessageFormat
                .format("            <el-table-column prop=\"{0}\" label=\"{1}\" />",
                        structAttr.isEnumClass() ? structAttr.getName() + "Str" : structAttr.getName(), structAttr.getDesc());
    }

    private static String uploadVue(StructAttr structAttr) {

        // 文件上传回调
        String uploadSuccessFunction = MessageFormat.format("function handle{1}FileUploadSuccess(response, uploadFile) '{'\n" +
                "    submitForm.{0} = proxy.$upload.handleUploadSuccess(response, uploadFile)\n" +
                "'}'", structAttr.getName(), toUpperCamelCase(structAttr.getName()));

        // 不是图片，新增下面已传的list列表
        String editFileListView = "";
        if (structAttr.isFile() && !structAttr.isFileImage()) {
            editFileListView = MessageFormat.format("\n\nconst {0}FileList = ref([])", structAttr.getName());
        }

        return uploadSuccessFunction + editFileListView;
    }

    private static String searchForm0(StructAttr structAttr) {
        if (!structAttr.isQuery()) {
            return "";
        }
        String template = "                    <el-form-item label=\"{0}\" label-width=\"100px\">\n"
                + "                        {1}"
                + "                    </el-form-item>\n";
        String text = "";
        if (structAttr.isEnumClass()) {
            text = MessageFormat.format("<el-select v-model=\"searchForm.{0}\" placeholder=\"请选择{1}\" style=\"width:148px\">\n"
                    + "                            <el-option v-for=\"item in {0}Options\" :key=\"item.value\" :label=\"item.label\"\n"
                    + "                                :value=\"item.value\">\n"
                    + "                            </el-option>\n"
                    + "                        </el-select>\n", structAttr.getName(), structAttr.getDesc());
        } else if (structAttr.isDateClass()) {
            text = MessageFormat.format("<el-select v-model=\"searchForm.{0}\" placeholder=\"请选择{1}\" style=\"width:148px\">\n"
                    + "             <el-date-picker\n" +
                    "                        v-model=\"searchForm.{0}\"\n" +
                    "                        type=\"{2}\"\n" +
                    "                        placeholder=\"请选择{1}\"\n" +
                    "                    />                "
                    + "                        \n", structAttr.getName(), structAttr.getDesc(), structAttr.dateOrTime());
        } else {
            text = MessageFormat
                    .format("                        <el-input v-model=\"searchForm.{0}\" placeholder=\"请输入{1}\" clearable />\n",
                            structAttr.getName(), structAttr.getDesc());
        }

        return MessageFormat.format(template, structAttr.getDesc(), text);
    }

    private static String searchFormAssignTemplate0(StructAttr structAttr) {
        if (structAttr.isEnumClass() || structAttr.isDigit()) {
            return MessageFormat.format("    if (searchForm.{0}.toString().length > 0) '{'\n"
                    + "        config.{0} = searchForm.{0}\n"
                    + "    '}'", structAttr.getName());
        }
        return MessageFormat.format("    config.{0} = searchForm.{0}\n", structAttr.getName());
    }

    private static boolean isFormField(StructAttr structAttr) {
        return !structAttr.getName().equals("operatorId") && !structAttr.getName().equals("id")
                && !structAttr.getName().equals("createTime") && !structAttr.getName().equals("updateTime")
                && !structAttr.getName().equals("valid");
    }

    private static void generateSQL(GenerationParams params, Struct struct) throws IOException {

        String fieldStr = struct.getFields().stream().map(JsonToJavaClassGenerator::generateSQLField).collect(Collectors.joining(",\n"));

        String sqlStr = MessageFormat.format(SQL_TEMPLATE, struct.getTableName(), fieldStr);

        writeSQLToFile(sqlStr, struct, params);
    }

    private static void writeSQLToFile(String str, Struct struct, GenerationParams params) throws IOException {
        String outputFilePath = params.parentPackage.replace('.', '/') + "/" + struct.getTableName() + ".sql";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(str.getBytes("UTF-8"));
        fos.close();
    }

    private static String generateSQLField(StructAttr structAttr) {
        String fieldDesc = "";
        if (structAttr.getType().equals("Integer")) {
            if (structAttr.getName().equals("valid")) {
                fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` int(11) NOT NULL DEFAULT 1";
            } else {
                if (structAttr.isPrimary()) {
                    fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ";
                } else {
                    fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` int(11) NOT NULL DEFAULT 0";
                }

            }
        } else if (structAttr.getType().equals("Double")) {
            fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` double NOT NULL DEFAULT 0";
        }else if (structAttr.getType().equals("String")) {
            fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` varchar(500) NOT NULL DEFAULT ''";
        } else if (structAttr.getType().equals("LocalDateTime") || structAttr.getType().equals("LocalDate")) {
            if (structAttr.getName().equals("createTime")) {
                fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP";
            } else if (structAttr.getName().equals("updateTime")) {
                fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
            } else {
                fieldDesc = "`" + camelCaseToUnderscore(structAttr.getName()) + "` timestamp NOT NULL ";
            }
        }

        String desc = StringUtils.isNotBlank(structAttr.getFullDesc()) ? structAttr.getFullDesc() : structAttr.getDesc();
        if (StringUtils.isNotBlank(desc)) {
            // 描述
            fieldDesc += " COMMENT '" + desc + "'";
        }

        return fieldDesc;
    }

    private static void generateWebControllerClass(GenerationParams params, Struct struct) throws IOException {
        String serviceStr = MessageFormat.format(WEB_CONTROLLER_TEMPLATE, params.parentPackage, struct.getTableName(), struct.getName());

        // 将生成的实体类内容写入文件
        writeWebControllerClassToFile(serviceStr, struct, params);
    }

    private static void writeWebControllerClassToFile(String serviceStr, Struct struct, GenerationParams params) throws IOException {
        String outputFilePath = params.webPackagePrefix.replace('.', '/') + "/" + struct.getName().toLowerCase() + "/controller/" + struct.getName() + "Controller.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(serviceStr.getBytes("UTF-8"));
        fos.close();
    }

    private static void generateWebServiceClass(GenerationParams params, Struct struct) throws IOException {
        String conditions = struct.getFields().stream().map(attr -> toConditionQuery(struct, attr))
                .collect(Collectors.joining("\n"));
        String conditionStr = MessageFormat.format(WEB_SERVICE_QUERY_CONDITION_TEMPLATE, conditions, struct.getTableName());
        String serviceStr = MessageFormat.format(WEB_SERVICE_TEMPLATE, struct.getTableName(), struct.getName(), params.parentPackage, conditionStr);

        // 将生成的实体类内容写入文件
        writeWebServiceClassToFile(serviceStr, struct, params);
    }

    private static String toConditionQuery(Struct struct, StructAttr structAttr) {
        if (structAttr.isEnumClass() && structAttr.getName().equals("valid")) {
            return MessageFormat.format(".eq({0}::get" + toUpperCamelCase(structAttr.getName()) + ", 1)", struct.getName());
        }
        if (!structAttr.isQuery()) {
            return "";
        }

        // 生效中
        if (structAttr.getType().equals("int") || structAttr.getType().equals("Integer")) {
            return MessageFormat.format(".eq({0}QueryReq." + toGetMethod(structAttr.getName()) + " != 0, {1}::get" + toUpperCamelCase(structAttr.getName()) + ", {0}QueryReq." + toGetMethod(structAttr.getName()) + ")", struct.getTableName(), struct.getName());
        } else if (structAttr.getType().equals("String")) {
            // .like(StringUtils.isNotBlank(userQueryReq.getUserName()), User::getUserName, userQueryReq.getUserName())
            return MessageFormat.format(".like(StringUtils.isNotBlank({0}QueryReq." + toGetMethod(structAttr.getName()) + "), {1}::get" + toUpperCamelCase(structAttr.getName()) + ", {0}QueryReq." + toGetMethod(structAttr.getName()) + ")", struct.getTableName(), struct.getName());
        } else if (structAttr.getType().equals("LocalDateTime")) {
            return MessageFormat.format(".ge(StringUtils.isNotBlank({0}QueryReq." + toGetMethod(structAttr.getName()) + "), {1}::get" + toUpperCamelCase(structAttr.getName()) + ", {0}QueryReq." + toGetMethod(structAttr.getName()) + ")", struct.getTableName(), struct.getName());
        }

        return "";
    }

    private static void writeWebServiceClassToFile(String serviceStr, Struct struct, GenerationParams params) throws IOException {
        String outputFilePath = params.webPackagePrefix.replace('.', '/') + "/" + struct.getName().toLowerCase() + "/service/" + "Biz" + struct.getName() + "Service.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(serviceStr.getBytes("UTF-8"));
        fos.close();
    }

    private static void generateWebEntityClass(GenerationParams params, Struct struct) throws IOException {
        // 字段
        String fieldStr = struct.getFields().stream().map(attr -> toWebEntityFieldStr(attr)).collect(Collectors.joining("\n"));

        // builder赋值
        String builder = "return " + struct.getName() + "Res.builder()\n        ." + struct.getFields().stream().map(attr -> attr.getName() + "(" + struct.getName().toLowerCase() + ".get" + toUpperCamelCase(attr.getName()) + "())").collect(Collectors.joining("\n.")) + "\n        .build();\n";
        String builderStr = MessageFormat.format(WEB_ENTITY_INNER_RES_BUILDER_TEMPLATE, struct.getName(), struct.getName(), struct.getName().toLowerCase(), builder);

        // get方法
        String getterStr = struct.getFields().stream()
                .filter(StructAttr::isEnumClass)
                .map(JsonToJavaClassGenerator::generateWebEntityGetMethod)
                .collect(Collectors.joining("\n"));

        // 生成enum 自定义的enums
        List<Pair<String, String>> fileContent = struct.getFields().stream()
                .filter(StructAttr::isEnumClass)
                .filter(attr -> CollectionUtils.isNotEmpty(attr.getRefAttrs()))
                .map(attr -> {
                    String enumLabels = attr.getRefAttrs().stream()
                            .map(ref -> ref.getDesc() + "(" + ref.getValue() + ", \"" + ref.getLabel() + "\")")
                            .collect(Collectors.joining(",\n"));

                    return Pair.of(attr.getRefClassName(), MessageFormat
                            .format(ENUM_TEMPLATE, params.webPackagePrefix, struct.getLowerName(), attr.getRefClassName(), enumLabels));
                }).collect(Collectors.toList());

        for (int i = 0; i < fileContent.size(); i++) {
            writeWebEntityEnumToFile(fileContent.get(i).getRight(), struct, fileContent.get(i).getLeft(), params);
        }
        // query req
        String querys = struct.getFields().stream()
                .filter(attr -> attr.isQuery())
                .map(attr -> "    " + MessageFormat.format(FIELD_TEMPLATE, "", toSimpleFieldType(attr.getType()), attr.getName())).collect(Collectors.joining("\n"));
        String queryReqStr = MessageFormat.format(WEB_ENTITY_INNER_QUERY_REQ_TEMPLATE, struct.getName(), querys);

        String enumValues = "";
        if (fileContent.size() > 0) {
            enumValues = fileContent.stream()
                    .map(pair -> "@Default\nprivate List<Map<String, Object>> " + replaceEnum(toCamelCase(pair.getKey())) + "s = " + pair.getKey() + ".TOTALS;")
                    .collect(Collectors.joining("\n"));
        }
        // qurey res
        String queryResStr = MessageFormat.format(WEB_ENTITY_INNER_QUERY_RES_TEMPLATE, struct.getName(), enumValues);

        // web entity
        String entityClassResContent = MessageFormat.format(WEB_ENTITY_INNER_RES_TEMPLATE, struct.getName(), fieldStr + "\n" + builderStr + getterStr);
        String entityClassContent = MessageFormat.format(WEB_ENTITY_TEMPLATE, params.parentPackage, struct.getLowerName(), struct.getName(), entityClassResContent + "\n" + queryReqStr + "\n" + queryResStr);

        // 将生成的实体类内容写入文件
        writeWebEntityClassToFile(entityClassContent, struct, params);

    }

    private static void writeWebEntityEnumToFile(String classContent, Struct struct, String fileName, GenerationParams generationParams) throws IOException {
        String outputFilePath = generationParams.webPackagePrefix.replace('.', '/') + "/" + struct.getName().toLowerCase() + "/entities/" + fileName + ".java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(classContent.getBytes("UTF-8"));
        fos.close();
    }

    private static String generateWebEntityGetMethod(StructAttr structAttr) {
        if (structAttr.getRef().equals("@com.archer.admin.web.common.ValidEnum")) {
            // 默认引入
            return MessageFormat.format("\npublic String get" + toUpperCamelCase(structAttr.getName()) + "Str() '{' \n {0} \n'}'",
                    "return ValidEnum.of(" + structAttr.getName() + ").getLabel();");
        } else {
            return MessageFormat.format("\npublic String get" + toUpperCamelCase(structAttr.getName()) + "Str() '{' \n {0} \n'}'",
                    "return " + structAttr.getRefClassName() + ".of(" + structAttr.getName() + ").getLabel();");
        }
    }

    private static String toSimpleFieldType(String type) {
        if (type.equals("Integer") || type.equals("int")) {
            return "int";
        } else if (type.equals("Double") || type.equals("double")) {
            return "double";
        } else if (type.equals("Float") || type.equals("float")) {
            return "float";
        } else if (type.equals("Boolean") || type.equals("boolean")) {
            return "boolean";
        }
        return "String";
    }

    private static String toWebEntityFieldStr(StructAttr attr) {
        String desc = "";
        if (StringUtils.isNotBlank(attr.getDesc())) {
            desc = "// " + attr.getDesc();
        }
        String annotation = "";
        if (attr.getType().equals("LocalDateTime") || attr.getType().equals("LocalDate")) {
            if (StringUtils.isNotBlank(attr.getTimeFormat())) {
                annotation = "@JsonFormat(pattern = \"" + attr.getTimeFormat() + "\")";
            } else {
                annotation = "@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")";
            }
        }
        String descAndAnno = StringUtils.isNotBlank(desc) ? (desc + "\n" + annotation) : annotation;
        return MessageFormat.format(FIELD_TEMPLATE, descAndAnno, attr.getType(), attr.getName());
    }

    private static void generateEntityClass(GenerationParams params, Struct struct, List<String> fieldDefinitions) throws IOException {
        String entityClassContent = MessageFormat.format(ENTITY_CLASS_TEMPLATE, params.basePackagePrefix, struct.getTableName(), struct.getName(),
                String.join("\n", fieldDefinitions));

        // 将生成的实体类内容写入文件
        writeEntityClassToFile(entityClassContent, struct.getName(), params.basePackagePrefix);
    }

    private static void generateMapperInterface(GenerationParams params, Struct struct) throws IOException {
        String mapperContent = MessageFormat.format(MAPPER_TEMPLATE, params.basePackagePrefix, struct.getName());

        // 将生成的Mapper接口内容写入文件
        writeMapperInterfaceToFile(mapperContent, struct.getName(), params.basePackagePrefix);
    }

    private static void generateServiceInterface(GenerationParams params, Struct struct) throws IOException {
        String serviceInterfaceContent = MessageFormat.format(SERVICE_INTERFACE_TEMPLATE, params.basePackagePrefix, struct.getName());

        // 将生成的Service层接口内容写入文件
        writeServiceInterfaceToFile(serviceInterfaceContent, struct.getName(), params.basePackagePrefix);
    }

    private static void generateServiceImpl(GenerationParams params, Struct struct) throws IOException {
        String serviceImplContent = MessageFormat.format(SERVICE_IMPL_TEMPLATE, params.basePackagePrefix, struct.getName());

        // 将生成的Service层接口实现类内容写入文件
        writeServiceImplToFile(serviceImplContent, struct.getName(), params.basePackagePrefix);
    }

    private static String readJsonFileContent(String jsonFilePath) throws IOException {
        StringBuilder jsonContentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFilePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContentBuilder.append(line);
            }
        }
        return jsonContentBuilder.toString();
    }

    private static void writeEntityClassToFile(String classContent, String className, String packagePrefix) throws IOException {
        String outputFilePath = packagePrefix.replace('.', '/') + "/entities/" + className + ".java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(classContent.getBytes("UTF-8"));
        fos.close();
    }

    private static void writeMapperInterfaceToFile(String mapperContent, String className, String packagePrefix) throws IOException {
        String outputFilePath = packagePrefix.replace('.', '/') + "/repository/" + className + "Mapper.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(mapperContent.getBytes("UTF-8"));
        fos.close();
    }

    private static void writeServiceInterfaceToFile(String serviceInterfaceContent, String className, String packagePrefix) throws IOException {
        String outputFilePath = packagePrefix.replace('.', '/') + "/service/" + className + "Service.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(serviceInterfaceContent.getBytes("UTF-8"));
        fos.close();
    }

    private static void writeServiceImplToFile(String serviceImplContent, String className, String packagePrefix) throws IOException {
        String outputFilePath = packagePrefix.replace('.', '/') + "/service/impl/" + className + "ServiceImpl.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(serviceImplContent.getBytes("UTF-8"));
        fos.close();
    }

    private static void writeWebEntityClassToFile(String classContent, Struct struct, GenerationParams generationParams) throws IOException {
        String outputFilePath = generationParams.webPackagePrefix.replace('.', '/') + "/" + struct.getName().toLowerCase() + "/entities/" + struct.getName() + "Transform.java";
        File outputFile = new File(outputFilePath);
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(classContent.getBytes("UTF-8"));
        fos.close();
    }

    private static String replaceEnum(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.replace("enum", "").replace("Enum", "");
    }


    private static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    private static String toUpperCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static String camelCaseToUnderscore(String camelCaseString) {
        if (camelCaseString == null || camelCaseString.isEmpty()) {
            return camelCaseString;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char currentChar = camelCaseString.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    private static String toGetMethod(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return "get" + input.substring(0, 1).toUpperCase() + input.substring(1) + "()";
    }

    public static void main(String[] args) {
        GenerationParams params = new GenerationParams("com.archer.admin");
        try {
            String currentPath = System.getProperty("user.dir");
            System.out.println(currentPath);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/menu.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/role_menu.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/user_role.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/user.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/role.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/copyright2.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/product.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/channel.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/productPic.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/productSpecGroup.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/productSpecValue.json", params);
            // generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/sku.json", params);
             generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/productChannel.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/productChannelSku.json", params);
//            generateJavaClassFromJson(currentPath + "/admin-web/src/main/resources/category.json", params);
            System.out.println("Java class generated successfully!");
        } catch (IOException e) {
            System.out.println("Error generating Java class: " + e.getMessage());
            e.printStackTrace();
        }
    }
}