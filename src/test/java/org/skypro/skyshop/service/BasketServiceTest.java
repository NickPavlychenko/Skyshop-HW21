package org.skypro.skyshop.service;

import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private BasketService basketService;

    private UUID existingProductId;
    private UUID nonExistingProductId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        existingProductId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        nonExistingProductId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        testProduct = new SimpleProduct(
                existingProductId,
                "Тестовый продукт",
                10000
        );
    }

    @Test
    void addProductToBasket_WhenProductDoesNotExist_ShouldThrowException() {
        when(storageService.findProductById(nonExistingProductId))
                .thenReturn(Optional.empty());

        NoSuchProductException exception = assertThrows(
                NoSuchProductException.class,
                () -> basketService.addProductToBasket(nonExistingProductId)
        );

        assertEquals("Продукт с ID " + nonExistingProductId + " не найден",
                exception.getMessage());

        verify(storageService, times(1)).findProductById(nonExistingProductId);
        verify(productBasket, never()).addProduct(any());
    }

    @Test
    void addProductToBasket_WhenProductExists_ShouldAddToBasket() {
        when(storageService.findProductById(existingProductId))
                .thenReturn(Optional.of(testProduct));

        basketService.addProductToBasket(existingProductId);

        verify(storageService, times(1)).findProductById(existingProductId);
        verify(productBasket, times(1)).addProduct(existingProductId);
    }

    @Test
    void getUserBasket_WhenBasketIsEmpty_ShouldReturnEmptyBasket() {
        when(productBasket.getAllProducts()).thenReturn(Collections.emptyMap());

        UserBasket userBasket = basketService.getUserBasket();

        assertNotNull(userBasket);
        assertTrue(userBasket.getItems().isEmpty());
        assertEquals(0, userBasket.getTotal());
        assertEquals(0, userBasket.getItemsCount());

        verify(productBasket, times(1)).getAllProducts();
        verify(storageService, never()).getProductById(any());
    }

    @Test
    void getUserBasket_WhenBasketHasItems_ShouldReturnCorrectBasket() {
        UUID productId1 = existingProductId;
        UUID productId2 = UUID.fromString("33333333-3333-3333-3333-333333333333");

        Product product1 = testProduct;
        Product product2 = new SimpleProduct(productId2, "Другой продукт", 20000);

        Map<UUID, Integer> basketItems = new HashMap<>();
        basketItems.put(productId1, 2);
        basketItems.put(productId2, 1);

        when(productBasket.getAllProducts()).thenReturn(basketItems);
        when(storageService.getProductById(productId1)).thenReturn(product1);
        when(storageService.getProductById(productId2)).thenReturn(product2);

        UserBasket userBasket = basketService.getUserBasket();

        assertNotNull(userBasket);
        assertEquals(2, userBasket.getItems().size());

        assertEquals(40000, userBasket.getTotal());

        assertEquals(3, userBasket.getItemsCount());

        List<BasketItem> items = userBasket.getItems();

        BasketItem item1 = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId1))
                .findFirst()
                .orElse(null);

        assertNotNull(item1);
        assertEquals(2, item1.getQuantity());
        assertEquals(20000, item1.getTotalPrice()); // 10000 * 2

        BasketItem item2 = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId2))
                .findFirst()
                .orElse(null);

        assertNotNull(item2);
        assertEquals(1, item2.getQuantity());
        assertEquals(20000, item2.getTotalPrice()); // 20000 * 1

        verify(productBasket, times(1)).getAllProducts();
        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }

    @Test
    void clearBasket_ShouldCallProductBasketClear() {
        basketService.clearBasket();

        verify(productBasket, times(1)).clearBasket();
    }

    @Test
    void getTotalItemsCount_ShouldReturnCorrectCount() {
        when(productBasket.getTotalItemCount()).thenReturn(5);

        int count = basketService.getTotalItemsCount();

        assertEquals(5, count);
        verify(productBasket, times(1)).getTotalItemCount();
    }

    @Test
    void getUserBasket_ShouldCallGetProductByIdForEachItem() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        Map<UUID, Integer> basketItems = new HashMap<>();
        basketItems.put(productId1, 1);
        basketItems.put(productId2, 3);

        when(productBasket.getAllProducts()).thenReturn(basketItems);
        when(storageService.getProductById(productId1))
                .thenReturn(new SimpleProduct(productId1, "Продукт 1", 1000));
        when(storageService.getProductById(productId2))
                .thenReturn(new SimpleProduct(productId2, "Продукт 2", 2000));

        basketService.getUserBasket();

        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }
}
