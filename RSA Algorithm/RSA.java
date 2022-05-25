package rsa.encrypt;
import java.util.Scanner;  
import java.awt.EventQueue;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;


public class RSAEncrypt {
    
    public static void main(String[] args) {
       /* Lines 16-38 are a function to calculate the 10th and 19th prime numbers
       *  of a specified range that is specified by the user
       */
       int primeNum[] = new int[1500];
       int count = 0;
       int prime10 = 0;
       int prime19 = 0;
       int e; 
       Scanner s = new Scanner(System.in);  
       System.out.print("Enter the first number : ");  
       int start = s.nextInt();  
       System.out.print("Enter the second number : ");  
       int end = s.nextInt();  
       System.out.println("Please enter your message you wish to encrypt: ");
       String message = s.next();
       System.out.println("Input: " + message);
       for (int i = start; i <= end; i++) {  
           if (isPrime(i)) {  
                primeNum[count] = i;
                count++;  
           }  
       }  
       prime10 = primeNum[9];
       prime19 = primeNum[18];
       System.out.printf("10th Prime Number: %d\n", prime10);
       System.out.printf("19th Prime Number: %d\n", prime19);
       
       //N is calculated by the multiplication of the 10th and 19th prime numbers
       int n = prime10 * prime19;
       //Cast N to a BigInteger variable to be utilized later in encrypt/decrypt
       BigInteger nB =  BigInteger.valueOf(n);
       System.out.printf("N Number: %d\n", n);
       //F(N) is calculated by (p-1)*(q-1)
       int phi = (prime10 - 1)*(prime19 - 1);
       System.out.printf("F(N): %d\n", phi);
       //Loop function to find e
       for(e = 2; e < phi; e++){
             if (gcd(e, phi) == 1) {
                break;
            }
        }
       System.out.println("Public Key: " +  "{" + e + ", " + n + "}");
       //Casting e, phi, and d to BigInteger to be used later by encrypt/decrypt
       BigInteger eI = BigInteger.valueOf(e);
       BigInteger phiB = BigInteger.valueOf(phi);
       BigInteger d = eI.modInverse(phiB);
       System.out.println("Private Key: " +  "{" + d + ", " + prime10 + ", " + prime19 + "}");
       
       BigInteger cipherMessage = Cipher(message);       
       BigInteger encrypted = encrypt(cipherMessage, eI, nB);
       BigInteger decrypted = decrypt(encrypted, d, nB);
       String restoredMessage = restoredString(decrypted);

       System.out.println("Original message: " + message);
       System.out.println("Ciphered: " + cipherMessage);
       System.out.println("Encrypted: " + encrypted);
       System.out.println("Decrypted: " + decrypted);
       System.out.println("Restored: " + restoredMessage);
    }
    //Function used by Prime Number finder to check if numbers selected are Prime
    public static boolean isPrime(int n) {  
       if (n <= 1) {  
           return false;  
       }  
       for (int i = 2; i <= Math.sqrt(n); i++) {  
           if (n % i == 0) {  
               return false;  
           }  
       }  
       return true;  
   } 
    
    public static int gcd(int e, int phi) {
		if (e == 0) {
                    return phi;
		} 
                else {
                    return gcd((phi%e), e);
		}
    }
    //Cipher function to convert string message into equal ASCII value
    public static BigInteger Cipher(String message) {
		message = message.toUpperCase();
		String cipherString = "";
		int i = 0;
		while (i < message.length()) {
			int ch = (int) message.charAt(i);
			cipherString = cipherString + ch;
			i++;
		}
		BigInteger cipherBig = new BigInteger(String.valueOf(cipherString));
		return cipherBig;
	}
    //Encrypt function where the ASCII Message is set to (M^e)% n
    public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
	return message.modPow(e, n);
    }
    //Decrypt function where the encrypted message is reversed by (M^d) % n
    public static BigInteger decrypt(BigInteger message, BigInteger d, BigInteger n) {
	return message.modPow(d, n);
    }
    
    //Decrypted ASCII String is then restored to character values to reveal original message
    public static String restoredString(BigInteger message) {
                String cipherString = message.toString();
		String output = "";
		int i = 0;
		while (i < cipherString.length()) {
			int temp = Integer.parseInt(cipherString.substring(i, i + 2));
			char ch = (char) temp;
			output = output + ch;
			i = i + 2;
		}
		return output;
    }
}
