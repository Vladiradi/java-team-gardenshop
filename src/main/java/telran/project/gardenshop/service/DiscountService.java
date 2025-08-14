package telran.project.gardenshop.service;

import telran.project.gardenshop.dto.DiscountRequestDto;
import telran.project.gardenshop.dto.DiscountResponseDto;
import telran.project.gardenshop.dto.ProductOfDayDto;
import telran.project.gardenshop.entity.Discount;
import telran.project.gardenshop.enums.DiscountType;

import java.util.List;

public interface DiscountService {

    /**
     * Создает новую скидку
     */
    DiscountResponseDto createDiscount(DiscountRequestDto requestDto);

    /**
     * Получает скидку по ID
     */
    DiscountResponseDto getDiscountById(Long id);

    /**
     * Получает все активные скидки
     */
    List<DiscountResponseDto> getAllActiveDiscounts();

    /**
     * Получает скидки по типу
     */
    List<DiscountResponseDto> getDiscountsByType(DiscountType type);

    /**
     * Получает скидки для конкретного товара
     */
    List<DiscountResponseDto> getDiscountsByProductId(Long productId);

    /**
     * Обновляет скидку
     */
    DiscountResponseDto updateDiscount(Long id, DiscountRequestDto requestDto);

    /**
     * Деактивирует скидку
     */
    void deactivateDiscount(Long id);

    /**
     * Удаляет скидку
     */
    void deleteDiscount(Long id);

    /**
     * Получает товар дня
     */
    ProductOfDayDto getProductOfDay();

    /**
     * Устанавливает товар дня
     */
    ProductOfDayDto setProductOfDay(Long productId, String description);

    /**
     * Получает все товары со скидками
     */
    List<DiscountResponseDto> getProductsWithDiscounts();

    /**
     * Проверяет, есть ли активные скидки у товара
     */
    boolean hasActiveDiscounts(Long productId);
}
