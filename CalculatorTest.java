public final class CalculatorTest {
    private static final double EPSILON = 1e-9;

    private CalculatorTest() {
        // Utility class
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > EPSILON) {
            throw new AssertionError(message + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertThrows(String message, Runnable operation) {
        try {
            operation.run();
            throw new AssertionError(message + " did not throw an exception");
        } catch (IllegalArgumentException ex) {
            // Expected
        }
    }

    private static void testAdd() {
        assertEquals(5.0, Calculator.add(2, 3), "add");
    }

    private static void testSubtract() {
        assertEquals(5.5, Calculator.subtract(10.0, 4.5), "subtract");
    }

    private static void testMultiply() {
        assertEquals(6.0, Calculator.multiply(1.5, 4), "multiply");
    }

    private static void testDivide() {
        assertEquals(3.0, Calculator.divide(9, 3), "divide");
    }

    private static void testDivideByZero() {
        assertThrows("divide by zero", () -> Calculator.divide(9, 0));
    }

    public static void main(String[] args) {
        testAdd();
        testSubtract();
        testMultiply();
        testDivide();
        testDivideByZero();
        System.out.println("All Calculator tests passed.");
    }
}
