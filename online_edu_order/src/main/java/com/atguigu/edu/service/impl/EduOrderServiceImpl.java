package com.atguigu.edu.service.impl;

import com.atguigu.edu.entity.EduOrder;
import com.atguigu.edu.entity.EduPayLog;
import com.atguigu.edu.mapper.EduOrderMapper;
import com.atguigu.edu.service.EduOrderService;
import com.atguigu.edu.service.EduPayLogService;
import com.atguigu.edu.utils.HttpClient;
import com.atguigu.edu.utils.OrderNoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2020-08-05
 */
@Service
public class EduOrderServiceImpl extends ServiceImpl<EduOrderMapper, EduOrder> implements EduOrderService {
    @Value("${wx.pay.app_id}")
    private String WX_PAY_APP_ID;
    @Value("${wx.pay.mch_id}")
    private String WX_PAY_MCH_ID;
    @Value("${wx.pay.spbill_create_ip}")
    private String WX_PAY_SPBILL_IP;
    @Value("${wx.pay.notify_url}")
    private String WX_PAY_NOTIFY_URL;
    @Value("${wx.pay.xml_key}")
    private String WX_PAY_XML_KEY;

    @Autowired
    private EduPayLogService logService;


    @Override
    public String createOrder(String courseId, String memberId) {
        EduOrder order = new EduOrder();
        String orderNo = OrderNoUtil.getOrderNo();
        order.setOrderNo(orderNo);
        order.setCourseId(courseId);
        //课程信息需要通过远程RPC的形式去其他微服务中获取
        order.setCourseTitle("分库分表利器ShardingSphere");
        order.setCourseCover("http://img30.360buyimg.com/popWareDetail/jfs/t1/129741/37/5140/257375/5f199618Ebb89c553/42c62b0b187d3e07.jpg");
        //用户信息需要通过远程RPC的形式去其他微服务中获取
        order.setTeacherName("李老师");
        order.setMemberId(memberId);
        order.setNickName("enjoy6288");
        order.setMobile("13677687689");
        order.setTotalFee(new BigDecimal(0.01));
        order.setPayType(1);
        order.setStatus(0);
        baseMapper.insert(order);
        return orderNo;
    }

    @Override
    public Map<String, Object> createPayQrCode(String orderNo) {
        //根据订单号查询订单
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        EduOrder orderInfo = baseMapper.selectOne(wrapper);


        //1.需要组织传入参数 把数据组织成一个map
        Map<String, String> requestParams = new HashMap<>();
        //公众账号ID
        requestParams.put("appid",WX_PAY_APP_ID);
        //商户号
        requestParams.put("mch_id",WX_PAY_MCH_ID);
        //随机字符串
        requestParams.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        requestParams.put("body",orderInfo.getCourseTitle());
        //商户订单号
        requestParams.put("out_trade_no",orderNo);
        //标价金额 数据库单位为元 微信为分 元需要转换为分 0.01 0.011 1.1
        String totalFee=orderInfo.getTotalFee().multiply(new BigDecimal(100)).intValue()+"";
        requestParams.put("total_fee",totalFee);
        //终端IP
        requestParams.put("spbill_create_ip",WX_PAY_SPBILL_IP);
        //通知地址
        requestParams.put("notify_url",WX_PAY_NOTIFY_URL);
        //交易类型
        requestParams.put("trade_type","NATIVE");

        //2.调用统计下单接口
        try {
            String paramsXml = WXPayUtil.generateSignedXml(requestParams, WX_PAY_XML_KEY);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(paramsXml);
            httpClient.setHttps(true);
            httpClient.post();

            //3.得到返回信息
            String content = httpClient.getContent();
            Map<String, String> txRetVal = WXPayUtil.xmlToMap(content);

            Map<String, Object> retMap = new HashMap<>();
            String qrCodeUrl = txRetVal.get("code_url");
            retMap.put("qrCodeUrl",qrCodeUrl);
            retMap.put("totalFee",orderInfo.getTotalFee());
            retMap.put("orderNo",orderNo);
            retMap.put("courseId",orderInfo.getCourseId());
            return retMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }

    @Override
    public Map<String, String> queryPayState(String orderNo) {
        //a.把参数封装成xml参数
        //1.需要组织传入参数 把数据组织成一个map
        Map<String, String> requestParams = new HashMap<>();
        //公众账号ID
        requestParams.put("appid",WX_PAY_APP_ID);
        //商户号
        requestParams.put("mch_id",WX_PAY_MCH_ID);
        //商户订单号
        requestParams.put("out_trade_no",orderNo);
        //随机字符串
        requestParams.put("nonce_str", WXPayUtil.generateNonceStr());
        //b.调用接口
        try {
            String paramsXml = WXPayUtil.generateSignedXml(requestParams, WX_PAY_XML_KEY);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(paramsXml);
            httpClient.setHttps(true);
            httpClient.post();

            String content = httpClient.getContent();
            System.out.println(content);
            Map<String, String> txRetMap = WXPayUtil.xmlToMap(content);
            return txRetMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //c.得到返回值
    }

    @Override
    public void updateOrderState(Map<String, String> txRetMap) {
        //a.修改数据库里面的订单状态
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        String orderNo = txRetMap.get("out_trade_no");
        wrapper.eq("order_no",orderNo);
        EduOrder orderInfo = baseMapper.selectOne(wrapper);
        orderInfo.setStatus(1);
        baseMapper.updateById(orderInfo);

        QueryWrapper<EduPayLog> logWrapper = new QueryWrapper<>();
        logWrapper.eq("order_no",orderNo);
        EduPayLog existPaylog = logService.getOne(logWrapper);
        if(existPaylog==null){
            //b.往日志里面添加一条数据
            EduPayLog payLog = new EduPayLog();
            payLog.setOrderNo(orderNo);
            payLog.setPayTime(new Date());
            payLog.setTotalFee(orderInfo.getTotalFee());
            payLog.setTransactionId(txRetMap.get("transaction_id"));
            payLog.setTradeState(txRetMap.get("trade_state"));
            payLog.setPayType(1);
            logService.save(payLog);
        }


    }
}
