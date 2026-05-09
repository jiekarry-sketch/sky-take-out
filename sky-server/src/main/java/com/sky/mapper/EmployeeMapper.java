package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("insert into employee (name,username,password,phone,sex,id_number,create_time,update_time,create_user,update_user,status) " +
            "values (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    @AutoFill(value = OperationType.INSERT) //找切面类 AutoFillAspect
    //公共字段自动填充。能让你在写代码时，不再需要手动去写那堆重复的 setCreateTime、setUpdateUser 等代码。
    void insert(Employee employee);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态修改属性
     * @param e
     */
    @AutoFill(value= OperationType.UPDATE)
    void update(Employee e);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
