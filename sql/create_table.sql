# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;


-- 生成分析表
create table chart
(
    id          bigint auto_increment comment 'id'
        primary key,
    goal        text                                   null comment '分析目标',
    name        varchar(128)                           null comment '图表名称',
    chartData   text                                   null comment '图表数据',
    chartType   varchar(128)                           null comment '图表类型',
    genChart    text                                   null comment '生成的图表数据',
    genResult   text                                   null comment '生成的分析结论',
    status      varchar(128) default 'wait'            not null comment 'wait,running,succeed,failed',
    execMessage text                                   null comment '执行信息',
    userId      bigint                                 null comment '创建用户 id',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除'
)
    comment '图表信息表' collate = utf8mb4_unicode_ci;

-- 积分领取表
create table reward_record
(
    id           bigint                             null,
    userId       bigint                             not null comment '领取用户id',
    rewardPoints int      default 0                 null comment '奖励积分',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '奖励积分领取表';

-- 服务调用记录表
create table service_record
(
    id         bigint                             not null
        primary key,
    userId     bigint                             not null comment '调用服务者',
    type       tinyint                            not null comment '调用服务类型',
    createTime datetime default CURRENT_TIMESTAMP null,
    updateTime datetime default CURRENT_TIMESTAMP null,
    isDelete   tinyint  default 0                 null
)
    comment '服务调用记录表';



-- AI助手表
-- auto-generated definition
drop table if exists ai_role;
create table ai_role
(
    id            bigint                   not null
        primary key,
    assistantName varchar(255)  default '' null comment '助手名称',
    userId         bigint                   not null comment '创建人Id',
    type          varchar(255)             null comment '助手类型',
    historyTalk   tinyint(1)    default 0  null comment '历史对话',
    functionDes   varchar(2048) default '' null comment '功能描述',
    inputModel    varchar(2048) default '' null comment '输入模型',
    roleDesign    varchar(2048)            null comment '角色设定',
    targetWork    varchar(2048) default '' null comment '目标任务',
    requirement   varchar(2048) default '' null comment '需求说明',
    style         varchar(255) default '' null comment '风格设定',
    otherRequire  varchar(2048) default '' null comment '其它示例',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除'
);

-- 用户信息表
create table user_message
(
    id          bigint                                  not null comment '消息id'
        primary key,
    userId      bigint                                  not null,
    description varchar(1024) default ''                null comment '内容',
    type        tinyint       default 0                 null comment '0-普通 1-成功 2-失败',
    title       varchar(255)  default ''                null comment '消息标题',
    createTime  datetime      default CURRENT_TIMESTAMP null,
    updateTime  datetime      default CURRENT_TIMESTAMP null,
    isDelete    tinyint       default 0                 null,
    isRead      tinyint(1)    default 0                 null comment '0 -未读 1-已读',
    route       varchar(255)  default ''                null comment '消息对应跳转的路由'
);


