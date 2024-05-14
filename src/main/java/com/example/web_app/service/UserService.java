package com.example.web_app.service;

import com.example.web_app.domain.Message;
import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.Role;
import com.example.web_app.domain.User;
import com.example.web_app.model.UserDTO;
import com.example.web_app.repos.MessageRepository;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.repos.ProductRepository;
import com.example.web_app.repos.RoleRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.util.NotFoundException;
import com.example.web_app.util.ReferencedWarning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final MessageRepository messageRepository;

    public UserService(final UserRepository userRepository, final RoleRepository roleRepository,
            final ProductRepository productRepository, final OrderRepository orderRepository,
            final MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.messageRepository = messageRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("userId"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Integer userId) {
        return userRepository.findById(userId)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getUserId();
    }

    public void update(final Integer userId, final UserDTO userDTO) {
        final User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Integer userId) {
        userRepository.deleteById(userId);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setRoleId(user.getRoleId().stream()
                .map(role -> role.getId())
                .toList());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCreatedAt(userDTO.getCreatedAt());
        final List<Role> roleId = iterableToList(roleRepository.findAllById(
                userDTO.getRoleId() == null ? Collections.emptyList() : userDTO.getRoleId()));
        if (roleId.size() != (userDTO.getRoleId() == null ? 0 : userDTO.getRoleId().size())) {
            throw new NotFoundException("one of roleId not found");
        }
        user.setRoleId(new HashSet<>(roleId));
        return user;
    }

    private <T> List<T> iterableToList(final Iterable<T> iterable) {
        final List<T> list = new ArrayList<T>();
        iterable.forEach(item -> list.add(item));
        return list;
    }

    public ReferencedWarning getReferencedWarning(final Integer userId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        final Product userProduct = productRepository.findFirstByUser(user);
        if (userProduct != null) {
            referencedWarning.setKey("user.product.user.referenced");
            referencedWarning.addParam(userProduct.getProductId());
            return referencedWarning;
        }
        final Order buyerOrder = orderRepository.findFirstByBuyer(user);
        if (buyerOrder != null) {
            referencedWarning.setKey("user.order.buyer.referenced");
            referencedWarning.addParam(buyerOrder.getOrderId());
            return referencedWarning;
        }
        final Message senderMessage = messageRepository.findFirstBySender(user);
        if (senderMessage != null) {
            referencedWarning.setKey("user.message.sender.referenced");
            referencedWarning.addParam(senderMessage.getMessageId());
            return referencedWarning;
        }
        final Message receiverMessage = messageRepository.findFirstByReceiver(user);
        if (receiverMessage != null) {
            referencedWarning.setKey("user.message.receiver.referenced");
            referencedWarning.addParam(receiverMessage.getMessageId());
            return referencedWarning;
        }
        return null;
    }

}
