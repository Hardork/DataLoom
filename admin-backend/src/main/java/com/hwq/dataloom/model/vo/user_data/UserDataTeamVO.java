package com.hwq.dataloom.model.vo.user_data;

import com.hwq.dataloom.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/10
 * @Description:
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDataTeamVO {
    // 用户信息
    private UserVO userVO;
    // 权限
    private Integer permission;
    // 角色
    private Integer role;
}
