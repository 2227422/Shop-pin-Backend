package cn.edu.neu.shop.pin.consumer.controller.admin;

import cn.edu.neu.shop.pin.consumer.service.admin.AdminStoreControllerService;
import cn.edu.neu.shop.pin.consumer.service.admin.InitialControllerService;
import com.alibaba.fastjson.JSONObject;
import feign.Client;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/manager")
public class InitialController {
    @Autowired
    InitialControllerService initialControllerService;

    @Autowired
    public InitialController(
            Decoder decoder, Encoder encoder, Client client) {
        this.initialControllerService = Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(new SpringMvcContract())
                .target(InitialControllerService.class, "http://Pin-Provider");
    }

    @GetMapping(value = "/user/info")
    public JSONObject defaultLogin() {
        return initialControllerService.defaultLogin();
    }
}
