package com.karaca.karacabank.service;

import com.karaca.karacabank.constants.Currency;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {
    public double convertCurrency(double amount, Currency sourceCurrency, Currency targetCurrency){
        double exchangeRateUSDToTRY=27.0;
        double exchangeRateUSDToEUR=1.1;
        double exchangeRateTRYToUSD=0.037;
        double exchangeRateTRYToEUR=30;
        double exchangeRateEURToUSD=0.91;
        double exchangeRateEURToTRY=0.33;
        if(sourceCurrency==Currency.USD&&targetCurrency==Currency.TRY){
            return amount*exchangeRateUSDToTRY;
        }else if(sourceCurrency==Currency.USD&&targetCurrency==Currency.EUR){
            return amount*exchangeRateUSDToEUR;
        }
        else if(sourceCurrency==Currency.TRY&&targetCurrency==Currency.USD){
            return amount*exchangeRateTRYToUSD;
        }
        else if(sourceCurrency==Currency.TRY&&targetCurrency==Currency.EUR){
            return amount*exchangeRateTRYToEUR;
        }
        else if(sourceCurrency==Currency.EUR&&targetCurrency==Currency.USD){
            return amount*exchangeRateEURToUSD;
        }
        else if(sourceCurrency==Currency.EUR&&targetCurrency==Currency.TRY){
            return amount*exchangeRateEURToTRY;
        }else {
            return amount;
        }
    }
}
