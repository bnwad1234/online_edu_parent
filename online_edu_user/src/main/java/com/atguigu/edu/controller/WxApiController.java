package com.atguigu.edu.controller;

import com.atguigu.edu.entity.MemberCenter;
import com.atguigu.edu.service.MemberCenterService;
import com.atguigu.edu.utils.HttpClientUtils;
import com.atguigu.edu.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx/")
@CrossOrigin
public class WxApiController {
    @Value("${wx.open.app_id}")
    private String WX_OPEN_APPID;
    @Value("${wx.open.app_secret}")
    private String WX_OPEN_APPSECRET;
    @Value("${wx.open.redirect_url}")
    private String WX_OPEN_RETURL;

    @Autowired
    private MemberCenterService memberCenterService;

    //1.生成二维码
    @GetMapping("login")
    public String qrCode() throws Exception{
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //对回调地址进行编码
        String encodedUrl = URLEncoder.encode(WX_OPEN_RETURL, "UTF-8");
        String state="atguigu";
        String qrCodeUrl = String.format(baseUrl, WX_OPEN_APPID, encodedUrl, state);
        return "redirect:"+qrCodeUrl;
    }

    //2.编写扫描成功之后的回调接口
    @GetMapping("callback")
    public String callback(String code) throws Exception{
        //a.通过code结合appid和app-secret获取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        baseAccessTokenUrl = String.format(baseAccessTokenUrl, WX_OPEN_APPID, WX_OPEN_APPSECRET, code);
        //模拟发起一次http请求
        String retVal = HttpClientUtils.get(baseAccessTokenUrl);
        //通过Gson先把json字符串转换为map对象
        Gson gson = new Gson();
        Map infoMap = gson.fromJson(retVal, HashMap.class);
        String accessToken =(String)infoMap.get("access_token");
        String openId =(String)infoMap.get("openid");

        //b.通过accessToken和openId获取用户个人信息
        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                "?access_token=%s" +
                "&openid=%s";
        userInfoUrl = String.format(userInfoUrl, accessToken, openId);
        //模拟发起一次http请求
        String userValue = HttpClientUtils.get(userInfoUrl);
        Map userMap = gson.fromJson(userValue, HashMap.class);
        String nickname =(String)userMap.get("nickname");
        String headImage =(String)userMap.get("headimgurl");

        //查询数据库里面是否已经存在该用户
        QueryWrapper<MemberCenter> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openId);
        MemberCenter existMemberCenter = memberCenterService.getOne(wrapper);
        if(existMemberCenter==null) {
            //把个人的用户信息保存起来
            existMemberCenter = new MemberCenter();
            existMemberCenter.setNickname(nickname);
            existMemberCenter.setAvatar(headImage);
            existMemberCenter.setOpenid(openId);
            memberCenterService.save(existMemberCenter);
        }
        String token = JwtUtils.geneJsonWebToken(existMemberCenter);
        return "redirect:http://127.0.0.1:3000?token="+token;
    }

}
