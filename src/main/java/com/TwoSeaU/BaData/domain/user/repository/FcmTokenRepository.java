package com.TwoSeaU.BaData.domain.user.repository;

import com.TwoSeaU.BaData.domain.user.entity.FcmToken;
import com.TwoSeaU.BaData.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken,Long> {
    Optional<FcmToken> findByUserAndToken(final User user, final String fcmToken);
}
