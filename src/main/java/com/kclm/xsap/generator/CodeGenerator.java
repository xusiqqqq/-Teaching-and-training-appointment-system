/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-07 10:20
 * @Description 利用mybatis-plus 的generator组件来生成代码
 */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        //代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        //全局配置
        GlobalConfig gc = new GlobalConfig();
        final String projectPath = System.getProperty("user.dir");
        log.debug(projectPath);
        gc.setOutputDir(projectPath+"/src/main/java");
        gc.setAuthor("yejf");
        gc.setOpen(false);
        //把全局配置添加给 代码生成器
        autoGenerator.setGlobalConfig(gc);

        //配置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/xsap_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        //把数据源添加给 代码生成器
        autoGenerator.setDataSource(dsc);

        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("xsap");
        pc.setParent("com.kclm.xsap");
        //
        autoGenerator.setPackageInfo(pc);

        //配置模板引擎
        class MyCustomerTemplateEngine extends AbstractTemplateEngine {
            @Override
            public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
                System.out.println("------------");
            }

            @Override
            public String templateFilePath(String filePath) {
                System.out.println(filePath);
                return "";
            }
        }
        autoGenerator.setTemplateEngine(new MyCustomerTemplateEngine());
        //配置模板
        TemplateConfig tc = new TemplateConfig();
        gc.setControllerName(null);
        gc.setEntityName(null);
        gc.setMapperName(null);
        tc.setXml(null);
        autoGenerator.setTemplate(tc);

        //自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                //to do
            }
        };
        //自定义输出设置
        List<FileOutConfig> fileOutConfigs = new ArrayList<>();
        fileOutConfigs.add(new FileOutConfig() {
            @Override
            public String outputFile(TableInfo tableInfo) {
                //自定义输出文件名
                return projectPath+"/src/main/resources/mapper/"+tableInfo.getEntityName()
                        +"Mapper"+ StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(fileOutConfigs);
        autoGenerator.setCfg(cfg);

        //策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass("com.kclm.xsap.entity.BaseEntity");
        strategy.setEntityLombokModel(true);
        //strategy.setRestControllerStyle(true);
        // 公共父类
        //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns(new String[]{"id", "createTime","lastModifyTime","version"});
        /*strategy.setInclude(new String[]{
                        "t_global_reservation_set","t_recharge_record","t_member_log","t_member_car",
                        "t_consume_record","t_course_card","t_course","t_schedule_record",
                        "t_reservation_record","t_member","t_member_bind_record",
                        "t_employee"});*/
        strategy.setInclude("t_user");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        //把策略添加到 代码生成器中
        autoGenerator.setStrategy(strategy);
        // 执行
        autoGenerator.execute();
    }
}
