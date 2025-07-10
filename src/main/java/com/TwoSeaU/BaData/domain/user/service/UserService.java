package com.TwoSeaU.BaData.domain.user.service;

import org.springframework.stereotype.Service;

import com.TwoSeaU.BaData.domain.user.dto.response.DataResponse;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.domain.user.exception.UserException;
import com.TwoSeaU.BaData.domain.user.repository.UserRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public DataResponse getData(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));

		return DataResponse.of(user.getDataAmount());
	}
}
