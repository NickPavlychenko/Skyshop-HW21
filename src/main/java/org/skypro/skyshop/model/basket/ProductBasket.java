package org.skypro.skyshop.model.basket;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProductBasket {
    private final Map<UUID, Integer> basketItems;

    public ProductBasket() {
        this.basketItems = new HashMap<>();
    }

    public void addProduct(UUID productId) {
        basketItems.merge(productId, 1, Integer::sum);
    }

    public Map<UUID, Integer> getAllProducts() {
        return Collections.unmodifiableMap(basketItems);
    }

    public void clearBasket() {
        basketItems.clear();
    }

    public int getTotalItemCount() {
        return basketItems.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getProductQuantity(UUID productId) {
        return basketItems.getOrDefault(productId, 0);
    }
}
