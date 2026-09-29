package za.co.molorane.javastartshere.ch04;

/**
 * Demonstrates Visibility Promotion (Access Widening):
 * Overriding permits widening access (protected -> public) to satisfy LSP,
 * but strictly forbids narrowing access (public -> protected/private).
 */
public class VisibilityPromotion {

    public static class BaseService {
        // Protected visibility
        protected String executeOperation() {
            return "Base protected operation executed";
        }
    }

    public static class EnterpriseService extends BaseService {
        // Widened visibility: promoted from protected to public
        @Override
        public String executeOperation() {
            return "Promoted public enterprise operation executed";
        }
    }
}
