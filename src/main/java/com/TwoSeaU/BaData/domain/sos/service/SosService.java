package com.TwoSeaU.BaData.domain.sos.service;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.sos.dto.SaveSosResponse;
import com.TwoSeaU.BaData.domain.sos.entity.Sos;
import com.TwoSeaU.BaData.domain.sos.repository.SosRepository;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SosService {

	private final UserRepository userRepository;
	private final SosRepository sosRepository;

	public SaveSosResponse createSos(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		Sos savedSos = sosRepository.save(Sos.of(user));

		return SaveSosResponse.of(savedSos.getId());
	}
}
