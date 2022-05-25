package project2csc4101;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Convertor {
    private static int exponent;
    private static int[] exponent_as_binary;
    private static int sign;
    private static double integral;
    private static int[] integral_as_binary;
    private static double fractional;
    private static int[] fractional_as_binary;
    private static double quotient;

    public Convertor(){}

    private static int[] resizeArray(int[] a){
        int index = 0;
        int[] b = new int[a.length + 1];
        for (int i: a) {
            b[index] = i;
            index++;
        }
        return b;

    }

    private static int[] combineArrays(int[] a, int[] b){
        int[] out = new int[a.length + b.length];
        for (int i = 0; i < a.length + b.length; i++){
            if (i > a.length - 1)
                out[i] = b[i-a.length];
            else
                out[i] = a[i];
        }
        return out;
    }

    private static int[] reverseArray(int[] in){
        int[] reversed = new int[in.length];
        int rev_index = 0;
        for (int i = in.length - 1; i >= 0; i--){
            reversed[rev_index] = in[i];
            rev_index +=1;
        }
        return reversed;
    }

    private static int[] fractionalToBinary(double current_num){
        int[] as_binary = new int[2];
        int i = 0;
        double _product;
        int _integral;
        while (current_num != 0){
            if (as_binary.length == 23)
                break;
            else if (i == as_binary.length)
                as_binary = resizeArray(as_binary);
            _product = current_num * 2;
            _integral = (int)(_product - (_product % 1));
            as_binary[i] = _integral;
            current_num = _product - _integral;
            i++;
        }
        return as_binary;
    }

    private static int[] findNegativeExponent(double f){
        double original = f;
        quotient = original;
        exponent = 0;
        int bias = 127;

        // divide the original value by 2 until the quotient is greater than 1.0
        while(quotient < 1.0){
            exponent--;
            quotient = original / (Math.pow(2,exponent));
        }

        quotient -= 1;

        return integralToBinary((bias + exponent));
    }

    private static int[] integralToBinary(double in){
        int i = 0;
        int[] as_binary = new int[4];
        int num = (int) in;
        while (num > 0){
            if (i == as_binary.length)
                as_binary = resizeArray(as_binary);
            as_binary[i] = num % 2;
            num /= 2;
            i++;
        }
        return reverseArray(as_binary);
    }

    private static int[] padBinary(int[] a){
        if (a.length == 8)
            return a;
        int[] out = new int[8];
        int padding = 8 - a.length;
        int a_index = 0;
        for (int i = 0; i < out.length; i ++){
            if (i < padding)
                out[i] = 0;
            else
                out[i] = a[a_index++];
        }
        return out;
    }

    private static int[] findPositiveExponent(int[] integral){
        boolean greater_than_zero = false;
        int bias = 127;

        // Go through integral ensure its not all zeroes
        for (int i: integral)
            if (i == 1) {
                greater_than_zero = true;
                break;
            }
        if (greater_than_zero){
            for (int i = 0; i < integral.length; i++){
                if (integral[i] == 1){
                    exponent = (integral.length - 1) - i;
                    break;
                }
            }
        }

        return integralToBinary((bias + exponent));
    }

    private static int[] makeMantissa(int[] i, int[] f, boolean has_integral){
        int[] _mantissa = new int[23];
        int m = 0;
        if (has_integral) {
            int[] full = combineArrays(i, f);
            for (int x : i) {
                if (x == 1) {

                }
            }
            if (full.length == 1)
                full = f;
            boolean beginMantissa = false;
            for (int index = 0; index < full.length; index++) {
                if (m == 23)
                    break;
                if (beginMantissa) {
                    if (index == full.length - 1) {
                        _mantissa[m] = 0;
                        break;
                    } else
                        _mantissa[m] = full[index];
                    m++;

                } else {
                    if (full[index] == 1) {
                        beginMantissa = true;
                        continue;
                    }
                }
            }
        }
        else{
            _mantissa = f;
        }
        return _mantissa;
    }


    public static String IEEE754(double decimal){
        int[] as_binary = new int[1];
        sign = (decimal >= 0) ? 0 : 1;
        if (sign == 1)
            decimal *= -1;
        fractional = decimal % 1;
        integral = decimal - fractional;

        integral_as_binary = integralToBinary(integral);

        if (Math.abs(decimal) > 1) {
            exponent_as_binary = padBinary(findPositiveExponent(integral_as_binary));
            fractional_as_binary = fractionalToBinary(fractional);
        }

        else {
            exponent_as_binary = padBinary(findNegativeExponent(fractional));
            fractional_as_binary = fractionalToBinary(quotient);
        }


        as_binary[0] = sign;
        as_binary = combineArrays(as_binary,exponent_as_binary);
        as_binary = combineArrays(as_binary, makeMantissa(integral_as_binary, fractional_as_binary, (decimal >= 1.0)? true : false));

        String out = "";

        for (int i = 0; i < as_binary.length; i++){
            if (i == 0)
                out += as_binary[i] + " | ";
            else if (i < 9)
                out += as_binary[i];
            else if (i == 9)
                out += " | " + as_binary[i];
            else
                out += as_binary[i];
        }
        return out;
    }
}
