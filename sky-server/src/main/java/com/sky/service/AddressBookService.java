package com.sky.service;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址信息
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */

    AddressBook getById(Long id);
    /**
     * 动态查询
     * @param addressBook
     * @return
     */
    AddressBook getAddressBook(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @param id
     * @return
     */
    List<AddressBook> list(Long id);

    /**
     * 更新地址信息
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

}
