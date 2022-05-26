import java.util.Stack;

public class Convertor {

    private static int precedence(String s){
        switch (s){
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return -1;
        }
    }

    private static double performOperation(double a, double b, String op) {
        switch (op) {
            case "+" :
                return b + a;
            case "-" :
                return b - a;
            case "*" :
                return b * a;
            case "/" :
                return b / a;
            default:
                return -1;
        }
    }
    public static String convertToPostfix(String Expression){
        String result = "";
        Stack<String> operands = new Stack<String>();
        String expression = Expression;
        String[] elements = expression.split(" ");
        String temp;

        for(String e: elements) {

            if (precedence(e) > 0) {
                while (!operands.isEmpty() && precedence(operands.peek()) >= precedence(e)) {
                    result += operands.pop() + " ";
                }
                operands.push(e);
            }
            else if (e.equals(")")) {
                temp = operands.pop();
                while (!temp.equals("(")) {
                    result += temp + " ";
                    temp = operands.pop();
                }
            }
            else if (e.equals("("))
                operands.push(e);

            else
                result += e + " ";                     // Is not an operator, append to result
        }

        while (!operands.isEmpty()) {
            result += operands.pop() + " ";
        }

        return result;
    }

    public static String evaluatePostfix(String Expression){
        Stack<Double> stack = new Stack<>();
        double d;
        double e;

        for (String s: Expression.split("\s++")){
            try {
                d = Double.valueOf(s);
                stack.push(d);
            } catch (NumberFormatException n) {
                if (s.equals(" ")){
                    String t = String.format("%.0f", stack.pop());
                    String u = String.format("%.0f", stack.pop());
                    d = Double.valueOf((u + t));
                    stack.push(d);
                }
                else {
                    d = stack.pop();
                    e = stack.pop();
                    stack.push(performOperation(d, e, s));
                }
            }
        }
        return String.valueOf(stack.pop());
    }


}
