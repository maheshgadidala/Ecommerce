package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.payload.PaymentDto;
import com.JavaEcommerce.Ecommerce.request.ProcessPaymentRequest;
import com.JavaEcommerce.Ecommerce.response.PaymentResponse;
import com.JavaEcommerce.Ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long paymentId) {
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getUserPayments() {
        List<PaymentDto> payments = paymentService.getUserPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentDto> payments = paymentService.getPaymentsByStatus(status);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDto> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentDto payment = paymentService.getPaymentByTransactionId(transactionId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentDto>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<PaymentDto> payments = paymentService.getPaymentHistory(pageNumber, pageSize);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/receipt/{orderId}")
    public ResponseEntity<PaymentDto> getPaymentReceipt(@PathVariable Long orderId) {
        PaymentDto payment = paymentService.getPaymentReceipt(orderId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable Long paymentId) {
        String status = paymentService.checkPaymentStatus(paymentId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}

