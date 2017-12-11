package utility.elbonian;

import utility.elbonian.MalformedNumberException;
import utility.elbonian.ValueOutOfBoundsException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements a converter that takes a string that represents a number in either the
 * Elbonian or Arabic numeral form. This class has methods that will return a value in the chosen form.
 *
 * @version 3/18/17
 */
public class ElbonianArabicConverter {

    // A string that holds the number (Elbonian or Arabic) you would like to convert
    private final String number;


    /**
     * Constructor for the ElbonianArabic class that takes a string. The string should contain a valid
     * Elbonian or Arabic numeral. The String can have leading or trailing spaces. But there should be no
     * spaces within the actual number (ie. "9 9" is not ok, but " 99 " is ok). If the String is an Arabic
     * number it should be checked to make sure it is within the Elbonian number systems bounds. If the
     * number is Elbonian, it must be a valid Elbonian representation of a number.
     *
     * @param number A string that represents either a Elbonian or Arabic number.
     * @throws MalformedNumberException Thrown if the value is an Elbonian number that does not conform
     * to the rules of the Elbonian number system. Leading and trailing spaces should not throw an error.
     * @throws ValueOutOfBoundsException Thrown if the value is an Arabic number that cannot be represented
     * in the Elbonian number system.
     */
    public ElbonianArabicConverter(String number) throws MalformedNumberException, ValueOutOfBoundsException {

        String s = number.trim();
        if(isElbonian(s) && !isValidNumber(s)) throw new MalformedNumberException(number);
        if(!isElbonian(s) && !inBounds(s)) throw new ValueOutOfBoundsException(number);

        this.number = s;
    }

    /**
     * Converts the number to an Arabic numeral or returns the current value as an int if it is already
     * in the Arabic form.
     *
     * @return An arabic value
     */
    public int toArabic() {
        if(!isElbonian(number)) return Integer.parseInt(number);

        int returnNum = 0;
        for(int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if(c == 'M') returnNum += 1000;
            else if(c == 'D') returnNum += 500;
            else if(c == 'e') returnNum += 400;
            else if(c == 'C') returnNum += 100;
            else if(c == 'L') returnNum += 50;
            else if(c == 'm') returnNum += 40;
            else if(c == 'X') returnNum += 10;
            else if(c == 'V') returnNum += 5;
            else if(c == 'w') returnNum += 4;
            else if(c == 'I') returnNum += 1;
        }
        return returnNum;
    }

    /**
     * Converts the number to an Elbonian numeral or returns the current value if it is already in the Elbonian form.
     *
     * @return An Elbonian value
     */
    public String toElbonian() {
        if(isElbonian(number)) return number;

        int temp = Integer.parseInt(number);
        String returnStr = "";
        int count = 0;
        while(temp >= 1000 && count<3){
            returnStr=returnStr+"M";
            temp-=1000;
            count++;
        }
        count = 0;
        if(temp>=500){
            returnStr = returnStr +"D";
            temp-=500;
        }
        if(temp>=400){
            returnStr = returnStr +"e";
            temp-=400;
        }
        while(temp >=  100 && count < 3){
            returnStr=returnStr+"C";
            temp-=100;
            count++;
        }
        count = 0;
        if(temp>=50){
            returnStr = returnStr +"L";
            temp-=50;
        }
        if(temp>=40){
            returnStr = returnStr +"m";
            temp-=40;
        }
        while(temp >=  10 && count < 3){
            returnStr=returnStr+"X";
            temp-=10;
            count++;
        }
        count = 0;
        if(temp>=5){
            returnStr = returnStr +"V";
            temp-=5;
        }
        if(temp>=4){
            returnStr = returnStr +"w";
            temp-=4;
        }
        while(temp >=  1 && count < 3){
            returnStr=returnStr+"I";
            temp-=1;
            count++;
        }
        count = 0;
        return returnStr;
    }

    private static boolean isElbonian(String s){
        boolean b = false;
        try{
            Integer.parseInt(s);
        }catch (NumberFormatException e){
            b = true;
        }
        return b;
    }

    private static boolean inBounds(String s) {
        // Check Arabic
        if(Integer.parseInt(s) < 1 || Integer.parseInt(s) > 4332){
            return false;
        }
        return true;
    }

    private static boolean isValidNumber(String s) {
        String input = s;
        String temp = s.replaceFirst("MMM", "");
        if(temp.equals(input)) {
            temp = temp.replaceFirst("MM","");
            if(temp.equals(input)){
                temp = temp.replaceFirst("M","");
                if(temp.equals(input)){
                    //good for repeat Ms
                }
            }
        }
        if(temp.contains("M")) return false;

        input = temp;
        temp = temp.replaceFirst("CCC", "");
        if(temp.equals(input)) {
            temp = temp.replaceFirst("CC","");
            if(temp.equals(input)){
                temp = temp.replaceFirst("C","");
                if(temp.equals(input)){
                    //good for repeat Cs
                }
            }
        }
        if(temp.contains("C")) return false;

        input = temp;
        temp = temp.replaceFirst("XXX", "");
        if(temp.equals(input)) {
            temp = temp.replaceFirst("XX","");
            if(temp.equals(input)){
                temp = temp.replaceFirst("X","");
                if(temp.equals(input)){
                    //good for repeat Xs
                }
            }
        }
        if(temp.contains("X")) return false;

        input = temp;
        temp = temp.replaceFirst("III", "");
        if(temp.equals(input)) {
            temp = temp.replaceFirst("II","");
            if(temp.equals(input)){
                temp = temp.replaceFirst("I","");
                if(temp.equals(input)){
                    //good for repeat Is
                }
            }
        }
        if(temp.contains("I")) return false;

        input = temp;
        temp = temp.replaceFirst("D", "");
        if(temp.contains("D")) return false;

        input = temp;
        temp = temp.replaceFirst("e", "");
        if(temp.contains("e")) return false;

        input = temp;
        temp = temp.replaceFirst("L", "");
        if(temp.contains("L")) return false;

        input = temp;
        temp = temp.replaceFirst("m", "");
        if(temp.contains("m")) return false;

        input = temp;
        temp = temp.replaceFirst("V", "");
        if(temp.contains("V")) return false;

        input = temp;
        temp = temp.replaceFirst("w", "");
        if(temp.contains("w")) return false;



        input = temp;
        if(input.equals("") && checkOrder(s)) return true;

        return false;
    }

    static boolean checkOrder(String s){

        if(s.equals("")) return true;

        ArrayList<String> letters = new ArrayList<>(Arrays.asList("I","w","V","X","m","L","C","e","D","M")); //0-9
        String firstC = s.substring(0,1);
        //DDmXI

        for(int i= letters.indexOf(firstC)+1; i < letters.size(); i++) {
            if(s.contains(letters.get(i))) return false;
        }
        return checkOrder(s.replaceFirst(firstC,""));
    }
}
