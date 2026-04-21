package com.sky.service.impl;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    AddressBookMapper addressBookMapper;
    /**
     * 新增地址信息
     * @param addressBook
     */
    public void save(AddressBook addressBook) {
        addressBookMapper.insert(addressBook);
    }

    /**
     * 动态查询
     * @param addressBook
     * @return
     */
    public AddressBook getAddressBook(AddressBook addressBook) {
        return addressBookMapper.getAddressBook(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @param userId
     * @return
     */
    public List<AddressBook> list(Long userId) {
        return addressBookMapper.list(userId);
    }

    /**
     * 更新地址信息
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }
}
