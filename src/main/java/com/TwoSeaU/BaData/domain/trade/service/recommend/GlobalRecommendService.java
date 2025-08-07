package com.TwoSeaU.BaData.domain.trade.service.recommend;

import com.TwoSeaU.BaData.domain.trade.entity.Gifticon;
import com.TwoSeaU.BaData.domain.trade.repository.GifticonRepository;
import com.TwoSeaU.BaData.domain.trade.repository.JdbcRepository;
import com.TwoSeaU.BaData.domain.trade.service.recommend.doubleVector.PostVectorizerDouble;
import com.TwoSeaU.BaData.domain.trade.service.recommend.floatVector.PostVectorizerFloat;
import com.TwoSeaU.BaData.domain.trade.service.recommend.pgVector.VectorUtilsPg;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalRecommendService {
    private final GifticonRepository gifticonRepository;

    private final PostVectorizerDouble doubleVectorizer;
    private final PostVectorizerFloat floatVectorizer;
    private final VectorUtilsPg pgVectorUtilsPg;
    private final JdbcRepository jdbcRepository;

    @Transactional
    public String updateAllDoubleVector() {
        for (Gifticon gifticon : gifticonRepository.findAll()) {
            double[] vector = doubleVectorizer.vectorizePost(
                    gifticon.getPrice(),
                    gifticon.getDeadLine(),
                    gifticon.getCategory(),
                    gifticon.getPartner()
            );

            gifticon.updateDoubleVector(vector);
        }

        return "success";
    }

    @Transactional
    public String updateAllFloatAndPgVector() {
        for (Gifticon gifticon : gifticonRepository.findAll()) {
            float[] vector = floatVectorizer.vectorizePost(
                    gifticon.getPrice(),
                    gifticon.getDeadLine(),
                    gifticon.getCategory(),
                    gifticon.getPartner()
            );

            gifticon.updateFloatVector(vector);
            jdbcRepository.updatePostVector(gifticon.getId(), vector);
//            gifticon.updateVectorPg(
//                    vector
//                    //pgVectorUtilsPg.vectorFloatToByte(vector)
//            );
        }

        return "success";
    }
}
