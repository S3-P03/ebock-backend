package com.ebock.mapper;

import com.ebock.business.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User getUserInfo(@Param("cip") String cip);
}
