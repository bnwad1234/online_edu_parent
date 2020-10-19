package com.atguigu.edu.controller;

import com.atguigu.edu.service.SmsServcie;
import com.atguigu.response.RetVal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class SmsController {
    @Autowired
    private SmsServcie smsServcie;

    //1.短信发送验证码
    @GetMapping("sendSms/{phoneNum}")
    public RetVal sendSms(@PathVariable String phoneNum){
        smsServcie.sendSms(phoneNum);
        return RetVal.success();
    }
    //2.短信验证码的验证
    @GetMapping("validateSmsCode/{phoneNum}/{codeUI}")
    public RetVal validateSmsCode(@PathVariable String phoneNum,@PathVariable String codeUI){
        if(StringUtils.isEmpty(codeUI)){
            return RetVal.error().message("验证码不能为空哦 不然要打屁屁哦");
        }
        boolean flag=smsServcie.validateSmsCode(phoneNum,codeUI);
        if(flag){
            return RetVal.success();
        }else{
            return RetVal.error().message("验证码不正确");
        }

    }
}
