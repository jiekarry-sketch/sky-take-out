package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 根据类型查询菜品
     * @param type
     * @return
     */

    List<Category> list(Integer type);

    /**
     * 修改菜品分类
     * @param category
     */
    @AutoFill(value= OperationType.UPDATE)
    void update(Category category);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param category
     */
    @AutoFill(value= OperationType.INSERT)
    @Insert("insert into  category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            "values" +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void delete(Long id);

}
