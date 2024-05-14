package com.example.web_app.service;

import com.example.web_app.domain.Category;
import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import com.example.web_app.model.ProductDTO;
import com.example.web_app.repos.CategoryRepository;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.util.NotFoundException;
import com.example.web_app.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public ProductService(final ProductRepository productRepository,
            final UserRepository userRepository, final CategoryRepository categoryRepository,
            final OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    public List<ProductDTO> findAll() {
        final List<Product> products = productRepository.findAll(Sort.by("productId"));
        return products.stream()
                .map(product -> mapToDTO(product, new ProductDTO()))
                .toList();
    }

    public ProductDTO get(final Integer productId) {
        return productRepository.findById(productId)
                .map(product -> mapToDTO(product, new ProductDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ProductDTO productDTO) {
        final Product product = new Product();
        mapToEntity(productDTO, product);
        return productRepository.save(product).getProductId();
    }

    public void update(final Integer productId, final ProductDTO productDTO) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(productDTO, product);
        productRepository.save(product);
    }

    public void delete(final Integer productId) {
        productRepository.deleteById(productId);
    }

    public ProductDTO mapToDTO(final Product product, final ProductDTO productDTO) {
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setUser(product.getUser() == null ? null : product.getUser().getUserId());
        productDTO.setCategory(product.getCategory() == null ? null : product.getCategory().getId());
        return productDTO;
    }

    public Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        final User user = productDTO.getUser() == null ? null
                : userRepository.findById(productDTO.getUser())
                        .orElseThrow(() -> new NotFoundException("user not found"));
        product.setUser(user);
        final Category category = productDTO.getCategory() == null ? null
                : categoryRepository.findById(productDTO.getCategory())
                        .orElseThrow(() -> new NotFoundException("category not found"));
        product.setCategory(category);
        return product;
    }

    public ReferencedWarning getReferencedWarning(final Integer productId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Product product = productRepository.findById(productId)
                .orElseThrow(NotFoundException::new);
        final Order productOrder = orderRepository.findFirstByProduct(product);
        if (productOrder != null) {
            referencedWarning.setKey("product.order.product.referenced");
            referencedWarning.addParam(productOrder.getOrderId());
            return referencedWarning;
        }
        return null;
    }

}
