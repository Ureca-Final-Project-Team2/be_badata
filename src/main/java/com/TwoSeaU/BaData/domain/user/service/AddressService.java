package com.TwoSeaU.BaData.domain.user.service;

import com.TwoSeaU.BaData.domain.store.service.GeoUtils;
import com.TwoSeaU.BaData.domain.user.dto.request.AddressCreateRequest;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAddressResponse;
import com.TwoSeaU.BaData.domain.user.dto.response.GetAddressSliceResponse;
import com.TwoSeaU.BaData.domain.user.entity.Address;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.AddressRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createAddress(final AddressCreateRequest addressCreateRequest, final String username){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()-> new GeneralException(UserException.USER_NOT_FOUND));

        final Address address = addressRepository.save(Address.of(addressCreateRequest.getDetailAddress(),
                                          loginUser,
                                          GeoUtils.makeByCoordinate(addressCreateRequest.getLongtitude(), addressCreateRequest.getLatitude()) ));

        return address.getId();
    }

    public GetAddressSliceResponse getAddressSliceResponse(final String username, final Pageable pageable){

        final User loginUser = userRepository.findByUsername(username).orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

        final Slice<Address> addressByUser = addressRepository.findByUser(loginUser, pageable);

        return GetAddressSliceResponse.of(addressByUser.getContent().stream().map(
                GetAddressResponse::from).toList(), addressByUser.hasNext());

    }

    @Transactional
    public Long deleteAddress(final String username, final Long addressId){

        final Address address = addressRepository.findById(addressId).orElseThrow(() -> new GeneralException(UserException.ADDRESS_NOT_FOUND));

        if(!address.getUser().getUsername().equals(username)){
            throw new GeneralException(UserException.CANT_DELETE_OTHER_ADDRESS);
        }

        addressRepository.delete(address);

        return addressId;
    }

}
