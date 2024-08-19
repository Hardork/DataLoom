package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.datasource.MoveDatasourceDirNodeRequest;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.entity.DatasourceDirTree;
import com.hwq.dataloom.model.enums.DirTypeEnum;
import com.hwq.dataloom.model.vo.datasource.ListDatasourceTreeVO;
import com.hwq.dataloom.service.DatasourceDirTreeService;
import com.hwq.dataloom.mapper.DatasourceDirTreeMapper;
import com.hwq.dataloom.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
* @author wqh
* @description 针对表【datasource_dir_tree(数据源目录树)】的数据库操作Service实现
* @createDate 2024-08-18 23:39:59
*/
@Service
public class DatasourceDirTreeServiceImpl extends ServiceImpl<DatasourceDirTreeMapper, DatasourceDirTree>
    implements DatasourceDirTreeService{

    @Resource
    private UserService userService;

    @Override
    public Boolean addDatasourceDirNode(AddDatasourceDirRequest addDatasourceDirRequest, HttpServletRequest request) {
        String name = addDatasourceDirRequest.getName();
        String type = addDatasourceDirRequest.getType();
        // 获取目录类型枚举
        DirTypeEnum dirTypeEnum = DirTypeEnum.getEnumByText(type);
        ThrowUtils.throwIf(dirTypeEnum == null, ErrorCode.NOT_FOUND_ERROR, "文件类型不存在");
        Long pid = addDatasourceDirRequest.getPid();
        // 权重, 用于排序
        Integer wight = addDatasourceDirRequest.getWight();
        // 获取用户信息
        User loginUser = userService.getLoginUser(request);
        // 如果插入的父节点是目录根节点就直接插入
        if (pid == 0) {
            // 插入
            DatasourceDirTree datasourceDirTree = new DatasourceDirTree();
            datasourceDirTree.setName(name);
            datasourceDirTree.setType(type);
            datasourceDirTree.setPid(0L);
            datasourceDirTree.setWight(wight);
            datasourceDirTree.setUserId(loginUser.getId());
            ThrowUtils.throwIf(!this.save(datasourceDirTree), ErrorCode.SYSTEM_ERROR, "系统异常");
            return true;
        }
        // 如果要保存的父目录是根节点子目录
        LambdaQueryWrapper<DatasourceDirTree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.
                eq(DatasourceDirTree::getId, pid).
                eq(DatasourceDirTree::getUserId, loginUser.getId());
        DatasourceDirTree parentDir = this.getOne(lambdaQueryWrapper);
        ThrowUtils.throwIf(parentDir == null, ErrorCode.NOT_FOUND_ERROR, "插入目录不存在");
        // 插入
        DatasourceDirTree datasourceDirTree = new DatasourceDirTree();
        datasourceDirTree.setName(name);
        datasourceDirTree.setType(type);
        datasourceDirTree.setPid(pid);
        datasourceDirTree.setWight(wight);
        datasourceDirTree.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!this.save(datasourceDirTree), ErrorCode.SYSTEM_ERROR, "系统异常");
        return true;
    }

    @Override
    public ListDatasourceTreeVO listDatasourceDirTree(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 查询出所有用户创建的节点
        LambdaQueryWrapper<DatasourceDirTree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.
                eq(DatasourceDirTree::getUserId, loginUser.getId());
        List<DatasourceDirTree> datasourceDirNodeList = this.list(lambdaQueryWrapper);
        // 创建根节点
        ListDatasourceTreeVO rootNode = genRootNode();
        // 遍历给节点赋值
        buildTree(rootNode, datasourceDirNodeList);
        return rootNode;
    }

    @Override
    public Boolean moveDatasourceDirNode(MoveDatasourceDirNodeRequest moveDatasourceDirNodeRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long id = moveDatasourceDirNodeRequest.getId();
        Long newPid = moveDatasourceDirNodeRequest.getNewPid();
        DatasourceDirTree datasourceDirTreeNode = this.getById(id);
        ThrowUtils.throwIf(Objects.equals(id, newPid), ErrorCode.PARAMS_ERROR, "移动目标文件夹不得为自己");
        // 校验权限
        ThrowUtils.throwIf(!Objects.equals(datasourceDirTreeNode.getUserId(), loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        if (newPid != 0L) {
            DatasourceDirTree parentNode = this.getById(newPid);
            ThrowUtils.throwIf(Objects.equals(parentNode.getUserId(), loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        }
        // 防止查询时死循环，需要校验
        ThrowUtils.throwIf(isChildNode(id, newPid), ErrorCode.PARAMS_ERROR, "不得将文件夹移动到自己的子目录下");
        // 更新
        datasourceDirTreeNode.setPid(newPid);
        ThrowUtils.throwIf(!this.updateById(datasourceDirTreeNode), ErrorCode.SYSTEM_ERROR);
        return true;
    }

    /**
     * 校验parentNodeId的所有父节点是否有为nodeId的
     * @param nodeId 当前节点id
     * @param parentNodeId 父节点id
     * @return
     */
    private boolean isChildNode(Long nodeId, Long parentNodeId) {
        if (nodeId.equals(parentNodeId)) {
            return true;
        }
        DatasourceDirTree parentNode = this.getById(parentNodeId);
        return parentNode != null && isChildNode(nodeId, parentNode.getPid());
    }

    /**
     * 构造目录树
     * @param rootNode
     * @param datasourceDirNodeList
     */
    public void buildTree(ListDatasourceTreeVO rootNode, List<DatasourceDirTree> datasourceDirNodeList) {
        if (rootNode == null) {
            return;
        }
        for (DatasourceDirTree datasourceDirTree : datasourceDirNodeList) {
            if (Objects.equals(datasourceDirTree.getPid(), rootNode.getId())) {
                // 构造子节点
                ListDatasourceTreeVO sonNode = new ListDatasourceTreeVO();
                BeanUtils.copyProperties(sonNode, datasourceDirTree);
                rootNode.getChildren().add(sonNode);
                // 递归构造
                buildTree(rootNode, datasourceDirNodeList);
            }
        }
    }

    /**
     * 生成根节点
     * @return
     */
    public ListDatasourceTreeVO genRootNode() {
        ListDatasourceTreeVO rootNode = new ListDatasourceTreeVO();
        rootNode.setId(0L);
        rootNode.setName("数据源");
        rootNode.setType("dir");
        rootNode.setPid(0L);
        rootNode.setWight(1);
        return rootNode;
    }
}




