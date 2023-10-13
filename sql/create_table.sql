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



-- AI助手表（已上线）
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
drop table if exists user_message;
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

-- AI历史会话记录表
drop table if exists chat_history;
create table chat_history (
                            id bigint primary key,
                            chatRole	tinyint not null default 0 comment '0-用户 1-AI',
                            chatId  bigint  not null comment '会话id',
                            modelId bigint not null comment '助手id',
                            content varchar(2048) null comment '回应内容',
                            execMessage varchar(255) null comment '特定信息',
                            status tinyint default 0 comment '消息状态  0-正常 1-异常',
                            createTime  datetime      default CURRENT_TIMESTAMP null,
                            updateTime  datetime      default CURRENT_TIMESTAMP null,
                            isDelete    tinyint       default 0                 null
);

-- 会话记录
drop table if exists chat;
create table chat (
                             id bigint primary key,
                             userId	bigint not null comment '用户id',
                             modelId bigint not null comment '助手id',
                             createTime  datetime      default CURRENT_TIMESTAMP null,
                             updateTime  datetime      default CURRENT_TIMESTAMP null,
                             isDelete    tinyint       default 0                 null
);

-- 用户创建的助手
drop table if exists user_create_assistant;
create table user_create_assistant
(
    id     bigint not null
        primary key,
    assistantName varchar(255)  default ''                null comment '助手名称',
    userId        bigint                                  not null comment '创建人Id',
    type          varchar(255)                            null comment '助手类型',
    historyTalk   tinyint(1)    default 0                 null comment '历史对话',
    functionDes   varchar(2048) default ''                null comment '功能描述',
    inputModel    varchar(2048) default ''                null comment '输入模型',
    roleDesign    varchar(2048)                           null comment '角色设定',
    targetWork    varchar(2048) default ''                null comment '目标任务',
    requirement   varchar(2048) default ''                null comment '需求说明',
    style         varchar(255)  default ''                null comment '风格设定',
    otherRequire  varchar(2048) default ''                null comment '其它示例',
    isOnline      boolean       default false             comment '是否上线',
    createTime    datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint       default 0                 not null comment '是否删除'
)
    comment '用户创建的助手';

-- 积分商品表
create table if not exists product_point
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '产品名称',
    description    varchar(256)                           null comment '产品描述',
    userId         bigint                                 null comment '创建人',
    total          bigint                                 null comment '金额(分)',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    status         tinyint      default 0                 not null comment '商品状态（0- 默认下线 1- 上线）',
    expirationTime datetime                               null comment '商品过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '产品信息';

-- vip商品表
create table if not exists product_vip
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '产品名称',
    description    varchar(256)                           null comment '产品描述',
    userId         bigint                                 null comment '创建人',
    total          bigint                                 null comment '金额(分)',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    productType    tinyint      not null default 0 comment '产品类型（0-vip 1-sVip）',
    status         tinyint      default 0                 not null comment '商品状态（0- 默认下线 1- 上线）',
    expirationTime datetime                               null comment '商品过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '产品信息';

-- 产品订单表
create table if not exists product_order
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           not null comment '订单号',
    codeUrl        varchar(256)                           null comment '二维码地址',
    userId         bigint                                 not null comment '创建人',
    productId      bigint                                 not null comment '商品id',
    orderName      varchar(256)                           not null comment '商品名称',
    total          bigint                                 not null comment '金额(分)',
    productType	   tinyInt								  not null comment '产品类型 0-积分服务 1-会员服务',
    status         varchar(256) default 'NOTPAY'          not null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(256) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    productInfo    text                                   null comment '商品信息',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品订单';





