package com.TwoSeaU.BaData.domain.store.service;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreLikes;
import com.TwoSeaU.BaData.domain.store.exception.StoreException;
import com.TwoSeaU.BaData.domain.store.repository.StoreLikesRepository;
import com.TwoSeaU.BaData.domain.store.repository.StoreRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreLikeService {

    private final StoreRepository storeRepository;
    private final StoreLikesRepository storeLikesRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long likeStore(final String username, final Long storeId){

        final Store store = storeRepository.findById(storeId).orElseThrow(()->new GeneralException(
                StoreException.CANT_FIND_STORE));

        final User loginUser = userRepository.findByUsername(username).orElseThrow(()-> new GeneralException(
                UserException.USER_NOT_FOUND));

        return storeLikesRepository.findByUserAndStore(loginUser, store)
                .map(storeLikes -> {
                    storeLikesRepository.delete(storeLikes);
                    return storeLikes.getId();
                })
                .orElseGet(() -> {
                    StoreLikes saved = storeLikesRepository.save(StoreLikes.of(store, loginUser));
                    return saved.getId();
                });

    }

}
