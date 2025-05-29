package doji.doe.carsharing.util;

import com.stripe.model.checkout.Session;
import doji.doe.carsharing.model.Payment;

public class SessionTestUtil {
    public static Session createSession(Payment payment) {
        Session session = new Session();
        session.setUrl(payment.getSessionUrl());
        session.setId(payment.getSessionId());
        session.setAmountTotal(payment.getAmountToPay().longValue());
        return session;
    }
}
