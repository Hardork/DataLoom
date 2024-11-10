package com.hwq.dataloom.controller;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.annotation.ReduceRewardPoint;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.data.DataQueryRequest;
import com.hwq.dataloom.model.dto.user_data.DeleteUserDataRequest;
import com.hwq.dataloom.model.dto.user_data.ShareUserDataRequest;
import com.hwq.dataloom.model.dto.user_data.UploadUserDataRequest;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.UserData;
import com.hwq.dataloom.model.vo.DataCollaboratorsVO;
import com.hwq.dataloom.model.vo.DataPage;
import com.hwq.dataloom.model.vo.data.PreviewExcelDataVO;
import com.hwq.dataloom.model.vo.user_data.UserDataTeamVO;
import com.hwq.dataloom.mongo.dto.AddChardDataRecordRequest;
import com.hwq.dataloom.mongo.dto.DeleteChartDataRecordRequest;
import com.hwq.dataloom.mongo.dto.EditChartDataRecordRequest;
import com.hwq.dataloom.mongo.entity.ChartData;
import com.hwq.dataloom.service.MongoService;
import com.hwq.dataloom.service.UserDataPermissionService;
import com.hwq.dataloom.service.UserDataService;
import com.hwq.dataloom.service.UserService;
import com.hwq.dataloom.utils.datasource.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/admin/data")
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

    @Resource
    private UserDataPermissionService userDataPermissionService;

    @ReduceRewardPoint
    @PostMapping("/upload")
    @Operation(summary = "用户上传数据集")
    public BaseResponse<Long> uploadFileToMongo(@RequestPart("file") MultipartFile multipartFile, UploadUserDataRequest uploadUserDataRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 校验文件
        checkFileValid(multipartFile);
        ThrowUtils.throwIf(uploadUserDataRequest == null, ErrorCode.PARAMS_ERROR);
        String dataName = uploadUserDataRequest.getDataName();
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.PARAMS_ERROR, "数据集名称不得为空");
        String description = uploadUserDataRequest.getDescription();
        Long id = userDataService.save(loginUser, dataName, description, multipartFile);
        return ResultUtils.success(id);
    }

    @ReduceRewardPoint
    @PostMapping("/upload/mysql")
    @Operation(summary = "用户上传数据集到MySQL")
    public BaseResponse<Long> uploadFileToMySQL(@RequestPart("file") MultipartFile multipartFile, UploadUserDataRequest uploadUserDataRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 校验文件
        checkFileValid(multipartFile);
        ThrowUtils.throwIf(uploadUserDataRequest == null, ErrorCode.PARAMS_ERROR);
        String dataName = uploadUserDataRequest.getDataName();
        ThrowUtils.throwIf(StringUtils.isEmpty(dataName), ErrorCode.PARAMS_ERROR, "数据集名称不得为空");
        String description = uploadUserDataRequest.getDescription();
        PreviewExcelDataVO previewExcelDataVO = excelUtils.queryDataFields(multipartFile);
        Long id = userDataService.saveToMySQL(loginUser, dataName, description, previewExcelDataVO.getTableFieldInfosList(), multipartFile);
        return ResultUtils.success(id);
    }


    @PostMapping("/preview")
    @Operation(summary = "预览上传数据")
    public BaseResponse<PreviewExcelDataVO> previewAndCheckExcelInfo(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 校验文件
        checkFileValid(multipartFile);
        // 返回数据
        PreviewExcelDataVO dataPage = excelUtils.queryDataFields(multipartFile);
        return ResultUtils.success(dataPage);
    }


    @PostMapping("/share/userData")
    @Operation(summary = "共享用户数据集")
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
    @Operation(summary = "链接获取数据集权限")
    public BaseResponse<Boolean> getOtherUserData(@PathVariable Long dataId, @PathVariable Integer type, @PathVariable String secret, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(dataId == null || dataId < 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "请求类型为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(secret), ErrorCode.PARAMS_ERROR);
        Boolean res = userDataService.checkLinkAndAuthorization(dataId, type, secret, loginUser);
        return ResultUtils.success(res);
    }

    @GetMapping("/list/team/{dataId}")
    @Operation(summary = "查看团队成员")
    public BaseResponse<List<UserDataTeamVO>> getDataCollaborators(@PathVariable Long dataId) {
        ThrowUtils.throwIf(dataId == null || dataId < 0, ErrorCode.PARAMS_ERROR);
        List<UserDataTeamVO> userList = userDataService.getUserDataTeam(dataId);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete/userData")
    @Operation(summary = "删除用户数据集")
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
    @Operation(summary = "显示用户所有数据集")
    public BaseResponse<List<UserData>> listUserDataInfo(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        List<UserData> userDataList = userDataService.listByPermission(loginUser);
        return ResultUtils.success(userDataList);
    }

    @GetMapping("/list/data/info/mysql")
    @Operation(summary = "显示用户MySQL数据集")
    public BaseResponse<List<UserData>> listUserMySQLDataInfo(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        List<UserData> userDataList = userDataService.listMySQLByPermission(loginUser);
        return ResultUtils.success(userDataList);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除记录")
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
    @Operation(summary = "添加记录")
    public BaseResponse<Boolean> addOneRecord(@RequestBody AddChardDataRecordRequest addChardDataRecordRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(addChardDataRecordRequest == null, ErrorCode.PARAMS_ERROR);
        Long dataId = addChardDataRecordRequest.getDataId();
        Map<String, Object> data = addChardDataRecordRequest.getData();
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id为空");
        Boolean add = mongoService.addOneRecord(dataId, data, request);
        return ResultUtils.success(add);
    }

    @PostMapping("/edit")
    @Operation(summary = "修改记录")
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
    @Operation(summary = "分页查询")
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

    private static void checkFileValid(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        // 校验文件大小
        final long ONE_MB = 10 * 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 10M");
        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
    }
}
