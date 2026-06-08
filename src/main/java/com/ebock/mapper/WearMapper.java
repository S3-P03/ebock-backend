package com.ebock.mapper;

import com.ebock.business.Tag;
import com.ebock.business.Wear;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WearMapper {
    List<Wear> getAllWears();
}
