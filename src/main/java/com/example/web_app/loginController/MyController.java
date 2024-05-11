package com.example.web_app.loginController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web_app.domain.Grain;
import com.example.web_app.domain.User;
import com.example.web_app.model.GrainDTO;
import com.example.web_app.repos.GrainRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.service.GrainService;
import com.example.web_app.service.OrderService;
import com.example.web_app.util.WebUtils;
import java.util.List;

@Controller
public class MyController {

    @Autowired
    GrainRepository grainRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    GrainService grainService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/farmer")
    public String getFarmer() {
        return "Pages/farmer";
    }

    @GetMapping("/farmer/orders")
    public String getFarmers(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "Pages/farmerOrders";
    }

    @GetMapping("/farmer/crops")
    public String getFarmers1(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).get();

        List<GrainDTO> grains = grainRepository.findBySeller(user).stream()
                .map(x -> grainService.mapToDTO(x, new GrainDTO())).collect(Collectors.toList());
        model.addAttribute("obj", new GrainDTO());
        model.addAttribute("grains", grains);
        return "Pages/farmerCrops";
    }

    @PostMapping("/farmer/crops/submit")
    public String submitForm(@ModelAttribute("obj") GrainDTO grainDTO,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("fieldImages") MultipartFile[] fieldImages, final RedirectAttributes redirectAttributes)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).get();

        Grain grain = new Grain();
        grain.setDescription(grainDTO.getDescription());
        grain.setGrainName(grainDTO.getGrainName());
        grain.setPrice(grainDTO.getPrice());
        grain.setQuantity(grainDTO.getQuantity());
        grain.setSeller(user);

        try {

            if (!thumbnail.isEmpty()) {
                grain.setThumbnail(thumbnail.getBytes());
            }
            if (fieldImages != null) {
                Set<byte[]> x = new HashSet<byte[]>();
                for (MultipartFile a : fieldImages) {
                    x.add(a.getBytes());
                }
                grain.setFieldIamgs(x);
            }
            grainRepository.save(grain);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(e.getMessage()));
        }
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("Grain added successfully"));
        return "redirect:/farmer/crops";
    }

    @GetMapping("/buyer")
    public String getBuyer() {
        return "Pages/buyer";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Pages/admin";
    }

}
