package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 插入地址信息
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 动态查询地址(当前:userId/userId and isDefault)
     * @param addressBook
     * @return
     */
    AddressBook getAddressBook(AddressBook addressBook);

    /**
     * 查询当前登录用户userId的所有地址信息
     * @param userId
     * @return
     */
    List<AddressBook> list(Long userId);

    /**
     * 更新地址信息
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);
}
