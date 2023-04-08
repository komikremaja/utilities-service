package com.paypay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypay.model.UtilitiesNpwp;

@Repository
public interface UtilitiesNpwpRepo extends JpaRepository<UtilitiesNpwp, Integer>{
    UtilitiesNpwp findByNpwpNumber(String npwp);
}
