package com.atguigu.edu.controller;


import com.atguigu.edu.entity.EduOrder;
import com.atguigu.edu.service.EduOrderService;
import com.atguigu.edu.utils.JwtUtils;
import com.atguigu.response.RetVal;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2020-08-05
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
public class EduOrderController {
    @Autowired
    private EduOrderService orderService;


    //1.根据课程id下订单
    @GetMapping("createOrder/{courseId}")
    public RetVal createOrder(@PathVariable String courseId, HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        String orderNo=orderService.createOrder(courseId,memberId);
        return RetVal.success().data("orderNo",orderNo);
    }

    //2.根据订单id查询订单
    @GetMapping("getOrderByOrderNo/{orderNo}")
    public RetVal getOrderByOrderNo(@PathVariable String orderNo){
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        EduOrder orderInfo = orderService.getOne(wrapper);
        return RetVal.success().data("orderInfo",orderInfo);
    }

    //3.在微信那边下订单
    @GetMapping("createPayQrCode/{orderNo}")
    public RetVal createPayQrCode(@PathVariable String orderNo){
        Map<String, Object> retMap = orderService.createPayQrCode(orderNo);
        return RetVal.success().data(retMap);
    }

    //4.查询支付订单
    @GetMapping("queryPayState/{orderNo}")
    public RetVal queryPayState(@PathVariable String orderNo){
        Map<String, String> txRetMap = orderService.queryPayState(orderNo);
        //商品已经支付成功
        if(txRetMap.get("trade_state").equals("SUCCESS")){
            //修改订单的状态
            orderService.updateOrderState(txRetMap);
            return RetVal.success().message("支付成功");
        }else{
           return RetVal.error().message("支付失败");
        }
    }
}

