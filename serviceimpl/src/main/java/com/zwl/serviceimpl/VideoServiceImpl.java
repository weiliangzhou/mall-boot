package com.zwl.serviceimpl;

import com.zwl.dao.mapper.VideoMapper;
import com.zwl.model.po.Video;
import com.zwl.model.vo.QueryTypeVideoVo;
import com.zwl.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;

    @Override
    public List<Video> getWxVideoList(QueryTypeVideoVo queryTypeVideoVo) {
        List<Video> videoList = videoMapper.getWxVideoList(queryTypeVideoVo);
        for(Video video:videoList){
            if(null != video.getPlayTime()){
                Integer playTime = video.getPlayTime();
                String playTimeDesc = playTime/60 + ":" + playTime%60;
                video.setPlayTimeDesc(playTimeDesc);
            }
//            if(null==video.getModifyTime())video.setModifyTime(video.getCreateTime());
        }
        return videoList;
    }

    @Override
    public List<Video> getVideoList(Video video) {
        List<Video> videoList = videoMapper.getVideoList(video);
        for(Video video1:videoList){
            if(null != video1.getPlayTime()){
                Integer playTime = video1.getPlayTime();
                String playTimeDesc = playTime/60 + "分" + playTime%60 + "秒";
                video1.setPlayTimeDesc(playTimeDesc);
            }
//            if(null==video1.getModifyTime())video1.setModifyTime(video1.getCreateTime());
        }
        return videoList;
    }

    @Override
    public int addVideo(QueryTypeVideoVo queryTypeVideoVo) {
        return videoMapper.addVideo(queryTypeVideoVo);
    }

    @Override
    public int updateVideo(QueryTypeVideoVo queryTypeVideoVo) {
        return videoMapper.updateVideo(queryTypeVideoVo);
    }

    @Override
    public int deleteVideo(Integer id) {
        return videoMapper.deleteVideo(id);
    }

    @Override
    public Video getVideoInfoById(Video video) {
        return videoMapper.getVideoInfoById(video);
    }
}
