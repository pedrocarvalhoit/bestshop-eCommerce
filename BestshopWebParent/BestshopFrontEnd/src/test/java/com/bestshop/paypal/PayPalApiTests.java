package com.bestshop.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AdVGSzAmIFd_Q092mmTCx3SlIi3nuSZUKlAgWl_aNrm9e77TzCZWxw0RmfUvsZ4mlCHvEXw6n7NEwv7N";
    private static final String CLIENT_SECRET = "EO1Ze3ZhBt1lnRKBsd3Re4VDrzYLnzdq_ioo_3_6wZRVwrA_yR-O_qFD5Zr8JgiMURh_B1ptdxCnzJ5o";

    @Test
    public void testGetOrderDetails() {
        String orderId = "createAnOrder";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
                requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));

    }
}