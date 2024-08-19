package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.datasource.MoveDatasourceDirNodeRequest;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.entity.DatasourceDirTree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.vo.datasource.ListDatasourceTreeVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wqh
* @description 针对表【datasource_dir_tree(数据源目录树)】的数据库操作Service
* @createDate 2024-08-18 23:39:59
*/
public interface DatasourceDirTreeService extends IService<DatasourceDirTree> {

    /**
     * 新建文件夹
     * @param addDatasourceDirRequest
     * @param request
     * @return
     */
    Boolean addDatasourceDirNode(AddDatasourceDirRequest addDatasourceDirRequest, HttpServletRequest request);


    /**
     * 展示文件夹目录树
     * @param request
     * @return
     */
    ListDatasourceTreeVO listDatasourceDirTree(HttpServletRequest request);

    /**
     * 移动文件节点
     * @param moveDatasourceDirNodeRequest
     * @param request
     * @return
     */
    Boolean moveDatasourceDirNode(MoveDatasourceDirNodeRequest moveDatasourceDirNodeRequest, HttpServletRequest request);
}
