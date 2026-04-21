package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/addressBook")
@Api(tags="C端-地址簿相关接口")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增收货地址信息
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("新增地址信息:{}",addressBook);
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询收货地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById( @PathVariable Long id) {
        log.info("查询id为:{}的地址",id);
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        addressBook = addressBookService.getById(addressBook.getId());
        return  Result.success(addressBook);
    }

    /**
     * 查询当前用户所有地址信息
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        log.info("正在查询当前用户所有地址信息");
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookService.list(userId);
        return Result.success(list);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("根据用户id查询默认地址")
    public Result<AddressBook> getDefaultAddressBook() {
        log.info("正在查询默认地址...");
        Integer isDefault = 1;
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(isDefault);
        AddressBook defaultAddressBook = addressBookService.getAddressBook(addressBook);
        return Result.success(defaultAddressBook);
    }

    /**
     * 设置地址为默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefaultAddressBook(@RequestBody AddressBook addressBook) {
        log.info("设置地址:{}为默认地址",addressBook);
        addressBook = addressBookService.getAddressBook(addressBook);
        addressBook.setIsDefault(1);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateAddressBook(@RequestBody AddressBook addressBook) {
        log.info("正在修改地址id为:{}的地址",addressBook.getId());
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddressBook(Long id) {
        log.info("正在删除id为{}的地址",id);
        addressBookService.deleteById(id);
        return  Result.success();
    }
}
