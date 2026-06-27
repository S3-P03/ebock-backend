package com.ebock.mapper;

import com.ebock.business.Image;
import com.ebock.dto.response.image.ItemImageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageMapper {
    Image getImageFromGuid(@Param("guid") String guid);
    List<ItemImageResponse> getItemImages(@Param("id") int id);
    void insert(@Param("image") Image image);
}
