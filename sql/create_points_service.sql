
-- 优惠券模版表
drop table if exists coupon_template;
create table coupon_template(
                                `id`			bigint(20)		PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                `name`		    varchar(256)	DEFAULT NULL COMMENT '优惠券名称',
                                `description`	text			DEFAULT NULL COMMENT '描述',
                                `type`			tinyint(1)		DEFAULT NULL COMMENT '类型',
                                `validStartTime` datetime		DEFAULT NULL COMMENT '有效期开始时间',
                                `validEndTime`   datetime		DEFAULT NULL COMMENT '有效期截止时间',
                                `stock`			int(11)			DEFAULT NULL COMMENT '优惠券发行量',
                                `claimRules`	json			DEFAULT NULL COMMENT	'领取规则',
                                `usageRules`	json			DEFAULT NULL COMMENT	'使用规则',
                                `status`	    tinyint(1)		DEFAULT 0 COMMENT	'0-使用中 1-正常下线',
                                createTime      datetime           null default CURRENT_TIMESTAMP comment '创建时间',
                                updateTime      datetime           null default CURRENT_TIMESTAMP comment '更新时间',
                                isDelete        tinyint            default 0  null comment '逻辑删除'
);

-- 优惠券发放任务表
DROP TABLE if exists coupon_task;
CREATE TABLE `coupon_task`
(
    `id`                 bigint(20) PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `batchId`           bigint(20) DEFAULT NULL COMMENT '批次ID',
    `taskName`          varchar(128) DEFAULT NULL COMMENT '优惠券批次任务名称',
    `sendNum`           int(11) DEFAULT NULL COMMENT '发放优惠券数量',
    `userListFilePath`  varchar(255) DEFAULT NULL COMMENT '发放用户列表文件地址',
    `notifyType`        varchar(32)  DEFAULT NULL COMMENT '通知方式，可组合使用 0：站内信 1：弹框推送 2：邮箱 3：短信',
    `couponTemplateId` bigint(20) DEFAULT NULL COMMENT '优惠券模板ID',
    `sendType`          tinyint(1) DEFAULT NULL COMMENT '发送类型 0：立即发送 1：定时发送',
    `sendTime`          datetime     DEFAULT NULL COMMENT '发送时间',
    `status`             tinyint(1) DEFAULT NULL COMMENT '状态 0：待执行 1：执行中 2：执行失败 3：执行成功 4：取消',
    `completionTime`    datetime     DEFAULT NULL COMMENT '完成时间',
    `operatorId`        bigint(20) DEFAULT NULL COMMENT '操作人 -- 管理源id',
    createTime      datetime           null default CURRENT_TIMESTAMP comment '创建时间',
    updateTime      datetime           null default CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint            default 0  null comment '逻辑删除',
    KEY                  `idx_batch_id` (`batchId`) USING BTREE,
    KEY                  `idx_coupon_template_id` (`couponTemplateId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1816362696870739971 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板发送任务表';


-- 发放优惠券失败记录
DROP TABLE if exists coupon_task_fail_record;
CREATE TABLE `coupon_task_fail_record`
(
    `id`          bigint(20) PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `batchId`    bigint(20) NOT NULL COMMENT '批次ID',
    `userId`     bigint(20) NOT NULL COMMENT '用户ID',
    `couponTemplateId` bigint(20) DEFAULT NULL COMMENT '优惠券模板ID',
    `failedContent` text COMMENT '失败内容',
    createTime      datetime           null default CURRENT_TIMESTAMP comment '创建时间',
    updateTime      datetime           null default CURRENT_TIMESTAMP comment '更新时间',
    `operatorId`        bigint(20) DEFAULT NULL COMMENT '操作人 -- 管理源id',
    isDelete        tinyint            default 0  null comment '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券发放任务失败详情表';

-- 用户领取优惠券记录
DROP TABLE if exists user_coupon;
CREATE TABLE `user_coupon`
(
    `id`                 bigint(20) PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `userId`            bigint(20) DEFAULT NULL COMMENT '用户ID',
    `couponTemplateId` bigint(20) DEFAULT NULL COMMENT '优惠券模板ID',
    `receiveTime`       datetime DEFAULT NULL COMMENT '领取时间',
    `receiveCount`      int(3) DEFAULT NULL COMMENT '领取次数',
    `validStartTime`   datetime DEFAULT NULL COMMENT '有效期开始时间',
    `validEndTime`     datetime DEFAULT NULL COMMENT '有效期结束时间',
    `useTime`           datetime DEFAULT NULL COMMENT '使用时间',
    `source`             tinyint(1) DEFAULT NULL COMMENT '券来源 0：领券中心 1：平台发放 2：店铺领取',
    `status`             tinyint(1) DEFAULT NULL COMMENT '状态 0：未使用 1：锁定 2：已使用 3：已过期 4：已撤回',
    createTime      datetime           null default CURRENT_TIMESTAMP comment '创建时间',
    updateTime      datetime           null default CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint            default 0  null comment '逻辑删除',
    UNIQUE KEY `idx_user_id_coupon_template_receive_count` (`userId`,`couponTemplateId`,`receiveCount`) USING BTREE,
    KEY                  `idx_user_id` (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1816074493920030734 DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';


-- 优惠券提示
CREATE TABLE `coupon_template_remind`
(
    `userId`            bigint(20) NOT NULL COMMENT '用户ID',
    `couponTemplateId` bigint(20) NOT NULL COMMENT '券ID',
    `information`        bigint(20) DEFAULT NULL COMMENT '存储信息',
    `startTime`         datetime DEFAULT NULL COMMENT '优惠券开抢时间',
    PRIMARY KEY (`userId`, `couponTemplateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户预约提醒信息存储表';