package com.TwoSeaU.BaData.domain.sos.entity;

import java.util.Objects;

import com.TwoSeaU.BaData.domain.sos.exception.SosException;
import com.TwoSeaU.BaData.domain.user.entity.User;
import com.TwoSeaU.BaData.global.common.BaseEntity;
import com.TwoSeaU.BaData.global.response.GeneralException;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "sos")
public class Sos extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_id", nullable = false)
	private User requester;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responder_id")
	private User responder;

	public static Sos of(final User requester) {

		return Sos.builder()
			.requester(requester)
			.build();
	}

	public Boolean respond(final User responder) {
		if(this.responder != null) {
			throw new GeneralException(SosException.ALREADY_RESPONDER_EXIST);
		}

		if(Objects.equals(this.requester.getId(), responder.getId())) {
			throw new GeneralException(SosException.CANNOT_RESPOND_TO_OWN_SOS);
		}

		final int sosData = 100;
		if(responder.getDataAmount() < 100) {
			throw new GeneralException(SosException.INSUFFICIENT_DATA);
		}

		this.responder = responder;
		this.requester.addData(sosData);
		this.responder.addData(-sosData);

		final int rewardCoin = 10;
		this.responder.addCoin(rewardCoin);

		return true;
	}
}
