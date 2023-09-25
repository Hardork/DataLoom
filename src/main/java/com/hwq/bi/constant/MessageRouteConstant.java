package com.hwq.bi.constant;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 10:35
 * @Description:
 *
 * { path: '/my_chart', name: '历史分析', icon: 'pieChart', component: './MyChart' },
 *   { path: '/my_info', name: '个人中心', icon: 'userOutlined', component: './MyInfo' },
 *   { path: '/chart_detail/:id', name: '分析详情', icon: 'pieChart', component: './ChartDetail', hideInMenu: true},
 **/
public interface MessageRouteConstant {
    String MY_CHART = "/my_chart";
    String MY_INFO = "//my_info";
    String CHART_DETAIL = "/chart_detail/";
}
