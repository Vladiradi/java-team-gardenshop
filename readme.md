
# 🌿 Java Team Project – GardenShop
### Telran School BE 2025
 Командный backend-проект онлайн-магазина товаров для дома и сада, реализованный на Java Spring Boot.

 




## 📌 Описание проекта

Garden Shop — это backend-приложение для интернет-магазина, позволяющее клиентам просматривать каталог товаров,
добавлять их в корзину, оформлять заказы и отслеживать их статус.  
Администраторы могут управлять товарами, категориями, скидками и получать отчёты по продажам.



## 📌 Клонировать репозиторий

```
git clone https://github.com/Vladiradi/java-team-gardenshop.git
```




## 📌 Полезные ссылки

- 📘 [Макет](https://www.figma.com/design/SDNWLzCWkh9ZXdCpWEaByv/project-frontend?node-id=0-1&p=f) 
 

- 📄 [Техническое задание](https://docs.google.com/document/d/1Xn41eFhdYAJVYzRucsNwpbLJ5lNxdvpfx__SZf5DwXA/edit?tab=t.0)


-  🚀 [Демо-версия](https://) *( appear later)*


## ⚙️ Стек технологий

| Технология      | Назначение                      |
|-----------------|---------------------------------|
| Java 21         | Язык программирования           |
| Spring Boot     | Backend фреймворк               |
| Spring Web      | Обработка HTTP-запросов         |
| Spring Data JPA | Работа с базой данных через ORM |
| Hibernate       | Провайдер JPA                   |
| PostgreSQL / H2 | Реляционная БД (prod/test)      |
| Lombok          | Генерация шаблонного кода       |
| MapStruct       | Маппинг DTO ↔ Entity            |
| Liquibase       | Миграции базы данных            |
| Maven           | Система сборки                  |
| JUnit / Mockito | Тестирование                    |


## 📌 Авторы проекта

| Участник            | Контакты                           | Вклад в проект |
|---------------------|------------------------------------|----------------|
| Vladimir Ryzhov     | [LinkedIn](https://linkedin.com/in/) | Team Lead      |
| Arkady Zon          | [LinkedIn](https://linkedin.com/in/) | Exception      |
| Anyuta Boldt        | [LinkedIn](https://linkedin.com/in/) | Testing        |
| Liudmyla Iermolenko | [LinkedIn](https://linkedin.com/in/) | Favorites      |


# Стандарты использования @Transactional

## Когда использовать @Transactional
- Используйте @Transactional только для методов, где требуется атомарность нескольких операций с БД (например, несколько save/delete, сложная бизнес-логика, массовые изменения).
- Пример:
```java
@Transactional
public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
    Account from = accountRepository.findById(fromId).get();
    Account to = accountRepository.findById(toId).get();
    from.debit(amount);
    to.credit(amount);
    accountRepository.save(from);
    accountRepository.save(to);
}
```

## Когда НЕ использовать @Transactional
- Не используйте @Transactional для методов, которые делают только один вызов save или delete — Spring Data JPA сам откроет транзакцию на уровне репозитория.
- Пример:
```java
public void deleteUser(Long id) {
    userRepository.deleteById(id); // транзакция будет создана автоматически
}
```

## Общие рекомендации
- Не ставьте @Transactional на класс, если не все методы требуют транзакции.
- Для сервисов с несколькими изменяющими операциями используйте @Transactional только на нужных методах.
- Для методов только чтения транзакция не требуется.
- Для массовых обновлений (batch) используйте @Transactional для атомарности.

## Примеры из проекта
- Оставляйте @Transactional только для методов:
  - CartItemServiceImpl: addItemToCart, updateItemQuantity, removeItemFromCart, clearCart
  - OrderServiceImpl: createOrder, cancelOrder
  - SchedulerService: processOrders

---

Следуйте этим правилам для минимизации нагрузки на БД и предотвращения блокировок.


