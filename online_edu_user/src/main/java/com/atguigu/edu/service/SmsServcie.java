package com.atguigu.edu.service;

public interface SmsServcie {
    void sendSms(String phoneNum);

    boolean validateSmsCode(String phoneNum, String codeUI);
}
