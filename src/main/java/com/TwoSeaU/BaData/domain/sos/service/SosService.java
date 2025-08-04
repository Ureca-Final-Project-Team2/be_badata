package com.TwoSeaU.BaData.domain.sos.service;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.sos.dto.response.RespondSosResponse;
import com.TwoSeaU.BaData.domain.sos.dto.response.SaveSosResponse;
import com.TwoSeaU.BaData.domain.sos.entity.Sos;
import com.TwoSeaU.BaData.domain.sos.exception.SosException;
import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
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

	private final String sosMessage = "누군가 SOS를 요청하였습니다.";

	public SaveSosResponse requestSos(final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Sos savedSos = sosRepository.save(Sos.of(user));

		sseService.broadcast(sosMessage);

		return SaveSosResponse.of(savedSos.getId());
	}

	public RespondSosResponse respondSos(final Long sosId, final String username) {
		final Sos sos = sosRepository.findByIdForUpdate(sosId)
			.orElseThrow(() -> new GeneralException(SosException.SOS_NOT_FOUND));

		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final Boolean isSuccess = sos.respond(user);

		if(isSuccess) {
			sseService.sendToClient(sos.getRequester().getId(), "누군가 요청을 수락하였습니다.");
			sseService.sendToClient(user.getId(), "SOS 요청을 수락하였습니다.");
		}

		return RespondSosResponse.of(sos.getId(), isSuccess);
	}
}
