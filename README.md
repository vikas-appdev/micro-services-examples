#### Microservices examples

For this series we will use group id as `com.example.microservices` and artifact id as micro-services name.

### Limit Services
- artificat `limitservices`, port `8080`

Dependencies used 

- Web 
- DevTools
- Actuator
- Config Client

`application.properties`

```
spring.application.name=limit-services

xyz.minimum=99
xyz.maximum=99999

```

Read application.properties using @ConfigurationProperties

Configuration.java

```java
@Component
@ConfigurationProperties("xyz")
public class Configuration {
	
	private int minimum;
	private int maximum;
	
	public int getMinimum() {
		return minimum;
	}
	public int getMaximum() {
		return maximum;
	}
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
}
```

Autowire Component inside RestController and read value from properties using Configuration class

```java

@RestController
public class LimitsConfigurationController {
	
	@Autowired
	private Configuration configuration;

	@GetMapping("api/v1/limits")
	public LimitConfiguration retriveLimitsFromConfigurations() {
		
		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
		
	}
}

```

#### Currency Exchange Service 
Used dependencies are `Spring Web`, `Config Client`. Optional Dependencies `Spring Boot DevTools`, `Spring Boot Actuator`.

- To make use of db using `h2database` with `spring-data-jpa`.


#### Currency Conversion Service 

Used dependencies are `Spring Web`, `Config Client`. Optional Dependencies `Spring Boot DevTools`, `Spring Boot Actuator`.



#### Invoke other micro-services using RestTemplate()

```java

@RestController
public class CorrencyConversionController {
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate()
				.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables);
		
		CurrencyConversionBean response = responseEntity.getBody();
		
		
		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
	}

}

```

#### Invoke other micro-services using FEIGN

Create a interface for proxy

```java
@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
public interface CurrencyExchangeServiceProxy {
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversionBean retrieveExchnageValue(@PathVariable String from, @PathVariable String to);

}
```

And make use of this proxy instead of RestTemplate

```java
@RestController
public class CorrencyConversionController {
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	// Same as above code but using feign proxy
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean response = proxy.retrieveExchnageValue(from, to);
		
		
		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
	}

}
```

Feign helps us to simplyfy the client code to talk to restful services.










Yureka zul and atomic