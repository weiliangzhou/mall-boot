package com.zwl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zwl.model.baseresult.Result;
import com.zwl.model.baseresult.ResultCodeEnum;
import com.zwl.model.exception.BSUtil;
import com.zwl.model.po.*;
import com.zwl.model.vo.UserLoginInfoVo;
import com.zwl.service.*;
import com.zwl.serviceimpl.RedisTokenManagerImpl;
import com.zwl.util.ThreadVariable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户controller
 */
@Slf4j
@RestController
@RequestMapping("/wx/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MiniAppWeChatService miniAppWeChatService;
    @Autowired
    private RedisTokenManagerImpl tokenManager;
    @Autowired
    private MsgSenderService msgSenderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CertificationService certificationService;
    @Autowired
    private MaidInfoService maidInfoService;
    @Autowired
    private UserAccountService userAccountService;


    /**
     * 用户小程序微信授权登录
     */
    @PostMapping("/authorization")
    public Result authorization(@RequestBody UserLoginInfoVo userLoginInfoVo) {
        log.info("====@@@@进入用户授权@@@@@==========");
        log.info("====@@@@推荐人传入参数为@@@@@==========：" + userLoginInfoVo.getReferrer());
        Result result = new Result();
        //根据merchantid获取appid和secret
        Merchant merchant = merchantService.getMerchantByMerchantId(userLoginInfoVo.getMerchantId());
        if (merchant == null)
            BSUtil.isTrue(false, "商户不存在");
        //根据code获取openid 去掉数据库appid和appsecret的空格和换行等
        String resultStr = miniAppWeChatService.authorizationCode(userLoginInfoVo.getCode(), merchant.getAppId(), merchant.getAppSecret());
        if (StringUtils.isBlank(resultStr))
            BSUtil.isTrue(false, "获取不到微信用户信息");
        JSONObject resultJson = JSON.parseObject(resultStr);
        String errcode = resultJson.getString("errcode");
        String errmsg = resultJson.getString("errmsg");
        String openid = resultJson.getString("openid");
        if (StringUtils.isNotBlank(resultJson.getString("errcode"))) {
            result.setCode(errcode);
            result.setMessage("微信返回错误信息：" + errmsg);
            return result;
        }
        //先查询用户之前是否授权登录过
        User userQuery = new User();
        userQuery.setWechatOpenid(openid);
        userQuery.setMerchantId(userLoginInfoVo.getMerchantId());
        userQuery = userService.getOneByParams(userQuery);
        String registerMobile = userLoginInfoVo.getRegisterMobile();
        String userId;
        if (userQuery == null) {//用户之前没授权登录过
            boolean isStockData = StringUtils.isBlank(registerMobile) ? false : userService.findStockData(registerMobile);
            if (isStockData)//存量数据需要更新openid
                userId = userService.updateUserStockDataByRegisterMobile(registerMobile, openid);
            else  // 并且不是存量数据
            {
                //手机号码防重
                User queryUser = new User();
                queryUser.setRegisterMobile(registerMobile);
                User validate_user = userService.getOneByParams(queryUser);
                if (validate_user != null)
                    BSUtil.isTrue(false, "已存在该手机号码");
                userId = userService.saveAuthorization(userLoginInfoVo, openid);
            }
        } else {//如果用户还未购买，则可以更新推荐人
            userId = userQuery.getUserId();
            log.info("====@@@@用户之前已经授权登录过，userId为：@@@@@==========：" + userId);
            userService.modifyAuthorization(userLoginInfoVo, userQuery);
        }
        //返回用户登录态
        TokenModel model = tokenManager.createToken(userId);
        String token = model.getSignToken();
        Map resultMap = new HashMap<String, Object>();
        resultMap.put("token", token);
        resultMap.put("userId", userId);
        result.setData(resultMap);
        return result;
    }


    //购买前绑定手机号
    @PostMapping("/bindingMobile")
    public Result bindMobile(@RequestBody JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String msgCode = jsonObject.getString("msgCode");
        String userId = jsonObject.getString("userId");
//        需要手机号码防重
        User queryUser = new User();
        queryUser.setRegisterMobile(phone);
        User validate_user = userService.getOneByParams(queryUser);
        if (validate_user != null)
            BSUtil.isTrue(false, "已存在该手机号码");

        //  校验验证码
        boolean isValidate = msgSenderService.checkCode(phone, msgCode, "1");
        if (!isValidate)
            BSUtil.isTrue(false, "验证码错误");
        Result result = new Result();
        //更新用户表
        User user = new User();
        user.setUserId(userId);
        user.setRegisterMobile(phone);
        //绑定手机成为会员
        user.setMemberLevel(0);
        int count = userService.updateUserByUserId(user);
        if (count == 0)
            BSUtil.isTrue(false, "绑定失败");
        //更新用户详情表
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setRegisterMobile(phone);
        userInfo.setIsBindMobile(1);
        userInfoService.modifyByParams(userInfo);

        return result;
    }

    @PostMapping("/sendMsgCode")
    public Result sendRegisterCode(@RequestBody JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String busCode = jsonObject.getString("busCode");
        msgSenderService.sendCode(phone, busCode);
        Result result = new Result();
        return result;
    }

    @PostMapping("/checkCode")
    public Result checkCode(@RequestBody JSONObject jsonObject) {
        String phone = jsonObject.getString("phone");
        String busCode = jsonObject.getString("busCode");
        String code = jsonObject.getString("code");
        boolean flag = msgSenderService.checkCode(phone, code, busCode);
        if (!flag)
            BSUtil.isTrue(false, "验证码错误");
        Result result = new Result();
        return result;
    }


    /**
     * 用户信息展示
     */
    @PostMapping("/getUserInfoByUserId")
    public Result getUserInfoByUserId(@RequestBody JSONObject jsonObject) {
        String userId_local = ThreadVariable.getUserID();
        String userId_param = jsonObject.getString("userId");
        String userId = org.apache.commons.lang3.StringUtils.isBlank(userId_local) ? userId_param : userId_local;
        Result result = new Result();
        //根据UserId查找用户详情表
        UserInfo userInfo = userInfoService.getByUserId(userId);
        UserLoginInfoVo userLoginInfoVo = new UserLoginInfoVo();
        if (userInfo != null) {
            userLoginInfoVo.setNickName(userInfo.getNickName());
            userLoginInfoVo.setLogoUrl(userInfo.getLogoUrl());
        }

        User user = userService.getByUserId(userId);
        if (user == null) {
            result.setCode(ResultCodeEnum.EXCEPTION);
            result.setMessage("查无用户，请校验userId");
            return result;
        }
        Integer memberLevel = user.getMemberLevel();
        String levelName;
        if (null == memberLevel || memberLevel == -1) {
            levelName = "游客";
        } else if (memberLevel == 0) {
            levelName = "会员";
        } else {
            Product product = productService.getProductByMemberLevel(memberLevel);
            levelName = product.getLevelName();
        }
        log.info("memberLevel::" + memberLevel);
        userLoginInfoVo.setMemberLevel(null == memberLevel ? -1 : memberLevel);
        userLoginInfoVo.setLevelName(levelName);
//        userLoginInfoVo.setIsBindMobile(userInfo.getIsBindMobile()==null?0:1);
        //通过主表获取绑定手机号
        userLoginInfoVo.setIsBindMobile(user.getRegisterMobile() == null ? 0 : 1);
        userLoginInfoVo.setRegisterMobile(user.getRegisterMobile());
//        userLoginInfoVo.setIsCertification(userInfo.getIsCertification() == null ? 0 : 1);
        //实名认证状态
        UserCertification userCertification = certificationService.getOneByUserId(userId);
        userLoginInfoVo.setCertificationStatus(userCertification.getStatus());
        Integer xiaxianCount = maidInfoService.getMaidInfoCount(userId);
        userLoginInfoVo.setXiaxianCount(null == xiaxianCount ? 0 : xiaxianCount);
        //账户余额
        Integer balance = userAccountService.getBalanceByUserId(userId);
        //余额：分转元
        balance = balance == null ? 0 : balance / 100;
        userLoginInfoVo.setBalance(balance);

        result.setData(userLoginInfoVo);
        return result;
    }

    /**
     * 分享绑定上下级关系
     */
    @PostMapping("/shareRelation")
    public Result shareRelation(@RequestBody JSONObject jsonObject) {
        String referrer = jsonObject.getString("referrer");
        String userId = jsonObject.getString("userId");
//        String merchantId = jsonObject.getString("merchantId");
        log.info("====@@@@进入用户授权@@@@@==========userId：" + userId);
        log.info("====@@@@推荐人传入参数为@@@@@==========：" + referrer);
        Result result = new Result();
        User userQuery = userService.getByUserId(userId);
        if (userQuery == null) {
            result.setCode(ResultCodeEnum.EXCEPTION);
            result.setMessage("查无用户，请检查userId");
            log.error("==============@@@@@@@@分享绑定上下级关系@@用户推荐人referrer：" + "为" + referrer + "的用户的userid不存在");
            return result;
        }

        //如果用户还未购买，则可以更新推荐人
        if ((userQuery.getIsBuy() == null || userQuery.getIsBuy() == 0) && StringUtils.isNotBlank(referrer)) {
            User user = new User();
            user.setUserId(userQuery.getUserId());
            //推荐人userId 推荐人必须购买过
            User userIsBuy = userService.getByUserId(referrer);
            if (userIsBuy == null) {
                result.setCode(ResultCodeEnum.EXCEPTION);
                result.setMessage("推荐人不存在，请检查referrer！");
                return result;
            }
            if (userIsBuy.getIsBuy() != null && userIsBuy.getIsBuy() == 1) {
                user.setReferrer(referrer);
                userService.updateUserByUserId(user);
            } else {

                result.setCode(ResultCodeEnum.EXCEPTION);
                result.setMessage("推荐人还未购买，不绑定关系！");
            }
        } else {
            result.setCode(ResultCodeEnum.EXCEPTION);
            result.setMessage("该用户已经购买，不再改绑推荐人！");
        }

        return result;
    }
}
