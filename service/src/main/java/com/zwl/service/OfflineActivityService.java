package com.zwl.service;

import com.zwl.model.po.OfflineActivity;

import java.util.List;

/**
 * @author 二师兄超级帅
 * @Title: OfflineActivityService
 * @ProjectName parent
 * @Description: TODO
 * @date 2018/9/2713:40
 */
public interface OfflineActivityService {
    OfflineActivity getOneByActivityId(Integer activityId);

    /**
     * 判断当前活动是否到期
     * @param activityId
     * @return
     */
    OfflineActivity getOneByActivityIdAndCheckTime(Integer activityId);

    /**
     * 根据活动主题获取详情
     * @param merchantId
     * @param themeId
     * @return
     */
    List<OfflineActivity> getOfflineActivityListByThemeId(String merchantId, String themeId);
}