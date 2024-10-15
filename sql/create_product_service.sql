create table product(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                        stockCount int NOT NULL COMMENT '商品库存',
                        stockCountSurplus int NOT NULL COMMENT '剩余库存',
                        productAmount decimal(10,2) NOT NULL COMMENT '商品金额【积分】',
                        productDesc varchar(30) NOT NULL COMMENT '商品描述',
                        availablePointType varchar(30) NULL COMMENT '可用优惠券列表【】',
                        productConfig varchar(30) NOT NULL COMMENT '商品配置【数量、单位】',
                        createTime datetime null default CURRENT_TIMESTAMP comment '创建时间',
                        updateTime datetime null default CURRENT_TIMESTAMP comment '更新时间',
                        isDelete tinyint default 0  null comment '逻辑删除'
) ENGINE=InnoDB AUTO_INCREMENT=1816362696870739971 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

create table product_order(
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                              productId int NOT NULL COMMENT '商品Id',
                              pointId int NOT NULL COMMENT '使用的优惠券Id',
                              payAmount decimal(10,2) NOT NULL COMMENT '支付金额',
                              state varchar(30) NOT NULL COMMENT '订单状态',
                              orderTime datetime not null COMMENT '下单时间',
                              createTime datetime null default CURRENT_TIMESTAMP comment '创建时间',
                              updateTime datetime null default CURRENT_TIMESTAMP comment '更新时间',
                              isDelete tinyint default 0  null comment '逻辑删除'
) ENGINE=InnoDB AUTO_INCREMENT=1816362696870739971 DEFAULT CHARSET=utf8mb4 COMMENT='商品订单表';