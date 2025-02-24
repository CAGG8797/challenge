package com.challenge.api.services;

import java.math.BigDecimal;
import java.util.concurrent.Future;

public interface OrderTotalService {
    Future<BigDecimal> calculateTotalAsync(String orderId);
}
