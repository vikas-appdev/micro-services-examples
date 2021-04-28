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













Yureka zul and atomic