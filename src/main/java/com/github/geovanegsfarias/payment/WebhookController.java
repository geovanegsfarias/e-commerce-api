package com.github.geovanegsfarias.payment;

import com.github.geovanegsfarias.configuration.StripeConfigurationProperties;
import com.github.geovanegsfarias.exception.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/webhook/stripe")
@Slf4j
@Hidden
public class WebhookController {
    private final StripeService stripeService;
    private final StripeConfigurationProperties stripeProperties;

    public WebhookController(StripeService stripeService, StripeConfigurationProperties stripeProperties) {
        this.stripeService = stripeService;
        this.stripeProperties = stripeProperties;
    }

    @PostMapping
    public String handleWebhookEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String header) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, header, stripeProperties.webhookSecret());
            switch (event.getType()) {

                case "checkout.session.completed":
                    var session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null) stripeService.handlePaymentSuccess(session);
                    break;

                case "checkout.session.expired":
                    var expiredSession = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (expiredSession != null) stripeService.handlePaymentFailed(expiredSession);
                    break;

                default:
                    log.warn("Unhandled Stripe event: {}", event.getType());
            }
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature error: {}", e.getMessage());
            throw new PaymentException("Signature error");
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
            throw new PaymentException("Webhook error");
        }

        return "ok";
    }

}
