package com.zwl.dao.mapper;


import com.zwl.model.po.ClassInfoStatistics;

public interface ClassInfoStatisticsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ClassInfoStatistics record);

    int insertSelective(ClassInfoStatistics record);

    ClassInfoStatistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClassInfoStatistics record);

    int updateByPrimaryKey(ClassInfoStatistics record);
}