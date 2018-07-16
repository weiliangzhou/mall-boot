package com.zwl.service;

import com.zwl.model.po.Information;

import java.util.List;

/**
 * @author 二师兄超级帅
 * @Title: InformationService
 * @ProjectName parent
 * @Description: TODO
 * @date 2018/7/1215:07
 */
public interface InformationService {
    List<Information> getInformationList(String merchant_id);

    int addInformation(Information information);

    int updateInformation(Information information);
}
