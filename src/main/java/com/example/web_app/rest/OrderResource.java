package com.example.web_app.rest;

import com.example.web_app.model.OrderDTO;
import com.example.web_app.service.OrderService;
import com.example.web_app.util.ReferencedException;
import com.example.web_app.util.ReferencedWarning;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderResource {

    private final OrderService orderService;

    public OrderResource(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable(name = "orderId") final Integer orderId) {
        return ResponseEntity.ok(orderService.get(orderId));
    }

    @PostMapping
    public ResponseEntity<Integer> createOrder(@RequestBody @Valid final OrderDTO orderDTO) {
        final Integer createdOrderId = orderService.create(orderDTO);
        return new ResponseEntity<>(createdOrderId, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Integer> updateOrder(
            @PathVariable(name = "orderId") final Integer orderId,
            @RequestBody @Valid final OrderDTO orderDTO) {
        orderService.update(orderId, orderDTO);
        return ResponseEntity.ok(orderId);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable(name = "orderId") final Integer orderId) {
        final ReferencedWarning referencedWarning = orderService.getReferencedWarning(orderId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        orderService.delete(orderId);
        return ResponseEntity.noContent().build();
    }

}
