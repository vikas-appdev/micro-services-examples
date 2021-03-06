package com.example.microservices.currencyconversionservice;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
//@FeignClient(name = "currency-exchange-service")
@FeignClient(name = "netflix-zuul-api-gateway-server")
public interface CurrencyExchangeServiceProxy {
	
	/*
	 * @GetMapping("/currency-exchange/from/{from}/to/{to}") public
	 * CurrencyConversionBean retrieveExchnageValue(@PathVariable String
	 * from, @PathVariable String to);
	 */
	
	@GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversionBean retrieveExchnageValue(@PathVariable String from, @PathVariable String to);

}
