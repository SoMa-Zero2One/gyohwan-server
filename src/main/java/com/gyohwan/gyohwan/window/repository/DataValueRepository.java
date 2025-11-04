package com.gyohwan.gyohwan.window.repository;

import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.window.domain.DataValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataValueRepository extends JpaRepository<DataValue, Long> {

    List<DataValue> findByCountry(Country country);

    List<DataValue> findByOutgoingUniv(OutgoingUniv outgoingUniv);
}

