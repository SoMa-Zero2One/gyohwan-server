package com.gyohwan.gyohwan.window.service;

import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.CountryRepository;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.compare.repository.OutgoingUnivRepository;
import com.gyohwan.gyohwan.window.domain.DataField;
import com.gyohwan.gyohwan.window.domain.DataValue;
import com.gyohwan.gyohwan.window.dto.*;
import com.gyohwan.gyohwan.window.repository.DataFieldRepository;
import com.gyohwan.gyohwan.window.repository.DataValueRepository;
import com.gyohwan.gyohwan.window.repository.UnivFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WindowService {

    private final CountryRepository countryRepository;
    private final OutgoingUnivRepository outgoingUnivRepository;
    private final DataFieldRepository dataFieldRepository;
    private final DataValueRepository dataValueRepository;
    private final UnivFavoriteRepository univFavoriteRepository;

    @Transactional(readOnly = true)
    public List<CountryListResponse> findAllCountries() {
        List<Country> countries = countryRepository.findAll();
        List<DataField> countryFields = dataFieldRepository.findByEntityType(DataField.EntityType.COUNTRY);

        return countries.stream()
                .map(country -> {
                    List<DataValue> dataValues = dataValueRepository.findByCountry(country);
                    Map<Long, DataValue> valueMap = dataValues.stream()
                            .collect(Collectors.toMap(dv -> dv.getField().getId(), dv -> dv));

                    List<DataFieldDto> data = countryFields.stream()
                            .map(field -> DataFieldDto.fromFieldWithValue(field, valueMap.get(field.getId())))
                            .collect(Collectors.toList());

                    return CountryListResponse.from(country, data);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CountryDetailResponse findCountry(String countryCode) {
        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNTRY_NOT_FOUND));

        // Country의 DataField 및 DataValue 조회
        List<DataField> countryFields = dataFieldRepository.findByEntityType(DataField.EntityType.COUNTRY);
        List<DataValue> dataValues = dataValueRepository.findByCountry(country);
        Map<Long, DataValue> valueMap = dataValues.stream()
                .collect(Collectors.toMap(dv -> dv.getField().getId(), dv -> dv));

        List<DataFieldDto> data = countryFields.stream()
                .map(field -> DataFieldDto.fromFieldWithValue(field, valueMap.get(field.getId())))
                .collect(Collectors.toList());

        // 해당 국가의 대학 조회
        List<OutgoingUniv> universities = outgoingUnivRepository.findByCountry(country);
        List<UnivSimpleDto> univDtos = universities.stream()
                .map(UnivSimpleDto::from)
                .collect(Collectors.toList());

        log.info("Country retrieved: countryCode={}, univCount={}", countryCode, universities.size());
        return CountryDetailResponse.from(country, data, univDtos);
    }

    @Transactional(readOnly = true)
    public List<UnivListResponse> findAllUniversities(Long userId) {
        List<OutgoingUniv> universities = outgoingUnivRepository.findAll();
        List<DataField> univFields = dataFieldRepository.findByEntityType(DataField.EntityType.UNIV);

        return universities.stream()
                .map(univ -> {
                    List<DataValue> dataValues = dataValueRepository.findByOutgoingUniv(univ);
                    Map<Long, DataValue> valueMap = dataValues.stream()
                            .collect(Collectors.toMap(dv -> dv.getField().getId(), dv -> dv));

                    List<DataFieldDto> data = univFields.stream()
                            .map(field -> DataFieldDto.fromFieldWithValue(field, valueMap.get(field.getId())))
                            .collect(Collectors.toList());

                    boolean isFavorite = userId != null &&
                            univFavoriteRepository.existsByUserIdAndOutgoingUnivId(userId, univ.getId());

                    return UnivListResponse.from(univ, isFavorite, data);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UnivListResponse> findUniversitiesBySeason(Long seasonId, Long userId) {
        List<OutgoingUniv> universities = outgoingUnivRepository.findBySeasonId(seasonId);
        List<DataField> univFields = dataFieldRepository.findByEntityType(DataField.EntityType.UNIV);

        return universities.stream()
                .map(univ -> {
                    List<DataValue> dataValues = dataValueRepository.findByOutgoingUniv(univ);
                    Map<Long, DataValue> valueMap = dataValues.stream()
                            .collect(Collectors.toMap(dv -> dv.getField().getId(), dv -> dv));

                    List<DataFieldDto> data = univFields.stream()
                            .map(field -> DataFieldDto.fromFieldWithValue(field, valueMap.get(field.getId())))
                            .collect(Collectors.toList());

                    boolean isFavorite = userId != null &&
                            univFavoriteRepository.existsByUserIdAndOutgoingUnivId(userId, univ.getId());

                    return UnivListResponse.from(univ, isFavorite, data);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnivDetailResponse findUniversity(Long universityId) {
        OutgoingUniv univ = outgoingUnivRepository.findById(universityId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNIV_NOT_FOUND));

        List<DataField> univFields = dataFieldRepository.findByEntityType(DataField.EntityType.UNIV);
        List<DataValue> dataValues = dataValueRepository.findByOutgoingUniv(univ);
        Map<Long, DataValue> valueMap = dataValues.stream()
                .collect(Collectors.toMap(dv -> dv.getField().getId(), dv -> dv));

        List<DataFieldDto> data = univFields.stream()
                .map(field -> DataFieldDto.fromFieldWithValue(field, valueMap.get(field.getId())))
                .collect(Collectors.toList());

        log.info("University retrieved: univId={}", universityId);
        return UnivDetailResponse.from(univ, data);
    }
}

