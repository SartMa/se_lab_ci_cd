import java.util.Locale;
import java.util.Scanner;

/**
 * Simple command-line calculator that supports the four basic operations.
 *
 * Usage examples:
 *   java Calculator add 3 4
 *   java Calculator divide 10 2
 *
 * When run without arguments, the application falls back to an interactive
 * prompt so that the calculator can be exercised manually.
 */
public class Calculator {

	public static double add(double a, double b) {
		return a + b;
	}

	public static double subtract(double a, double b) {
		return a - b;
	}

	public static double multiply(double a, double b) {
		return a * b;
	}

	public static double divide(double a, double b) {
		if (b == 0) {
			throw new IllegalArgumentException("Cannot divide by zero");
		}
		return a / b;
	}

	private static double calculate(String operation, double left, double right) {
		switch (operation.toLowerCase(Locale.ROOT)) {
			case "add":
			case "+":
				return add(left, right);
			case "subtract":
			case "sub":
			case "-":
				return subtract(left, right);
			case "multiply":
			case "mul":
			case "*":
				return multiply(left, right);
			case "divide":
			case "div":
			case "/":
				return divide(left, right);
			default:
				throw new IllegalArgumentException("Unsupported operation: " + operation);
		}
	}

	private static void printUsage() {
		System.err.println("Usage: java Calculator <add|subtract|multiply|divide> <left> <right>");
		System.err.println("Example: java Calculator add 4 5");
	}

	private static void runInteractive() {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Interactive mode. Type 'exit' to quit.");
			while (true) {
			System.out.print("Operation (add/subtract/multiply/divide): ");
			String operation = scanner.next();
			if ("exit".equalsIgnoreCase(operation)) {
				break;
			}
			try {
				System.out.print("Left operand: ");
				double left = scanner.nextDouble();
				System.out.print("Right operand: ");
				double right = scanner.nextDouble();
				double result = calculate(operation, left, right);
				System.out.println("Result: " + result);
			} catch (IllegalArgumentException ex) {
				System.err.println(ex.getMessage());
			} catch (Exception ex) {
				System.err.println("Invalid number. Please try again.");
				scanner.nextLine();
			}
			}
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);

		if (args.length == 0) {
			runInteractive();
			return;
		}

		if (args.length != 3) {
			printUsage();
			System.exit(2);
		}

		try {
			double left = Double.parseDouble(args[1]);
			double right = Double.parseDouble(args[2]);
			double result = calculate(args[0], left, right);
			System.out.println(result);
		} catch (NumberFormatException ex) {
			System.err.println("Operands must be numbers.");
			System.exit(3);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			System.exit(4);
		}
	}
}
