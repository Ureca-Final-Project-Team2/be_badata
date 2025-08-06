package com.TwoSeaU.BaData.domain.sos.service;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.sos.dto.response.ConnectSseResponse;
import com.TwoSeaU.BaData.domain.sos.dto.response.RespondSosResponse;
import com.TwoSeaU.BaData.domain.sos.dto.response.SaveSosResponse;
import com.TwoSeaU.BaData.domain.sos.entity.Sos;
import com.TwoSeaU.BaData.domain.sos.exception.SosException;
import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.user.entity.CoinHistory;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.enums.CoinSource;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.CoinHistoryRepository;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.TwoSeaU.BaData.global.sse.SseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SosService {

	private final UserRepository userRepository;
	private final SosRepository sosRepository;
	private final SseService sseService;
	private final CoinHistoryRepository coinHistoryRepository;

	private final String sosMessage = "누군가 SOS를 요청하였습니다.";

	public SaveSosResponse requestSos(final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Optional<Sos> latestSos = sosRepository.findFirstByRequesterIdOrderByCreatedAtDesc(user.getId());
		if(latestSos.isPresent()) {
			final YearMonth latestSosYearMonth = YearMonth.from(latestSos.get().getCreatedAt());
			final YearMonth nowYearMonth = YearMonth.now();

			if(latestSosYearMonth.equals(nowYearMonth) && latestSos.get().getResponder() != null) {
				throw new GeneralException(SosException.ALREADY_REQUEST_SOS);
			}
		}

		final Sos savedSos = sosRepository.save(Sos.of(user));

		final ConnectSseResponse connectSseResponse = ConnectSseResponse.of(savedSos, "SOS_REQUEST");
		sseService.broadcast(connectSseResponse);

		return SaveSosResponse.of(savedSos.getId());
	}

	public RespondSosResponse respondSos(final Long sosId, final String username) {
		final Sos sos = sosRepository.findByIdForUpdate(sosId)
			.orElseThrow(() -> new GeneralException(SosException.SOS_NOT_FOUND));

		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Boolean isSuccess = sos.respond(user);

		if(isSuccess) {
			final Integer rewardCoin = 10;
			coinHistoryRepository.save(CoinHistory.of(user, CoinSource.SOS, rewardCoin, user.getCoin()));
			final ConnectSseResponse connectSseResponse = ConnectSseResponse.of(sos, "SOS_RESPOND");
			sseService.broadcast(connectSseResponse);
		}

		return RespondSosResponse.of(sos.getId(), isSuccess);
	}
}
