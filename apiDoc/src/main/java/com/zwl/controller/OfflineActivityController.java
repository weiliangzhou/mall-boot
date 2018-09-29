package com.zwl.controller;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.zwl.model.baseresult.Result;
import com.zwl.model.po.OfflineActivity;
import com.zwl.model.po.OfflineActivityTheme;
import com.zwl.model.wxpay.WxPayVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 二师兄超级帅
 * @Title: 线下活动
 * @ProjectName parent
 * @Description: TODO
 * @date 2018/9/2617:14
 */
@Api2Doc(name = "线下活动")
@RestController
public class OfflineActivityController {
    @RequestMapping(name = "线下报名",
            value = "/wx/offlineActivity/buy", method = RequestMethod.POST)
    public WxPayVo offlineActivityBuy(
            @ApiComment("activityId") Integer activityId,
            @ApiComment("realName") String realName,
            @ApiComment("0 男 1女") Integer sex,
            @ApiComment("city") String city,
            @ApiComment("phone") String phone,
            @ApiComment("idCardNum") String idCardNum
    ) {
        WxPayVo wxPayVo = new WxPayVo();
        return wxPayVo;
    }

    @RequestMapping(name = "操作员签到",
            value = "/wx/offlineActivity/offlineLogin", method = RequestMethod.POST)
    public Result operatorSignIn(@ApiComment("操作员") String operator,@ApiComment("密码") Integer password,@ApiComment("商户号") String merchantId) {
        Result result = new Result();
        return result;

    }

    @RequestMapping(name = "线下活动签到",
            value = "/wx/offlineActivity/signIn", method = RequestMethod.POST)
    public Result signIn(@ApiComment("activityCode") Integer activityCode,
                         @ApiComment("操作员") String operator) {
        Result result = new Result();
        return result;

    }

    @RequestMapping(name = "线下活动主题列表",
            value = "/wx/offlineActivity/getOfflineActivityThemeList", method = RequestMethod.POST)
    public List getOfflineActivityThemeList(@ApiComment("merchantId") String merchantId,
                                            @ApiComment("queryType  0查询推荐列表 其他查询全部") String queryType,
                                            @ApiComment("pageSize") String pageSize,
                                            @ApiComment("pageNum") String pageNum
    ) {
        List<OfflineActivityTheme> offlineActivityThemeList = new ArrayList<>();
        return offlineActivityThemeList;
    }

    @RequestMapping(name = "线下活动主题详情",
            value = "/wx/offlineActivity/getOfflineActivityListByThemeId", method = RequestMethod.POST)
    public List getOfflineActivityListByThemeId(@ApiComment("merchantId") String merchantId,
                                                @ApiComment("主题id") String themeId
    ) {
        List<OfflineActivity> offlineActivityList = new ArrayList<>();
        return offlineActivityList;
    }


}
