package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.entity.Address;
import com.TwoSeaU.BaData.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {

    Slice<Address> findByUser(final User user, final Pageable pageable);
}
