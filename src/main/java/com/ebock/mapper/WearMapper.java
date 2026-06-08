package com.ebock.mapper;

import com.ebock.business.Tag;
import com.ebock.business.Wear;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WearMapper {
    List<Wear> getAllWears();
    void insert(@Param("wear") Wear wear);
    void update(@Param("wear") Wear wear);
}
