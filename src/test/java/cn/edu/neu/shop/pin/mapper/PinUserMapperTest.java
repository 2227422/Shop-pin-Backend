package cn.edu.neu.shop.pin.mapper;

import static org.junit.Assert.*;

import cn.edu.neu.shop.pin.model.PinRole;
import cn.edu.neu.shop.pin.model.PinUser;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PinUserMapperTest {
    @Autowired
    PinUserMapper pinUserMapper;

    @Test
    public void findByEmail() {
        PinUser p = pinUserMapper.findByEmailA("liyifei_1999@163.com");
        System.out.println();
        System.out.println("##################################################");
        System.out.println();
        System.out.println("id: " + p.getId());
        System.out.println("email: " + p.getNickname());
//        for(Integer pr : p.getRoles()) {
//            System.out.println("PinRole: " + pr);
//        }
        System.out.println();
        System.out.println("##################################################");
        System.out.println();
    }

    @Test
    public void existsByEmail() {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        System.out.println(list.size());
    }

    @Test
    public void save() {
    }

    @Test
    public void deleteByEmail() {
    }
}