package mk.ukim.finki.wp.exam.example.service.impl;

import mk.ukim.finki.wp.exam.example.model.Category;
import mk.ukim.finki.wp.exam.example.model.Product;
import mk.ukim.finki.wp.exam.example.model.exceptions.InvalidCategoryIdException;
import mk.ukim.finki.wp.exam.example.model.exceptions.InvalidProductIdException;
import mk.ukim.finki.wp.exam.example.repository.CategoryRepository;
import mk.ukim.finki.wp.exam.example.repository.ProductRepository;
import mk.ukim.finki.wp.exam.example.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        Optional<Product> prod = productRepository.findById(id);
        if(prod.isPresent())
            return prod.get();
        throw new InvalidProductIdException();
    }

    @Override
    public Product create(String name, Double price, Integer quantity, List<Long> categories) {
        List<Category> categoryList = categoryRepository.findAllById(categories);
        return productRepository.save(new Product(name,price,quantity,categoryList));
    }

    @Override
    public Product update(Long id, String name, Double price, Integer quantity, List<Long> categories) {
        List<Category> categoryList = categoryRepository.findAllById(categories);
        if(productRepository.findById(id).isPresent()){
            Product product = productRepository.findById(id).get();
            product.setName(name);
            product.setQuantity(quantity);
            product.setPrice(price);
            product.setCategories(categoryList);
            return productRepository.save(product);
        }
        throw new InvalidProductIdException();
    }

    @Override
    public Product delete(Long id) {
        Optional<Product> prod = productRepository.findById(id);
       if(prod.isPresent()) {
           productRepository.deleteById(id);
           return prod.get();
       }
       else throw new InvalidProductIdException();
    }

    @Override
    public List<Product> listProductsByNameAndCategory(String name, Long categoryId) {
        Category category = categoryId!=null? categoryRepository.findById(categoryId).get() : null;
        String nameLike = "%"+name+"%";
        if(name!=null && categoryId!=null)
            return  this.productRepository.findByNameLikeAndCategoriesContaining(nameLike,category);
        else if(name!=null)
            return this.productRepository.findByNameLike(nameLike);
        else if(categoryId!=null)
            return this.productRepository.findByCategoriesContaining(category);
        else return this.productRepository.findAll();

    }
}
