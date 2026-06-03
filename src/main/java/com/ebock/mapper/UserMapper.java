package com.ebock.mapper;

import com.ebock.business.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User getUserInfo(@Param("cip") String cip);
}
