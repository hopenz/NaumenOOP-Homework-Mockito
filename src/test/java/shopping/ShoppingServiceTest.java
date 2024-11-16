package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;

/**
 * Тестирование класса {@link ShoppingService}
 */
public class ShoppingServiceTest {

    /**
     * Mock-объекты класса {@link ProductDao}
     */
    private final ProductDao productDaoMock = Mockito.mock(ProductDao.class);

    /**
     * Экземпляр класса {@link ShoppingService}
     */
    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDaoMock);

    /**
     * Тестирование получения корзины пользователя
     * Тест валится, так как в {@link ShoppingServiceImpl} в методе {@link ShoppingServiceImpl#getCart(Customer)}
     * возвращается новая корзина
     */
    @Test
    public void getCartTest() {
        Customer customer = new Customer(1L, "112233");
        Cart cart = new Cart(customer);
        Assertions.assertEquals(cart, shoppingService.getCart(customer));
    }

    /**
     * Не вижу смысла это тестировать тут, так как в этом методе вызывается другой метод другого класса
     */
    @Test
    public void getAllProductsTest() {
    }

    /**
     * Не вижу смысла это тестировать тут, так как в этом методе вызывается другой метод другого класса
     */
    @Test
    public void getProductByNameTest() {
    }

    /**
     * Тестирование, что после покупки, количество продуктов уменьшается
     * и что после покупки корзина очищается
     * Но тест валится, так как корзина не очищается, но по логике должна (как вб, озон)
     *
     * @throws BuyException ошибка покупки
     */
    @Test
    public void buyWithReducingProductsAndClearingCartTest() throws BuyException {
        Product firstProduct = new Product("Энергетик", 5);
        Product secondProduct = new Product("Булочка", 3);

        Cart cart = new Cart(new Customer(1L, "112233"));
        cart.add(firstProduct, 2);
        cart.add(secondProduct, 1);

        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(3, firstProduct.getCount());
        Assertions.assertEquals(2, secondProduct.getCount());

        Mockito.verify(productDaoMock).save(firstProduct);
        Mockito.verify(productDaoMock).save(secondProduct);

        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Тестирование, что покупка невозможна, если корзина пуста
     *
     * @throws BuyException ошибка покупки
     */
    @Test
    public void buyWithEmptyCartTest() throws BuyException {
        Cart cart = new Cart(new Customer(1L, "112233"));
        Assertions.assertEquals(0, cart.getProducts().size());
        Assertions.assertFalse(shoppingService.buy(cart));
    }

    /**
     * Тестирование, что при покупке товара, которого нет в наличии, возникает ошибка
     *
     * @throws BuyException ошибка покупки
     */
    @Test
    public void buyWithMissingProductTest() throws BuyException {
        Product firstProduct = new Product("Энергетик", 3);

        Cart firstCart = new Cart(new Customer(1L, "112233"));
        Cart secondCart = new Cart(new Customer(2L, "445566"));

        firstCart.add(firstProduct, 2);
        secondCart.add(firstProduct, 2);

        Assertions.assertTrue(shoppingService.buy(firstCart));

        BuyException buyException = Assertions.assertThrows(BuyException.class,
                () -> shoppingService.buy(secondCart));

        Assertions.assertEquals("В наличии нет необходимого количества товара 'Энергетик'",
                buyException.getMessage());
    }

    /**
     * Тестирование случая, когда в магазине 5 энергетиков, хочу купить 5 -> энергетиков становится 0
     * Тест валится, так как в {@link ShoppingServiceImpl} в методе {@link ShoppingServiceImpl#validateCanBuy(Cart)}
     * идет строгая проверка, то есть количество товара на складе > количества товара в корзине
     *
     * @throws BuyException ошибка покупки
     */
    @Test
    public void buyAllProductsTest() throws BuyException {
        Product firstProduct = new Product("Энергетик", 5);

        Cart cart = new Cart(new Customer(1L, "112233"));
        cart.add(firstProduct, 5);

        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(0, firstProduct.getCount());
    }
}
