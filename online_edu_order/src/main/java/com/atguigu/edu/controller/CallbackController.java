package com.atguigu.edu.controller;


import com.atguigu.edu.service.EduOrderService;
import com.atguigu.response.RetVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2020-08-05
 */
//http://guli.shop/api/order/weixinPay/weixinNotify
@RestController
@RequestMapping("/api/order/weixinPay")
@CrossOrigin
public class CallbackController {
    @Autowired
    private EduOrderService orderService;
    @GetMapping("weixinNotify")
    public RetVal weixinNotify(){
        System.out.println("xxxxx");
        return RetVal.success();
    }


}

