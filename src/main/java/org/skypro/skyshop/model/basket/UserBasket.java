package org.skypro.skyshop.model.basket;

import java.util.Collections;
import java.util.List;

public class UserBasket {
    private final List<BasketItem> items;
    private final int total;

    public UserBasket(List<BasketItem> items) {
        this.items = Collections.unmodifiableList(items);
        this.total = calculateTotal();
    }

    private int calculateTotal() {
        return items.stream()
                .mapToInt(BasketItem::getTotalPrice)
                .sum();
    }

    public List<BasketItem> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    public int getItemsCount() {
        return items.stream()
                .mapToInt(BasketItem::getQuantity)
                .sum();
    }
}
