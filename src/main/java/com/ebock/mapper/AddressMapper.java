package com.ebock.mapper;

import com.ebock.business.Address;
import com.ebock.business.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    void insert(@Param("address") Address address);
    void update(@Param("address") Address address);
}
