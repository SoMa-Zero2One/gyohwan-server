package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.compare.domain.Season;
import com.gyohwan.gyohwan.compare.dto.SeasonListResponse;
import com.gyohwan.gyohwan.compare.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;


    public SeasonListResponse findSeasons() {
        List<Season> seasons = seasonRepository.findAll();
        return SeasonListResponse.from(seasons);
    }
}
