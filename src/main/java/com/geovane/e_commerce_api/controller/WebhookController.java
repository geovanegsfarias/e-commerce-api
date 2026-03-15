package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.exception.PaymentException;
import com.geovane.e_commerce_api.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/stripe")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    @Value("${spring.stripe.webhook.secret}")
    private String webhookSecret;
    private final StripeService stripeService;

    @Autowired
    public WebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public String handleWebhookEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String header) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, header, webhookSecret);
            switch (event.getType()) {

                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null) stripeService.handlePaymentSuccess(session);
                    break;

                case "checkout.session.expired":
                    Session expiredSession = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (expiredSession != null) stripeService.handlePaymentFailed(expiredSession);
                    break;

                default:
                    log.warn("Unhandled Stripe event: {}", event.getType());
            }
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature error: {}", e.getMessage());
            throw new PaymentException("Signature error.");
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
            throw new PaymentException("Webhook error.");
        }

        return "ok";
    }

}
