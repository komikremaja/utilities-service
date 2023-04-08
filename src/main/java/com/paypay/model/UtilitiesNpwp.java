package com.paypay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "utilities_npwp_data")
public class UtilitiesNpwp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_npwp")
    private Integer idNpwp;

    @Column(name = "npwp_number")
    private String npwpNumber;

    @Column(name = "npwp_status")
    private String npwpStatus;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
