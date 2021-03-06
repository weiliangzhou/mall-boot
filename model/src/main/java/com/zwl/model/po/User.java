package com.zwl.model.po;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String userId;
    private String wechatOpenid;
    private String gzhOpenid;
    private String wechatUnionId;
    private String merchantId;
    //    从什么渠道注册。1、wechat 微信注册 2、线下导入
    private Integer registerFrom;
    private String realName;
    private String logoUrl;
    private String registerMobile;
    private String referrer;
    // 99校长   6院长 4创业教练 1学员 0会员 -1游客
    private Integer memberLevel;
    private String levelName;
    private Date expiresTime;
    private Date registerTime;
    private Date modifyTime;
    private Integer available = 1;
    //是否购买
    private Integer isBuy;
    //性别
    private Integer gender;
    //城市
    private String city;
    //省份
    private String province;
    private String teamCode;


}