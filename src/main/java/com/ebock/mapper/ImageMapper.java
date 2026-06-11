package com.ebock.mapper;

import com.ebock.business.Category;
import com.ebock.business.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageMapper {
    Image getImageFromGuid(@Param("guid") String guid);
    void insert(@Param("image") Image image);
}
