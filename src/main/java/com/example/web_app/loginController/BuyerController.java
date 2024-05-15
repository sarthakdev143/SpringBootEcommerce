package com.example.web_app.loginController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import com.example.web_app.model.ProductDTO;
import com.example.web_app.repos.CategoryRepository;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.RoleRepository;
import com.example.web_app.service.CategoryService;
import com.example.web_app.service.OrderService;
import com.example.web_app.service.ProductService;
import com.example.web_app.util.ReferencedWarning;
import com.example.web_app.util.WebUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String getBuyer(Model m) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        User user = userRepository.findByEmail(username).get();

        m.addAttribute("products", productService.findAll());
        List<UserDto2> sellers = roleRepository.findById(Constant.ROLE_SELLER).get().getUserId().stream()
                .map(x -> new UserDto2(x.getUserId(), x.getUserName())).collect(Collectors.toList());
        m.addAttribute("sellers", sellers);
        m.addAttribute("categories", categoryService.findAll());
        m.addAttribute("cartCount", user.getBuyerOrders().size());
        return "Pages/buyer";
    }

    @GetMapping("/filter/category/{id}")
    public String getMethodName(@PathVariable("id") int id, Model m) {

        m.addAttribute("products", categoryRepository.findById(id).get().getProducts().stream()
                .map(x -> productService.mapToDTO(x, new ProductDTO())).collect(Collectors.toList()));

        List<UserDto2> sellers = roleRepository.findById(Constant.ROLE_SELLER).get().getUserId().stream()
                .map(x -> new UserDto2(x.getUserId(), x.getUserName())).collect(Collectors.toList());

        m.addAttribute("sellers", sellers);

        m.addAttribute("categories", categoryService.findAll());

        return "Pages/buyer";
    }

    @GetMapping("/filter/seller/{id}")
    public String getMethodName2(@PathVariable("id") int id, Model m) {

        m.addAttribute("products", userRepository.findById(id).get().getProducts().stream()
                .map(x -> productService.mapToDTO(x, new ProductDTO())).collect(Collectors.toList()));

        List<UserDto2> sellers = roleRepository.findById(Constant.ROLE_SELLER).get().getUserId().stream()
                .map(x -> new UserDto2(x.getUserId(), x.getUserName())).collect(Collectors.toList());

        m.addAttribute("sellers", sellers);

        m.addAttribute("categories", categoryService.findAll());

        return "Pages/buyer";
    }

    @GetMapping("/product/thumbnail/{id}")
    public ResponseEntity<byte[]> getPost(@PathVariable("id") int id, Model model) {
        Product post = productRepository.findById(id).get();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(post.getThumbnail());
    }

    @GetMapping("/product/details/{id}")
    public String getOrder(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.get(id));
        System.out.println(id);
        return "Pages/productDetails";
    }

    @GetMapping("/user/cart")
    public String getMetwhodName(Model m) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).get();
        m.addAttribute("orders", user.getBuyerOrders());
        return "Pages/cart";
    }

    @PostMapping("/user/order/{id}")
    public String getMetwhodNam2e(Model m, @PathVariable("id") Integer id,
            final RedirectAttributes redirectAttributes) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).get();
            Product product = productRepository.findById(id).get();
            Order order = new Order();

            order.setProduct(product);
            order.setBuyer(user);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalPrice(null);
            order.setQuantity(null);
            order.setOrderReviews(null);
            orderRepository.save(order);

            Set<Order> userOrders = user.getBuyerOrders();
            userOrders.add(order);
            user.setBuyerOrders(userOrders);
            userRepository.save(user);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage("Error : ", e.getMessage()));
        }
        return "redirect:/buyer/user/cart";
    }

    @Autowired
    OrderService orderService;

    @PostMapping("/user/orders/delete/{orderId}")
    public String delete(@PathVariable(name = "orderId") final Integer orderId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = orderService.getReferencedWarning(orderId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            orderService.delete(orderId);  
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("order.delete.success"));
        }
        return "redirect:/buyer/user/cart";
    }

}

@Component
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class UserDto2 {
    private Integer userId;
    private String userName;
}