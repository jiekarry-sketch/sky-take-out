package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据主键查用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long userId);

    /**
     * 查询到date日期为止所有用户数量
     * @param date
     * @return
     */
    @Select("select count(id) from user where create_time <= #{date}")
    Integer sumUsers(LocalDateTime date);

    /**
     * 查询指定日期新增用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
