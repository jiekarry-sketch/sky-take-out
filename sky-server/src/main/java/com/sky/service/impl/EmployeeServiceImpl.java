package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对（兼容旧MD5密码，自动升级为BCrypt）
        String storedPassword = employee.getPassword();
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            // 新密码格式：BCrypt验证
            if (!passwordEncoder.matches(password, storedPassword)) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }
        } else {
            // 旧密码格式：MD5验证，验证通过后自动升级为BCrypt
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
            if (!md5Password.equals(storedPassword)) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }
            // 自动将旧MD5密码升级为BCrypt
            employee.setPassword(passwordEncoder.encode(password));
            employeeMapper.update(employee);
            log.info("员工{}的密码已自动从MD5升级为BCrypt", employee.getUsername());
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝(注意DTO和实体类的属性名要一致)
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置账号的状态,默认正常.1表示正常，0表示锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码,数据库存放的是BCrypt加密后的password
        employee.setPassword(passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD));

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> p = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = p.getTotal();
        List<Employee> l = p.getResult();
        return new PageResult(total,l);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //update emp set status =? where id =?
        /*Employee e = new Employee();
        e.setStatus(status);
        e.setId(id);*/
        Employee e = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(e);
    }

    /**
     * 根据id查询员工信息
     * @param id
     */
    public Employee getById(Long id){
        Employee e = employeeMapper.getById(id);
        if(e!=null){
            e.setPassword("*****"); // 加强安全性
        }
        return e;
    }

    /**
     * 编辑员工信息
     * @param ed
     */
    public void update(EmployeeDTO ed){
        Employee e =new Employee();
        BeanUtils.copyProperties(ed,e);
        e.setUpdateTime(LocalDateTime.now());
        e.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(e);
    }
}
