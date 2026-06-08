package com.ebock.mapper;

import com.ebock.business.Category;
import com.ebock.business.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getAllCategories();
}
