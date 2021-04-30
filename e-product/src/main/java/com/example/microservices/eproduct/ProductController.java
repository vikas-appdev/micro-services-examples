package com.example.microservices.eproduct;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
	
	@Autowired
	private ProductRepository productRepository;
	
	@PostMapping("/products")
	public void saveProduct(@RequestBody ProductModel model) {
		productRepository.save(model);
	}
	
	@GetMapping("/products")
	public List<ProductModel> getAllProducts() {
		return productRepository.findAll();
	}
	
	@GetMapping("/products/{id}")
	public ProductModel getProductById(@PathVariable Long id) {
		Optional<ProductModel> optional = productRepository.findById(id);
		
		if(!optional.isPresent()) {
			throw new RuntimeException("No product found with given id: "+id);
		}
		
		return optional.get();
	}

}
