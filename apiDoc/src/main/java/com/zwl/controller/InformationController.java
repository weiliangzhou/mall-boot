//package com.zwl.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.terran4j.commons.api2doc.annotations.Api2Doc;
//import com.terran4j.commons.api2doc.annotations.ApiComment;
//import com.zwl.model.baseresult.Result;
//import com.zwl.model.po.Information;
//import com.zwl.service.InformationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
///**
// * @author 二师兄超级帅
// * @Title: 资讯列表
// * @ProjectName parent
// * @Description: TODO
// * @date 2018/7/1215:04
// */
//@Api2Doc(name = "资讯管理")
//@RestController
//public class InformationController {
//    @Autowired
//    private InformationService informationService;
//
//    @ApiComment("资讯添加")
//    @RequestMapping(name = "资讯添加",
//            value = "/teacher/information/add", method = RequestMethod.POST)
//    public String addInformation(@ApiComment("商户号") String merchantId, @ApiComment("标题") String title, @ApiComment("内容") String content, @ApiComment("url") String logoUrl, @ApiComment("音频") String audioUrl) {
//        Result result = new Result();
//        return JSONObject.toJSONString(result);
//    }
//
//    @ApiComment("资讯编辑")
//    @RequestMapping(name = "资讯编辑",
//            value = "/teacher/information/update", method = RequestMethod.POST)
//    public String updateInformation(@ApiComment("id") String id, @ApiComment("商户号") String merchantId, @ApiComment("标题") String title, @ApiComment("内容") byte[] content, @ApiComment("图片") String logoUrl, @ApiComment("音频") String audioUrl) {
//        Result result = new Result();
//        return JSONObject.toJSONString(result);
//    }
//    @ApiComment("资讯删除")
//    @RequestMapping(name = "资讯删除",
//            value = "/teacher/information/delete", method = RequestMethod.POST)
//    public String deleteInformation(@ApiComment("id") String id){
//        Result result = new Result();
//        return JSONObject.toJSONString(result);
//    }
//
//
//
//    @ApiComment(value = "获取资讯列表", seeClass = Information.class)
//    @RequestMapping(name = "获取资讯列表",
//            value = "/teacher/information/getInformationList", method = RequestMethod.POST)
//    public Information getInformationList(@ApiComment("pageNum") Integer pageNum, @ApiComment("pageSize") Integer pageSize, @ApiComment("商户号") String merchantId, @ApiComment("标题") String title) {
//        Information information = new Information();
//        return information;
//    }
//
//
//}
