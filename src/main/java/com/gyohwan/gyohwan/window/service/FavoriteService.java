package com.gyohwan.gyohwan.window.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.compare.repository.OutgoingUnivRepository;
import com.gyohwan.gyohwan.window.domain.UnivFavorite;
import com.gyohwan.gyohwan.window.repository.UnivFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FavoriteService {

    private final UnivFavoriteRepository univFavoriteRepository;
    private final UserRepository userRepository;
    private final OutgoingUnivRepository outgoingUnivRepository;

    @Transactional
    public void addFavorite(Long universityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        OutgoingUniv outgoingUniv = outgoingUnivRepository.findById(universityId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNIV_NOT_FOUND));

        // 이미 즐겨찾기가 있는지 확인
        if (univFavoriteRepository.existsByUserIdAndOutgoingUnivId(userId, universityId)) {
            throw new CustomException(ErrorCode.ALREADY_FAVORITED);
        }

        UnivFavorite favorite = new UnivFavorite(user, outgoingUniv);
        univFavoriteRepository.save(favorite);

        log.info("Favorite added: userId={}, univId={}", userId, universityId);
    }

    @Transactional
    public void removeFavorite(Long universityId, Long userId) {
        UnivFavorite favorite = univFavoriteRepository.findByUserIdAndOutgoingUnivId(userId, universityId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));

        univFavoriteRepository.delete(favorite);

        log.info("Favorite removed: userId={}, univId={}", userId, universityId);
    }
}

