package com.hwq.dataloom.service;

import com.hwq.dataloom.model.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.vo.GetUserChatHistoryVO;
import com.hwq.dataloom.utils.datasource.CustomPage;

import java.util.List;
import java.util.Map;

/**
* @author HWQ
* @description 针对表【chat】的数据库操作Service
* @createDate 2023-10-03 00:51:55
*/
public interface ChatService extends IService<Chat> {

    /**
     * 获取用户AI历史对话
     * @param loginUser
     * @return
     */
    List<GetUserChatHistoryVO> getUserChatHistory(User loginUser);

    /**
     * 添加用户对话
     * @param modelId
     * @param loginUser
     * @return
     */
    Boolean addUserChatHistory(Long modelId, User loginUser);

    /**
     * 添加智能问数对话
     * @param dataId
     * @param loginUser
     * @return
     */
    Boolean addUserAskSqlHistory(Long dataId, User loginUser);

    /**
     * 查询智能问数单条对话记录的分页数据
     * @param chatHistoryId 历史对话ID
     * @param pageNo 页下标
     * @param loginUser 请求用户
     * @return 分页数据
     */
    CustomPage<Map<String, Object>> getSingleHistoryPageData(Long chatHistoryId, Integer pageNo, User loginUser);
}
