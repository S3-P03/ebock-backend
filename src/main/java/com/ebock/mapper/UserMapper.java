package com.ebock.mapper;

import com.ebock.business.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User getUserInfo(@Param("cip") String cip);
    void createUser(@Param("cip") String cip,
                    @Param("email") String email,
                    @Param("firstName") String firstName,
                    @Param("lastName") String lastName);
    void updateUser(@Param("cip") String cip,
                    @Param("email") String email,
                    @Param("firstName") String firstName,
                    @Param("lastName") String lastName);
    void updateUserAddress(@Param("cip") String cip,
                           @Param("addressId") int addressId);
    int getUserCountByCip(@Param("cip") String cip);
}
