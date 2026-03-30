package com.example.demo.Service;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getProductsByPage(int page, int pageSize, Integer categoryId,
                                           Sort.Direction priceSort) {
        Sort sort = Sort.by(priceSort, "price");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        if (categoryId == null || categoryId == 0) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    public List<Product> GetSearchProducts(String key) {
        return productRepository.findByNameContainingIgnoreCase(key);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}
