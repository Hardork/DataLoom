package com.hwq.bi.service.impl.role_info;

/**
 * @Author:HWQ
 * @DateTime:2023/11/13 20:31
 * @Description: 角色策略接口
 **/
public interface RoleService {
    /**
     * 判断是否是当前角色
     * @return
     */
    boolean isCurrentRole(String userType);

    /**
     * 获取每日积分的数量
     * @return
     */
    Integer getDayReward();

    /**
     * 获取最大的Token数
     * @return
     */
    Integer getMaxToken();

    /**
     * 获取去往队列的标识
     * @return
     */
    String goToQueueName();

    /**
     * 获取去往交换机的routing_key
     * @return
     */
    String RoutingKey();

    /**
     * 发送消息到MQ
     * @return
     */
    void sendMessageToMQ(String message);

    /**
     * 最大上传文件大小
     * @return
     */
    Integer maxUploadFileSizeMB();

    /**
     * 获取图表保存天数
     * @return
     */
    Integer getChartSaveDay();

    /**
     * 获取对话保存信息
     * @return
     */
    Integer getChatSaveDay();
}
