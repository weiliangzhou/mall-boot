package com.zwl.service;

import com.zwl.model.po.ClassInfo;

import java.util.List;

public interface ClassInfoService {
    /**
     * 新增
     */
    int add(ClassInfo classInfo);

    /**
     * 根据id获取节课程详情
     * @param id
     * @return
     */
    ClassInfo getById(Long id);

    /**
     * 更新指定id的参数
     * @param classInfo
     * @return
     */
    int modifyByParams(ClassInfo classInfo);

    /**
     * 获取merchantId下的所有节课程
     * @param merchantId
     * @return
     */
    List<ClassInfo> getListByMerchantId(String merchantId);
}
