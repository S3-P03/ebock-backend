
package com.ebock.mapper;

import com.ebock.business.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    Address getAddressById(@Param("addressId") int addressId);
    void insert(@Param("address") Address address);
    void update(@Param("address") Address address);
}
