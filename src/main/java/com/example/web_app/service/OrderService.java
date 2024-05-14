package com.example.web_app.service;

import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.Review;
import com.example.web_app.domain.User;
import com.example.web_app.model.OrderDTO;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.ReviewRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.util.NotFoundException;
import com.example.web_app.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public OrderService(final OrderRepository orderRepository, final UserRepository userRepository,
            final ProductRepository productRepository, final ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<OrderDTO> findAll() {
        final List<Order> orders = orderRepository.findAll(Sort.by("orderId"));
        return orders.stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .toList();
    }

    public OrderDTO get(final Integer orderId) {
        return orderRepository.findById(orderId)
                .map(order -> mapToDTO(order, new OrderDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final OrderDTO orderDTO) {
        final Order order = new Order();
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getOrderId();
    }

    public void update(final Integer orderId, final OrderDTO orderDTO) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(orderDTO, order);
        orderRepository.save(order);
    }

    public void delete(final Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setQuantity(order.getQuantity());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setBuyer(order.getBuyer() == null ? null : order.getBuyer().getUserId());
        orderDTO.setProduct(order.getProduct() == null ? null : order.getProduct().getProductId());
        return orderDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        order.setQuantity(orderDTO.getQuantity());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setOrderDate(orderDTO.getOrderDate());
        final User buyer = orderDTO.getBuyer() == null ? null : userRepository.findById(orderDTO.getBuyer())
                .orElseThrow(() -> new NotFoundException("buyer not found"));
        order.setBuyer(buyer);
        final Product product = orderDTO.getProduct() == null ? null : productRepository.findById(orderDTO.getProduct())
                .orElseThrow(() -> new NotFoundException("product not found"));
        order.setProduct(product);
        return order;
    }

    public ReferencedWarning getReferencedWarning(final Integer orderId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        final Review orderReview = reviewRepository.findFirstByOrder(order);
        if (orderReview != null) {
            referencedWarning.setKey("order.review.order.referenced");
            referencedWarning.addParam(orderReview.getReviewId());
            return referencedWarning;
        }
        return null;
    }

}
