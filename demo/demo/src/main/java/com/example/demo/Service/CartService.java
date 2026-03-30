package com.example.demo.Service;

import com.example.demo.Model.CartItem;
import com.example.demo.Model.Order;
import com.example.demo.Model.OrderDetail;
import com.example.demo.Model.Product;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> items = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    public List<CartItem> getItems() {
        return items;
    }

    public void addToCart(int productId) {
        Product findProduct = productRepository.findById(productId).orElse(null);
        if (findProduct == null) {
            return;
        }

        items.stream()
                .filter(item -> item.getId() == findProduct.getId())
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + 1),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setId(productId);
                            newItem.setName(findProduct.getName());
                            newItem.setImage(findProduct.getImage());
                            newItem.setPrice(findProduct.getPrice() != null ? findProduct.getPrice().longValue() : 0L);
                            newItem.setQuantity(1);
                            items.add(newItem);
                        }
                );
    }

    public void updateQuantity(int productId, int quantity) {
        items.stream()
                .filter(item -> item.getId() == productId)
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    public void removeFromCart(int productId) {
        items.removeIf((CartItem item) -> item.getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Phần này tài liệu chưa mô tả đầy đủ code; bổ sung tối thiểu: lưu Order + OrderDetail, gán UserAccount khi đã đăng nhập DB, xóa giỏ.
     */
    @Transactional
    public void checkout() {
        if (items.isEmpty()) {
            return;
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setPaid(false);
        order.setTotalAmount(getTotal());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            userAccountRepository.findByUsername(auth.getName()).ifPresent(order::setAccount);
        }

        List<CartItem> snapshot = new ArrayList<>(items);
        for (CartItem cartItem : snapshot) {
            Product p = productRepository.findById(cartItem.getId()).orElse(null);
            if (p == null) {
                continue;
            }
            OrderDetail detail = new OrderDetail();
            detail.setProduct(p);
            detail.setPrice(cartItem.getPrice());
            detail.setQuantity(cartItem.getQuantity());
            detail.setOrder(order);
            order.getOrderDetails().add(detail);
        }

        if (order.getOrderDetails().isEmpty()) {
            return;
        }

        orderRepository.save(order);
        clear();
    }
}
