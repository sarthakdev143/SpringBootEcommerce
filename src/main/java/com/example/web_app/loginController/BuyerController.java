package com.example.web_app.loginController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import com.example.web_app.model.ProductDTO;
import com.example.web_app.repos.CategoryRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.RoleRepository;
import com.example.web_app.service.CategoryService;
import com.example.web_app.service.ProductService;

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

import java.util.List;

@Controller
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
    UserRepository userRepository;

    @GetMapping("/buyer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BUYER')")
    public String getBuyer(Model m) {
        m.addAttribute("products", productService.findAll());
        List<UserDto2> sellers = roleRepository.findById(Constant.ROLE_SELLER).get().getUserId().stream()
                .map(x -> new UserDto2(x.getUserId(), x.getUserName())).collect(Collectors.toList());
        m.addAttribute("sellers", sellers);
        m.addAttribute("categories", categoryService.findAll());
        return "Pages/buyer";
    }

    @GetMapping("/buyer/filter/category/{id}")
    public String getMethodName(@PathVariable("id") int id, Model m) {

        m.addAttribute("products", categoryRepository.findById(id).get().getProducts().stream()
                .map(x -> productService.mapToDTO(x, new ProductDTO())).collect(Collectors.toList()));

        List<UserDto2> sellers = roleRepository.findById(Constant.ROLE_SELLER).get().getUserId().stream()
                .map(x -> new UserDto2(x.getUserId(), x.getUserName())).collect(Collectors.toList());

        m.addAttribute("sellers", sellers);

        m.addAttribute("categories", categoryService.findAll());

        return "Pages/buyer";
    }

    @GetMapping("/buyer/filter/seller/{id}")
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

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).get();
        Product product = productRepository.findById(id).get();
        Order order = new Order();

        // changes
        return "pages/Cart";
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