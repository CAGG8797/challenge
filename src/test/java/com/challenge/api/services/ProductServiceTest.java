package com.challenge.api.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.Product;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.impl.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final String PRODUCT_ID = "id_1";
    private static final String PRODUCT_NAME = "product_1";
    private static final String PRODUCT_DESCRIPTION = "product_description";
    private static final BigInteger PRODUCT_ON_HAND = new BigInteger("20");
    private static final BigDecimal PRODUCT_UNIT_PRICE = new BigDecimal("50.5");
    private static final Product PRODUCT_DTO = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND.intValue(), PRODUCT_UNIT_PRICE);
    private static final ProductDAO SINGLE_PRODUCT_REPOSITORY_RESPONSE = new ProductDAO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND, PRODUCT_UNIT_PRICE, true);

    @Mock
    private ExtendedCrudRepository<ProductDAO, String> productRepository;

    @InjectMocks
    private ProductService productService;

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100})
    public void findAllProductsSuccessfully(int pageSize) {
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(databaseProducts(pageRequest));
        Page<Product> result = productService.getAll(pageRequest);
        assertNotNull(result);
        assertEquals(pageSize, result.getTotalElements());
    }

    @ParameterizedTest
    @NullSource
    public void tryToFindAllProductsWithNullParam(PageRequest pageRequest) {
        String expectedMessage = "PageRequest cannot be null";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.getAll(pageRequest));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void findProductByIdSuccessfully() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        Product result = productService.getById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.id());
        assertEquals(PRODUCT_NAME, result.name());
        assertEquals(PRODUCT_DESCRIPTION, result.description());
        assertEquals(PRODUCT_ON_HAND.intValue(), result.onHand());
        assertEquals(PRODUCT_UNIT_PRICE, result.unitPrice());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void tryToFindProductByIdWithNullOrEmptyParams(String id) {
        String expectedMessage = "Product id cannot be null or empty";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.getById(id));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"id_1", "id_2", "id_3"})
    public void tryToFindProductByIdWithNonExistentId(String id) {
        String expectedMessage = "Product with id " + id + " not found";
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> productService.getById(id));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void createOneProductSuccessfully() throws Exception {
        ProductDAO productDAO = new ProductDAO(UUID.randomUUID().toString(), PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND, PRODUCT_UNIT_PRICE, true);
        when(productRepository.saveAndFlush(any(ProductDAO.class))).thenReturn(productDAO);
        Product result = productService.create(PRODUCT_DTO);

        assertNotNull(result);
        assertNotEquals(PRODUCT_DTO, result);
        assertNotNull(result.id());
        // The id is generated by the database, so we cannot compare it with the DTO
        assertNotEquals(PRODUCT_DTO.id(), result.id());
        assertEquals(PRODUCT_DTO.name(), result.name());
        assertEquals(PRODUCT_DTO.description(), result.description());
        assertEquals(PRODUCT_DTO.onHand(), result.onHand());
        assertEquals(PRODUCT_DTO.unitPrice(), result.unitPrice());
    }

    @Test
    public void tryToCreateProductWithNullParam() {
        String expectedMessage = "Product cannot be null";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.create(null));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    public void tryToCreateProductWithInvalidName(String expectedMessage, String invalidName) {
        Product product = new Product(null, invalidName, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.create(product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptions")
    public void tryToCreateProductWithNullOrEmptyDescription(String expectedMessage, String invalidDescription) {
        Product product = new Product(null, PRODUCT_NAME, invalidDescription, PRODUCT_ON_HAND.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.create(product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidOnHand")
    public void tryToCreateProductWithInvalidOnHand(String expectedMessage, BigInteger invalidOnHand) {
        Product product = new Product(null, PRODUCT_NAME, PRODUCT_DESCRIPTION, invalidOnHand == null ? null : invalidOnHand.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.create(product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidUnitPrice")
    public void tryToCreateProductWithInvalidUnitPrice(String expectedMessage, BigDecimal invalidUnitPrice) {
        Product product = new Product(null, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND.intValue(), invalidUnitPrice);

        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.create(product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void updateOneProductSuccessfully() throws Exception {
        Product updatedProduct = new Product("new Id", "new Name", "new Description", 100, new BigDecimal("100"));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));

        when(productRepository.saveAndFlush(any(ProductDAO.class))).thenReturn(new ProductDAO(PRODUCT_ID, updatedProduct.name(),
                updatedProduct.description(), new BigInteger(String.valueOf(updatedProduct.onHand())), updatedProduct.unitPrice(), true));

        Product result = productService.update(PRODUCT_ID, updatedProduct);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.id());
        assertEquals(updatedProduct.name(), result.name());
        assertEquals(updatedProduct.description(), result.description());
        assertEquals(updatedProduct.onHand(), result.onHand());
        assertEquals(updatedProduct.unitPrice(), result.unitPrice());
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateParams")
    public void tryToUpdateProductWithNullAndEmptyParams(Product product, String id, String expectedMessage) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.update(id, product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void tryToUpdateNonExistentProduct() {
        String expectedMessage = "Product with id " + PRODUCT_ID + " not found";

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> productService.update(PRODUCT_ID, PRODUCT_DTO));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    public void tryToUpdateProductWithInvalidName(String expectedMessage, String invalidName) {
        Product product = new Product(null, invalidName, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.update(PRODUCT_ID, product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptions")
    public void tryToUpdateProductWithNullOrEmptyDescription(String expectedMessage, String invalidDescription) {
        Product product = new Product(null, PRODUCT_NAME, invalidDescription, PRODUCT_ON_HAND.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.update(PRODUCT_ID, product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidOnHand")
    public void tryToUpdateProductWithInvalidOnHand(String expectedMessage, BigInteger invalidOnHand) {
        Product product = new Product(null, PRODUCT_NAME, PRODUCT_DESCRIPTION, invalidOnHand == null ? null : invalidOnHand.intValue(), PRODUCT_UNIT_PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.update(PRODUCT_ID, product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidUnitPrice")
    public void tryToUpdateProductWithInvalidUnitPrice(String expectedMessage, BigDecimal invalidUnitPrice) {
        Product product = new Product(null, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_ON_HAND.intValue(), invalidUnitPrice);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        when(productRepository.saveAndFlush(any(ProductDAO.class)))
                .thenThrow(new ConstraintViolationException(expectedMessage, null, null));

        Exception exception = assertThrows(Exception.class, () -> productService.update(PRODUCT_ID, product));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void deleteOneProductSuccessfully() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(SINGLE_PRODUCT_REPOSITORY_RESPONSE));
        productService.delete(PRODUCT_ID);
        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void tryToDeleteProductWithNullOrEmptyParams(String id) {
        String expectedMessage = "Product id cannot be null or empty";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.delete(id));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void tryToDeleteNonExistentProduct() {
        String expectedMessage = "Product with id " + PRODUCT_ID + " not found";
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> productService.delete(PRODUCT_ID));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidNames() {
        return Stream.of(
                Arguments.of("Product name is required", null),
                Arguments.of("Product name is required", ""),
                Arguments.of("Product name is required", "   "),
                Arguments.of("Product name must be between 3 and 50 characters", "a"),
                Arguments.of("Product name must be between 3 and 50 characters", "this is a very long name that exceeds the maximum length allowed")
        );
    }

    private static Stream<Arguments> invalidDescriptions() {
        return Stream.of(
                Arguments.of("Product description is required", null),
                Arguments.of("Product description is required", ""),
                Arguments.of("Product description is required", "   "),
                Arguments.of("Product description must be between 3 and 200 characters", "a"),
                Arguments.of("Product description must be between 3 and 200 characters",
                        "This is a dummy message to test a string that exceeds the maximum length of 200 characters. " +
                                "It is important to ensure that this string is sufficiently long to trigger any validation logic that" +
                                " checks for the maximum allowed length of a product description in the application.")
        );
    }

    private static Stream<Arguments> invalidOnHand() {
        return Stream.of(
                Arguments.of("Product on hand is required", null),
                Arguments.of("Product on hand must be greater than or equal to 0", new BigInteger("-1")),
                Arguments.of("Product on hand must be greater than or equal to 0", new BigInteger("-100"))
        );
    }

    private static Stream<Arguments> invalidUnitPrice() {
        return Stream.of(
                Arguments.of("Product unit price is required", null),
                Arguments.of("Product unit price must be greater than or equal to 0", new BigDecimal("-1")),
                Arguments.of("Product unit price must be greater than or equal to 0", new BigDecimal("-100"))
        );
    }

    private static Stream<Arguments> invalidUpdateParams() {
        return Stream.of(
                Arguments.of(null, PRODUCT_ID, "Product to update cannot be null"),
                Arguments.of(PRODUCT_DTO, null, "Product id cannot be null or empty"),
                Arguments.of(PRODUCT_DTO, "", "Product id cannot be null or empty"),
                Arguments.of(PRODUCT_DTO, "   ", "Product id cannot be null or empty")
        );
    }

    private static Page<ProductDAO> databaseProducts(PageRequest pageRequest) {
        List<ProductDAO> products = new ArrayList<>();

        for (int i = 0; i < pageRequest.getPageSize(); i++) {
            products.add(new ProductDAO("id_" + i, "product_" + i,
                    "product_description_" + i, new BigInteger("20"), new BigDecimal("50.5"), true));
        }

        return new PageImpl<>(products, pageRequest, products.size());
    }
}
