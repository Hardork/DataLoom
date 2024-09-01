-- 定义最大值和位数
local SECOND_FIELD_BITS = 13

-- 将两个字段组合成一个int
local function combineFields(firstField, secondField)
    local firstFieldValue = firstField and 1 or 0
    -- 将第一个字段值左移13位，将后13位填充第二个字段
    return (firstFieldValue * 2 ^ SECOND_FIELD_BITS) + secondField
end

-- Lua脚本开始
local key = KEYS[1] -- Redis Key
local userSetKey = KEYS[2] -- 用户领券 Set 的 Key (记录已经领券的用户)
local userIdAndRowNum = ARGV[1] -- 用户 ID 和 Excel 所在行数

-- 获取库存
local stock = tonumber(redis.call('HGET', key, 'stock'))

-- 检查库存是否大于0
if stock == nil or stock <= 0 then
    -- 返回失败以及当前成功领券的用户数量
    return combineFields(false, redis.call('SCARD', userSetKey))
end

-- 自减库存
redis.call('HINCRBY', key, 'stock', -1)

-- 添加用户到领券集合
redis.call('SADD', userSetKey, userIdAndRowNum)

-- 获取用户领券集合的长度
local userSetLength = redis.call('SCARD', userSetKey)

-- 返回失败以及当前成功领券的用户数量
return combineFields(true, userSetLength)
