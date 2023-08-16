package com.karaca.karacabank.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRatesResponse {
    private Map<String,Double> rates;
}
