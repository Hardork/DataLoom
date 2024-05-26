package com.hwq.bi.controller;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.bi.annotation.ReduceRewardPoint;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.data.DataQueryRequest;
import com.hwq.bi.model.dto.user_data.DeleteUserDataRequest;
import com.hwq.bi.model.dto.user_data.ShareUserDataRequest;
import com.hwq.bi.model.dto.user_data.UploadUserDataRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.hwq.bi.model.vo.DataCollaboratorsVO;
import com.hwq.bi.model.vo.DataPage;
import com.hwq.bi.mongo.dto.AddChardDataRecordRequest;
import com.hwq.bi.mongo.dto.DeleteChartDataRecordRequest;
import com.hwq.bi.mongo.dto.EditChartDataRecordRequest;
import com.hwq.bi.mongo.entity.ChartData;
import com.hwq.bi.service.MongoService;
import com.hwq.bi.service.UserDataService;
import com.hwq.bi.service.UserService;
import com.hwq.bi.utils.datasource.ExcelUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/4/25 15:10
 * @description
 */
@RestController
@RequestMapping("/data")
@Slf4j
public class DataController {

    @Resource
    private MongoService mongoService;
    @Resource
    private UserService userService;

    @Resource
    private UserDataService userDataService;

    @Resource
    private ExcelUtils excelUtils;


    @ReduceRewardPoint
    @PostMapping("/upload")
    @ApiOperation("用户上传数据集")
    public BaseResponse<Long> uploadFileToMongo(@RequestPart("file") MultipartFile multipartFile, UploadUserDataRequest uploadUserDataRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 10 * 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 10M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
        ThrowUtils.throwIf(uploadUserDataRequest == null, ErrorCode.PARAMS_ERROR);
        String dataName = uploadUserDataRequest.getDataName();
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.PARAMS_ERROR, "数据集名称不得为空");
        String description = uploadUserDataRequest.getDescription();
        Long id = userDataService.save(loginUser, dataName, description, multipartFile);
        // 将生成的chartId作为数据表的表名chart_{id}
        // 将用户上传的数据存入到MongoDB中
        // 返回mongoDB
        return ResultUtils.success(id);
    }




    @PostMapping("/share/userData")
    @ApiOperation("共享用户数据集")
    public BaseResponse<String> shareUserData(@RequestBody ShareUserDataRequest shareUserDataRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(shareUserDataRequest == null || shareUserDataRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        Long id = shareUserDataRequest.getId();
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String link = userDataService.genLink(shareUserDataRequest, loginUser);
        return ResultUtils.success(link);
    }

    @GetMapping("/{dataId}/{type}/{secret}")
    @ApiOperation("链接获取数据集权限")
    public BaseResponse<Boolean> getOtherUserData(@PathVariable Long dataId, @PathVariable Integer type, @PathVariable String secret, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(dataId == null || dataId < 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "请求类型为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(secret), ErrorCode.PARAMS_ERROR);
        Boolean res = userDataService.checkLinkAndAuthorization(dataId, type, secret, loginUser);
        return ResultUtils.success(res);
    }

    @GetMapping("/list/collaborators/{dataId}")
    @ApiOperation("查看数据协作者")
    public BaseResponse<List<DataCollaboratorsVO>> getDataCollaborators(@PathVariable Long dataId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(dataId == null || dataId < 0, ErrorCode.PARAMS_ERROR);
        List<DataCollaboratorsVO> userList = userDataService.getDataCollaborators(dataId, loginUser);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete/userData")
    @ApiOperation("删除用户数据集")
    public BaseResponse<Boolean> deleteUserData(@RequestBody DeleteUserDataRequest deleteUserDataRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(deleteUserDataRequest == null || deleteUserDataRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteUserDataRequest.getId();
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 删除userData中的数据
        QueryWrapper<UserData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Boolean delete = userDataService.deleteUserData(id, loginUser);
        Boolean delMongo = mongoService.deleteUserData(id);
        return ResultUtils.success(delete && delMongo);
    }

    @GetMapping("/list/data/info")
    @ApiOperation("显示用户所有数据集")
    public BaseResponse<List<UserData>> listUserDataInfo(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        List<UserData> userDataList = userDataService.listByPermission(loginUser);
        return ResultUtils.success(userDataList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除记录")
    public BaseResponse<Boolean> deleteRecordById(@RequestBody DeleteChartDataRecordRequest deleteChartDataRecordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteChartDataRecordRequest == null, ErrorCode.PARAMS_ERROR);
        String id = deleteChartDataRecordRequest.getId();
        Long dataId = deleteChartDataRecordRequest.getDataId();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(id), ErrorCode.PARAMS_ERROR, "文档id为空");
        Boolean delete = mongoService.deleteRecordById(dataId, id, request);
        return ResultUtils.success(delete);
    }

    @PostMapping("/add")
    @ApiOperation("添加记录")
    public BaseResponse<Boolean> addOneRecord(@RequestBody AddChardDataRecordRequest addChardDataRecordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(addChardDataRecordRequest == null, ErrorCode.PARAMS_ERROR);
        Long dataId = addChardDataRecordRequest.getDataId();
        Map<String, Object> data = addChardDataRecordRequest.getData();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id为空");
        Boolean add = mongoService.addOneRecord(dataId, data, request);
        return ResultUtils.success(add);
    }

    @PostMapping("/edit")
    @ApiOperation("修改记录")
    public BaseResponse<Boolean> editRecordById(@RequestBody EditChartDataRecordRequest editChartDataRecordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(editChartDataRecordRequest == null, ErrorCode.PARAMS_ERROR);
        String id = editChartDataRecordRequest.getId();
        Long dataId = editChartDataRecordRequest.getDataId();
        Map<String, Object> data = editChartDataRecordRequest.getData();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(id), ErrorCode.PARAMS_ERROR, "文档id为空");
        ThrowUtils.throwIf(data == null, ErrorCode.PARAMS_ERROR, "修改数据不得为空");
        Boolean edit = mongoService.editRecordById(dataId, id, data, request);
        return ResultUtils.success(edit);
    }

    @PostMapping("/list/page")
    @ApiOperation("分页查询")
    public BaseResponse<DataPage> listUserDataByPage(@RequestBody DataQueryRequest dataQueryRequest,
                                                                 HttpServletRequest request) {
        long current = dataQueryRequest.getCurrent() - 1;
        long size = dataQueryRequest.getPageSize();
        Long dataId = dataQueryRequest.getDataId();
        ChartData chartData = dataQueryRequest.getChartData();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "id不得为空");
        DataPage dataPage = mongoService.listByPage(dataId, chartData, request, current, size);
        return ResultUtils.success(dataPage);
    }
}
