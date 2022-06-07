package com.wcc.geographicdistance.repo;

import com.wcc.geographicdistance.domain.Postcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostcodeRepo extends JpaRepository<Postcode,Long> {
    Postcode findByPostcode(String postcode);
}
