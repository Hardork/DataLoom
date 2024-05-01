package com.hwq.bi.bizmq.test;

import cn.hutool.core.collection.CollUtil;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.enums.RoleEnum;
import com.hwq.bi.utils.Message;
import com.hwq.bi.utils.MoonshotAiUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/4/29 11:56
 * @description
 */
public class Kimi {
    public static void main(String[] args) {
        String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容： \n" +
                "分析需求： {数据分析的需求或者目标} \n" +
                "原始数据： {csv格式的原始数据，用,作为分隔符} \n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，要求option是json格式的,合理地将数据进行可视化，不要生成任何多余的内容，比如注释} \n" +
                "【【【【【 \n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        String data = "排名,球队,胜场,负场,胜场差,胜率\n" +
                "1,Boston Celtics,64,18,0.0,78%\n" +
                "2,New York Knicks,50,32,14.0,61%\n" +
                "3,Milwaukee Bucks,49,33,15.0,60%\n" +
                "4,Cleveland Cavaliers,48,34,16.0,58%\n" +
                "5,Orlando Magic,47,35,17.0,57%\n" +
                "6,Indiana Pacers,47,35,17.0,57%\n" +
                "7,Philadelphia 76ers,47,35,17.0,57%\n" +
                "8,Miami Heat,46,36,18.0,56%\n" +
                "9,Chicago Bulls,39,43,25.0,48%\n" +
                "10,Atlanta Hawks,36,46,28.0,44%\n" +
                "11,Brooklyn Nets,32,50,32.0,39%\n" +
                "12,Toronto Raptors,25,57,39.0,30%\n" +
                "13,Charlotte Hornets,21,61,43.0,26%\n" +
                "14,Washington Wizards,15,67,49.0,18%\n" +
                "15,Detroit Pistons,14,68,50.0,17%\n" +
                "1,Oklahoma City Thunder,57,25,0.0,70%\n" +
                "2,Denver Nuggets,57,25,0.0,70%\n" +
                "3,Minnesota Timberwolves,56,26,1.0,68%\n" +
                "4,LAClippers,51,31,6.0,62%\n" +
                "5,Dallas Mavericks,50,32,7.0,61%\n" +
                "6,Phoenix Suns,49,33,8.0,60%\n" +
                "7,New Orleans Pelicans,49,33,8.0,60%\n" +
                "8,Los Angeles Lakers,47,35,10.0,57%\n" +
                "9,Sacramento Kings,46,36,11.0,56%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%\n" +
                "10,Golden State Warriors,46,36,11.0,56%\n" +
                "11,Houston Rockets,41,41,16.0,50%\n" +
                "12,Utah Jazz,31,51,26.0,38%\n" +
                "13,Memphis Grizzlies,27,55,30.0,33%\n" +
                "14,San Antonio Spurs,22,60,35.0,27%\n" +
                "15,Portland Trail Blazers,21,61,36.0,26%";
        Chart chart = new Chart();
        chart.setChartType("折线图");
        chart.setGoal("随意分析");
        chart.setChartData(data);
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), buildUserInput(chart))
        );
        System.out.println(MoonshotAiUtils.chat("moonshot-v1-32k",messages));
    }

    private static String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}
