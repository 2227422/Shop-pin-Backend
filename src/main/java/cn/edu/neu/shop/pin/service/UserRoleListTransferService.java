package cn.edu.neu.shop.pin.service;

import cn.edu.neu.shop.pin.mapper.PinUserMapper;
import cn.edu.neu.shop.pin.model.PinRole;
import cn.edu.neu.shop.pin.model.PinUser;
import cn.edu.neu.shop.pin.util.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleListTransferService {
    @Autowired
    PinUserMapper pinUserMapper;

    //通过id找用户
    public PinUser findById(Integer id) {
        PinUser pinUser = pinUserMapper.findById(id);
        List<PinRole> roles = PinUser.transfer(pinUser.getRoles());
        pinUser.setRoles(roles);
        return pinUser;
    }

    //是否存在有这个Id的用户
    public Boolean existsById(Integer id) {
        return pinUserMapper.existsById(id);
    }

    //保存用户信息
    public void save(PinUser user) {
        pinUserMapper.save(user);
    }

    //通过id删除用户
    public void deleteById(Integer id) {
        pinUserMapper.deleteById(id);
    }

}