package za.co.molorane.javastartshere.ch11;

/**
 * Demonstrates the timeless Java interview truth:
 * "Java is strictly Pass-by-Value. Always."
 *
 * For primitives, the value is the raw bits.
 * For objects, the value passed is the pointer address bits (a copy of the reference).
 */
public class PassByValuePhysics {

    public static class Account {
        private double balance;

        public Account(double balance) {
            this.balance = balance;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }

    /**
     * Modifies the internal state of the heap object pointed to by the reference copy,
     * but attempts to rebind the local reference parameter to a new object.
     */
    public static void modifyAccount(Account account, double newBalance, double rebindBalance) {
        account.setBalance(newBalance); // Modifies the object on the heap
        account = new Account(rebindBalance); // Rebinds LOCAL stack reference copy only!
    }

    /**
     * Classic interview trick:
     * Attempting to swap two references has zero effect on the caller's variables
     * because only local parameter copies are swapped on the stack frame.
     */
    public static void swap(Account a, Account b) {
        Account temp = a;
        a = b;
        b = temp;
    }
}
