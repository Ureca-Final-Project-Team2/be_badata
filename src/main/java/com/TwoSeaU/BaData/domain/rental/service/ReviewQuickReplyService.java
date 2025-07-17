package com.TwoSeaU.BaData.domain.rental.service;

import com.TwoSeaU.BaData.domain.rental.dto.response.ShowQuickReplyResponse;
import com.TwoSeaU.BaData.domain.rental.entity.QuickReply;
import com.TwoSeaU.BaData.domain.rental.repository.QuickReplyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQuickReplyService {

    private final QuickReplyRepository quickReplyRepository;

    public List<ShowQuickReplyResponse> getReviewQuickReplies(){

        return quickReplyRepository.findAll().stream().map(ShowQuickReplyResponse::from).toList();
    }

}
