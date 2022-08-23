import java.util.Scanner;

public class Main {

    static String[] romanAr = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C"};
    static int[] arabicAr = {1, 4, 5, 9, 10, 40, 50, 90, 100};
    static char[] signsAr = {'*', '/', '+', '-'};
    static boolean[] flags = new boolean[6]; //хранят флаги для вызова некоторых исключений и функции toArabic
    static int index;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите выражение (в любом регистре): ");
        String incoming = scan.nextLine();
        scan.close();
        String s = incoming.toUpperCase();
        String incomingData = s.replaceAll("\\u0020+", "");
        int[] operand = new int[2];

        try {
            index = findOperator(incomingData);
            if (flags[0] ){
                throw new Exception("Вы не ввели оператор");
            }
            else if (flags[1] ){
                throw new Exception("Вы ввели " + index + " оператора вместо одного");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            operand = transform(incomingData);
            if (flags[2] ) {
                throw new Exception("Числа больше 10");
            }
        } catch (ClassCastException e) {
            System.out.println("Вы ввели не числа");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            }
        try {
            String result = calc(transform(incomingData), incomingData);
            if (flags[2] ) {
                throw new Exception("Числа больше 10");
            }
            else if (flags[3] ) {
                throw new Exception("Числа меньше 0");
            }
            else if (flags[4] ) {
                throw new Exception("Результат вычислений меньше или равен нулю");
            }
            System.out.print("=" + result);
        } catch (Exception e) {
                System.out.println(e.getMessage());
        }

    }

    public static int findOperator (String incomingData) {
        int counterSigns = 0;
        int indexSign = -1;

        for (int i=0; i < incomingData.length(); i++) {
            for (char c : signsAr) {
                if (incomingData.charAt(i) == c) {
                    counterSigns++;
                    indexSign = i;
                }
            }
        }
        if (counterSigns == 0){
            flags[0] = true;
        }
        else if (counterSigns == 1){
            return indexSign;
        }
        else{
            flags[1] = true;
        }
        return counterSigns;
    }

    public static int[] transform(String incomingData) {
        String[] operation = incomingData.split("[/*+-]");
        int[] operands = new int[2];
        int counter = 0;
        int sum = 0;
        for (String s : operation) {
            sum += s.length();
            String[] ch = s.split("");
            for (String value : ch) {
                for (String item : romanAr) {
                    if (value.equals(item)) {
                        counter++;
                        break;
                    }
                }
            }
        }
        if (counter == sum) {
            flags[5] = true;
            for (int i=0; i < operation.length; i++) {
                operands[i] = toArabic(operation[i]);
            }
        }
        else {
            for (int i=0; i < operation.length; i++) {
                operands[i] = Integer.parseInt(operation[i]);
                if (operands[i] > 10) {
                    flags[2] = true;
                }
                else if (operands[i] < 0) {
                    flags[3] = true;
                }
            }
        }
        return operands;
    }

    public static int toArabic(String operands) {
        int number = 0;
        for (int i=0; i < operands.length(); i++){
            switch (operands.charAt(i)){
                case ('X'):
                    number += 10;
                    break;
                case ('V'):
                    number += 5;
                    break;
                case ('I'):
                    if ( i+1 < operands.length() && operands.charAt(i+1) == 'X') {
                        number += 9;
                        i++;
                    }
                    else if ( i+1 < operands.length() && operands.charAt(i+1) == 'V'){
                        number += 4;
                        i++;
                    }
                    else { number += 1; }
                    break;
            }
        }
        if (number > 10) { flags[2] = true; }
        return number;
    }
    public static String toRoman(int result) {
        StringBuilder romanResult = new StringBuilder();
        int m;
        for (int i = romanAr.length - 1; i >= 0; i--){
            m = result / arabicAr[i];
            for (int j=0; j < m; j++){
                romanResult.append(romanAr[i]);
            }
            result -= arabicAr[i] * m;
        }

        return romanResult.toString();
    }
    public static String calc(int[] operands, String incomingData){
        int result = switch (incomingData.charAt(index)) {
            case ('+') -> operands[0] + operands[1];
            case ('-') -> operands[0] - operands[1];
            case ('*') -> operands[0] * operands[1];
            case ('/') -> operands[0] / operands[1];
            default -> 0;
        };
        if (flags[5] & result <= 0){
            flags[4] = true;         //здесь я уже мог вставить throw exception, но раз уж начал использовать флаги...
        }
        else if (flags[5]  & result > 0) {
            return toRoman(result);
        }
        return Integer.toString(result);
    }
}

