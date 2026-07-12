package com.ebock.mapper;

import com.ebock.business.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getAllCategories();
    void insert(@Param("category") Category category);
    void update(@Param("category") Category category);
    void delete(@Param("categoryId") int categoryId);
    int getCountById(@Param("id") int id);
}
