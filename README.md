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

#### Client side load balancing with ribbon

Add ribbon dependency

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
	<version>2.2.8.RELEASE</version>
</dependency>
```

Add annotation `@Ribbon(name="currency-exchange-service")` on feign proxy interface

Now we can remove the url from `@FeignClient()`

```java
//@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
@FeignClient(name = "currency-exchange-service")
@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchangeServiceProxy {
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversionBean retrieveExchnageValue(@PathVariable String from, @PathVariable String to);

}

```

Configure application.properties with list of urls

```
spring.application.name=currency-conversion-service
server.port=8100
spring.config.import=optional:configserver:
currency-exchnage-service.ribbon.listOfServers=http://localhost:8000, http://localhost:8001
```




### Setting up Eureka Naming Server

Create a new project with `Eureka Server`, `Config Client`, `Spring Boot Actuator` and `Spring Boot DevTools` dependencies

Annotate main class with `@EnableEurekaServer` 
```java
@SpringBootApplication
@EnableEurekaServer
public class NetflixEurekaNamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetflixEurekaNamingServerApplication.class, args);
	}

}
```

Add following inside application.properties

```
spring.application.name=netflix-eureka-naming-server
server.port=8761

spring.config.import=optional:configserver:

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

After running application eureka server should be up and running

##### Connecting services to eureka

Open pom.xml of currency-conversion-service and add following dependency

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Add @EnableDiscoveryClient like following

```java
@SpringBootApplication
@EnableFeignClients("com.example.microservices.currencyconversionservice")
@EnableDiscoveryClient
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}

}
```

Configure url for eureka inside application.properties of currency-conversion-service

```
eureka.client.service-url.default-zone=http://localhost:8761/eureka
```

Run application and it will register itself with `eureka-naming-server`.

Repeat above steps for `currency-exchange-service`

All Instance should be running with eureka after starting services.








Yureka zul and atomic