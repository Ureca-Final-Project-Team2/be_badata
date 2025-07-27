package com.TwoSeaU.BaData.domain.store.repository;

import com.TwoSeaU.BaData.domain.store.entity.Store;
import com.TwoSeaU.BaData.domain.store.entity.StoreLikes;
import com.TwoSeaU.BaData.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikesRepository extends JpaRepository<StoreLikes, Long>, StoreLikesQueryRepository {
    Optional<StoreLikes> findByUserAndStore(final User user, final Store store);

    boolean existsByUserIdAndStoreId(final Long userId, final Long storeId);
}
