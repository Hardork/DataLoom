package com.hwq.bi.service;

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

    DataPage listByPage(Long chartId, ChartData chartData, HttpServletRequest request, long current, long size);

    Boolean deleteRecordById(Long dataId, String id, HttpServletRequest request);

    Boolean editRecordById(Long dataId, String id, Map<String, Object> data, HttpServletRequest request);

    Boolean addOneRecord(Long dataId, Map<String, Object> data, HttpServletRequest request);

    Boolean deleteUserData(Long id);
}
