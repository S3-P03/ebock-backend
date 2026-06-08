package com.ebock.mapper;

import com.ebock.business.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> getAllTags();
    void insert(@Param("tag") Tag tag);
}
