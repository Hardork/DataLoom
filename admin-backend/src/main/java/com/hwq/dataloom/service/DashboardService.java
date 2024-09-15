package com.hwq.dataloom.service;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.dashboard.*;
import com.hwq.dataloom.model.entity.ChartOption;
import com.hwq.dataloom.model.entity.Dashboard;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.vo.dashboard.GetChartDataVO;

import java.util.List;

/**
* @author wqh
* @description 针对表【dashboard(仪表盘表)】的数据库操作Service
* @createDate 2024-09-12 18:52:14
*/
public interface DashboardService extends IService<Dashboard> {

    /**
     * 添加仪表盘
     * @param addDashboardRequestDTO
     * @param loginUser
     */
    void addDashboard(AddDashboardRequestDTO addDashboardRequestDTO, User loginUser);

    /**
     * 删除仪表盘
     * @param dashboardId
     * @param loginUser
     */
    void deleteDashboard(Long dashboardId, User loginUser);

    /**
     * 保存仪表盘
     * @param saveDashboardRequestDTO
     * @param loginUser
     */
    void saveDashboard(SaveDashboardRequestDTO saveDashboardRequestDTO, User loginUser);

    /**
     * 添加图表
     * @param addDashboardChartRequestDTO
     * @param loginUser
     */
    Long addChart(AddDashboardChartRequestDTO addDashboardChartRequestDTO, User loginUser);

    ChartOption getChartById(Long chartOptionId);

    /**
     * 编辑图表
     * @param editDashboardChartRequestDTO
     * @param loginUser
     */
    void editChart(EditDashboardChartRequestDTO editDashboardChartRequestDTO, User loginUser);

    /**
     * 查询所有图表
     * @param dashboardId
     * @param loginUser
     */
    List<ChartOption> listAllChart(Long dashboardId, User loginUser);

    /**
     * 查询用户所有仪表盘
     * @param loginUser
     * @return
     */
    List<Dashboard> listAllDashboard(User loginUser);

    /**
     * 根据id获取仪表盘
     * @param dashboardId
     * @param loginUser
     * @return
     */
    Dashboard getDashboardById(Long dashboardId, User loginUser);

    /**
     * 根据id删除仪表盘
     * @param dashboardId
     * @param loginUser
     * @return
     */
    Boolean deleteChart(Long dashboardId, User loginUser);

    /**
     * 根据配置获取图表数据
     * @param getChartDataRequestDTO
     * @param loginUser
     * @return
     */
    GetChartDataVO getChartData(GetChartDataRequestDTO getChartDataRequestDTO, User loginUser);

}
