package com.hwq.dataloom.controller;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.datasource.MoveDatasourceDirNodeRequest;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.dto.datasource_tree.DeleteDatasourceDirNodeRequest;
import com.hwq.dataloom.model.vo.datasource.ListDatasourceTreeVO;
import com.hwq.dataloom.service.DatasourceDirTreeService;
import com.hwq.dataloom.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author HWQ
 * @date 2024/8/18 23:41
 * @description 数据源文件夹管理
 */
@RestController
@RequestMapping("/datasource/tree")
public class DataSourceTreeController {

    @Resource
    private DatasourceDirTreeService datasourceDirTreeService;

    @Resource
    private UserService userService;

    /**
     * 新增文件或文件夹
     * @param addDatasourceDirRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addDatasourceDirNode(@RequestBody @Valid AddDatasourceDirRequest addDatasourceDirRequest, HttpServletRequest request) {
        return ResultUtils.success(datasourceDirTreeService.addDatasourceDirNode(addDatasourceDirRequest, userService.getLoginUser(request)));
    }

    /**
     * 查询文件夹目录树
     * @param request
     * @return
     */
    @GetMapping("/tree")
    public BaseResponse<ListDatasourceTreeVO> listDatasourceDirTree(HttpServletRequest request) {
        return ResultUtils.success(datasourceDirTreeService.listDatasourceDirTree(request));
    }

    /**
     * 移动文件或文件夹
     * @param request
     * @return
     */
    @PostMapping("/move")
    public BaseResponse<Boolean> moveDatasourceDirNode(@RequestBody @Valid MoveDatasourceDirNodeRequest moveDatasourceDirNodeRequest, HttpServletRequest request) {
        return ResultUtils.success(datasourceDirTreeService.moveDatasourceDirNode(moveDatasourceDirNodeRequest, request));
    }


    /**
     * 删除文件或文件夹
     * @param deleteDatasourceDirNodeRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDatasourceDirNode(@RequestBody @Valid DeleteDatasourceDirNodeRequest deleteDatasourceDirNodeRequest, HttpServletRequest request) {
        return ResultUtils.success(datasourceDirTreeService.deleteDatasourceDirNode(deleteDatasourceDirNodeRequest, request));
    }
}
