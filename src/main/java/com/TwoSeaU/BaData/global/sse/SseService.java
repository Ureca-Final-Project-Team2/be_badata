package com.TwoSeaU.BaData.global.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.TwoSeaU.BaData.domain.sos.exception.SosException;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseService {

	private final UserRepository userRepository;
	private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

	public SseEmitter subscribe(final String username) {
		final User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		final SseEmitter sseEmitter = new SseEmitter(60 * 60 * 1000L);
		emitterMap.put(user.getId(), sseEmitter);

		sseEmitter.onCompletion(() -> emitterMap.remove(user.getId()));
		sseEmitter.onTimeout(() -> emitterMap.remove(user.getId()));
		sseEmitter.onError((e) -> emitterMap.remove(user.getId()));

		sendToClient(user.getId(), "SSE 연결 성공");
		return sseEmitter;
	}

	public void sendToClient(final Long userId, String message) {
		final SseEmitter sseEmitter = emitterMap.get(userId);

		if(sseEmitter != null) {
			try {
				sseEmitter.send(SseEmitter.event()
					.name("sos")
					.data(message)
				);
			} catch (IOException e) {
				emitterMap.remove(userId);
				throw new GeneralException(SosException.CANNOT_CONNECT_SSE);
			}
		}
	}

	public void broadcast(final String sosMessage) {
		final List<Long> userIds = new ArrayList<>(emitterMap.keySet());
		final List<User> users = userRepository.findAllById(userIds);

		users.stream()
			.filter(User::getIsNotificationEnabled)
			.forEach(user -> sendToClient(user.getId(), sosMessage));
	}
}