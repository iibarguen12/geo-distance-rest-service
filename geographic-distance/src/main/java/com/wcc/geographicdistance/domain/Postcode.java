package com.wcc.geographicdistance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postcode {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String postcode;
    private Double latitude;
    private Double longitude;
}