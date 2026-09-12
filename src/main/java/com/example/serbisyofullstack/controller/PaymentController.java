package com.example.serbisyofullstack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.RateLimiter;
import com.example.serbisyofullstack.dto.nested.PaymentDto;
import com.example.serbisyofullstack.dto.nested.RefundDto;
import com.example.serbisyofullstack.dto.request.payment.CreatePaymentRequest;
import com.example.serbisyofullstack.dto.request.payment.CreateRefundRequest;
import com.example.serbisyofullstack.dto.request.payment.PaymentWebhookRequest;
import com.example.serbisyofullstack.dto.response.payment.CreatePaymentResponse;
import com.example.serbisyofullstack.dto.response.payment.CreateRefundResponse;
import com.example.serbisyofullstack.dto.response.payment.PaymentWebhookResponse;
import com.example.serbisyofullstack.exception.TooManyRequestsException;
import com.example.serbisyofullstack.payment.PaymentWebhookProcessor;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Payment endpoints for the authenticated customer plus the gateway webhook.
 * The webhook endpoint is unauthenticated (gateway-to-gateway) and therefore
 * rate limited per IP; customer endpoints are rate limited per user.
 */
@Tag(name = "Payments", description = "Payments, refunds and the gateway webhook")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentWebhookProcessor webhookProcessor;
    private final CurrentUserService currentUserService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Create a payment for a booking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Booking does not belong to the caller"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest) {
        Long userId = currentUserService.getCurrentUserId();
        if (!rateLimiter.tryAcquire("payments:create:" + userId, 20, 60_000)) {
            throw new TooManyRequestsException("Too many payment attempts, please try again later");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(userId, request));
    }

    @Operation(summary = "Get a payment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Payment is not linked to the caller"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentForUser(
                currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Request a refund for a payment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Refund requested"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Payment is not linked to the caller"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "409", description = "Payment already fully refunded")
    })
    @PostMapping("/{id}/refunds")
    public ResponseEntity<CreateRefundResponse> requestRefund(
            @PathVariable Long id,
            @Valid @RequestBody CreateRefundRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.requestRefund(currentUserService.getCurrentUserId(), id, request));
    }

    @Operation(summary = "Get a refund")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Refund retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Refund is not linked to the caller"),
        @ApiResponse(responseCode = "404", description = "Refund not found")
    })
    @GetMapping("/refunds/{id}")
    public ResponseEntity<RefundDto> getRefund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getRefund(
                currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Gateway payment webhook (unauthenticated, IP rate limited)")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Webhook accepted for processing"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "429", description = "Too many webhook calls")
    })
    @PostMapping("/webhook")
    public ResponseEntity<PaymentWebhookResponse> webhook(
            @Valid @RequestBody PaymentWebhookRequest request,
            HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiter.tryAcquire("payments:webhook:" + ip, 100, 60_000)) {
            throw new TooManyRequestsException("Too many webhook calls");
        }
        webhookProcessor.process(request);
        PaymentWebhookResponse response = new PaymentWebhookResponse();
        response.setProcessedAt(java.time.LocalDateTime.now());
        return ResponseEntity.accepted().body(response);
    }
}
