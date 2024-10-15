create table member_account(
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                               userId BIGINT NOT NULL COMMENT '用户ID',
                               remainingCalls INT NOT NULL DEFAULT 0 COMMENT '剩余次数',
                               vip_expireTime TIMESTAMP DEFAULT NULL COMMENT 'VIP过期时间',
                               createTime datetime null default CURRENT_TIMESTAMP comment '创建时间',
                               updateTime datetime null default CURRENT_TIMESTAMP comment '更新时间',
                               isDelete tinyint default 0  null comment '逻辑删除'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员账户表';

CREATE TABLE user_credit_account (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                     userId BIGINT NOT NULL COMMENT '用户ID',
                                     totalAmount decimal(10,2) NOT NULL DEFAULT 0 COMMENT '总积分，显示总账户值，记得一个人获得的总积分',
                                     availableAmount decimal(10,2) NOT NULL DEFAULT 0 COMMENT '可用积分，每次扣减的值',
                                     accountStatus ENUM('open', 'close') NULL DEFAULT 'open' COMMENT '账户状态【open - 可用，close - 冻结】',
                                     createTime datetime null default CURRENT_TIMESTAMP comment '创建时间',
                                     updateTime datetime null default CURRENT_TIMESTAMP comment '更新时间',
                                     isDelete tinyint default 0  null comment '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分账户表';

