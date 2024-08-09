package com.hwq.bi.service;

import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.vo.DataPage;
import com.hwq.bi.mongo.entity.ChartData;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/25 20:04
 * @description
 */
@Service
public interface MongoService {

    /**
     * 分页查询
     * @param chartId
     * @param chartData
     * @param request
     * @param current
     * @param size
     * @return
     */
    DataPage listByPage(Long chartId, ChartData chartData, HttpServletRequest request, long current, long size);

    /**
     * 根据dataId + id删除记录
     * @param dataId
     * @param id
     * @param request
     * @return
     */
    Boolean deleteRecordById(Long dataId, String id, HttpServletRequest request);

    /**
     * 根据dataId + id编辑记录
     * @param dataId
     * @param id
     * @param data
     * @param request
     * @return
     */
    Boolean editRecordById(Long dataId, String id, Map<String, Object> data, HttpServletRequest request);

    /**
     * 根据dataId添加一条记录
     * @param dataId
     * @param data
     * @param request
     * @return
     */
    Boolean addOneRecord(Long dataId, Map<String, Object> data, HttpServletRequest request);

    /**
     * 删除用户数据
     * @param id
     * @return
     */
    Boolean deleteUserData(Long id);

    /**
     * 拷贝数据到新集合
     * @param sourceCollectionName
     * @param user
     * @return
     */
    Boolean copyDataToNewCollection(String sourceCollectionName, User user);

    Boolean saveChartOptionToMongo(Long chartId, String option);


}
