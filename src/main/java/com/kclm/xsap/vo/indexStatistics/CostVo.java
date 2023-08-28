package com.kclm.xsap.vo.indexStatistics;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CostVo {
    //图表名
    private String title;
    //x轴名字
    private String xname;
    //x轴数据
    private List<String> time;
    //y轴数据
    private List<Integer> data;
}
