package com.karaca.karacabank.service;

import com.karaca.karacabank.dto.ExchangeRatesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.karaca.karacabank.constants.Currency;
import java.util.Map;

@Service
public class CurrencyConversionService {
    private static final String API_BASE_URL = "http://api.exchangeratesapi.io/v1/latest";
    private static final String ACCESS_KEY = "bebe461ef523c5d7fde618482be66cd2";
    private final RestTemplate restTemplate;
    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double convertCurrency(double amount, Currency sourceCurrency, Currency targetCurrency){
        Map<String,Double> exchangeRates=fetchExchangeRates();
        double exchangeRateSourceToBase=exchangeRates.get(sourceCurrency.toString());
        double exchangeRateTargetToBase=exchangeRates.get(targetCurrency.toString());
        if(exchangeRateSourceToBase==0||exchangeRateTargetToBase==0){
            throw new RuntimeException("Exchange rates not available.");
        }
        double convertedAmountInBaseCurrency=amount/exchangeRateSourceToBase;
        return convertedAmountInBaseCurrency*exchangeRateTargetToBase;
    }

    private Map<String, Double> fetchExchangeRates() throws RuntimeException {
        String apiUrl=API_BASE_URL+"?access_key="+ACCESS_KEY+"&symbols=USD,EUR,TRY";
        ExchangeRatesResponse response=restTemplate.getForObject(apiUrl,ExchangeRatesResponse.class);
        if (response != null) return response.getRates();
        else throw new RuntimeException("Exchange rates aren't present.");
    }

}
