package utility;

import utility.elbonian.ElbonianArabicConverter;
import utility.elbonian.MalformedNumberException;
import utility.elbonian.ValueOutOfBoundsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for the ElbonianArabicConverter class.
 */
public class ConverterTests {

    @Test
    public void ElbonianToArabicSampleTest() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter converter = new ElbonianArabicConverter("1");
        assertEquals(converter.toElbonian(), "I");
    }

    @Test
    public void ArabicToElbonianSampleTest() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter converter = new ElbonianArabicConverter("I");
        assertEquals(converter.toArabic(), 1);
    }

    @Test(expected = MalformedNumberException.class)
    public void malformedNumberTest() throws MalformedNumberException, ValueOutOfBoundsException {
        throw new MalformedNumberException("TEST");
    }

    @Test(expected = ValueOutOfBoundsException.class)
    public void valueOutOfBoundsTest() throws MalformedNumberException, ValueOutOfBoundsException {
        throw new ValueOutOfBoundsException("TEST");
    }

    /////////NEW TESTS/////////////////
    @Test
    public void testLeadingSpace() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter converter = new ElbonianArabicConverter("   2");
        assertEquals(converter.toElbonian(), "II");
    }

    @Test
    public void testTrailingSpace() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter converter = new ElbonianArabicConverter("2    ");
        assertEquals(converter.toElbonian(), "II");
    }

    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberMiddleZero() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter converter = new ElbonianArabicConverter("1 5");
    }

    @Test(expected = ValueOutOfBoundsException.class)
    public void testIsValidNumberArabicInBounds1() throws MalformedNumberException, ValueOutOfBoundsException{
        //4332 is the max integer that can be converted
        ElbonianArabicConverter converter = new ElbonianArabicConverter("4333");
    }
    @Test(expected = ValueOutOfBoundsException.class)
    public void testIsValidNumberArabicInBounds2() throws MalformedNumberException, ValueOutOfBoundsException{
        //1 is the minimum
        ElbonianArabicConverter converter = new ElbonianArabicConverter("0");
    }
    @Test
    public void testIsValidNumberArabicInBounds3() throws MalformedNumberException, ValueOutOfBoundsException{
        //4332 is the max integer that can be converted
        ElbonianArabicConverter converter = new ElbonianArabicConverter("4332");
        assertEquals(converter.toElbonian(),"MMMDeCCCLmXXXVwIII");
    }

    //Test that there letters M, C, X, and I are all in a row
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheckRowM() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("MeM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheckRowC() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c2 = new ElbonianArabicConverter("CeC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheckRowX() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c3 = new ElbonianArabicConverter("XeX");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheckRowI() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c4  = new ElbonianArabicConverter("IeI");
    }

    //Test that there are not more than 3 of the letters M, C, X, or I in a number
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheck4xM() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("MMMM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheck4xC() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c2 = new ElbonianArabicConverter("CCCC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheck4xX() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c3 = new ElbonianArabicConverter("XXXX");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianCheck4xI() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c4 = new ElbonianArabicConverter("IIII");
    }

    //Test that there any number that starts with a 4 or 5 is only used once
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleD() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("DD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleL() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("LL");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleV() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VV");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleE() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("ee");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleM() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("mm");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianMultipleW() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("ww");
    }

    //Test that letters are in the order of M, D, e, C, L, m, X, V, w, I
        //M
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder1() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("DM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder2() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("eM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder3() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("CM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder4() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("LM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder5() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("mM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder6() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("XM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder7() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder8() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wM");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder9() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("IM");
    }
        //D
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder10() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("eD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder11() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("CD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder12() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("LD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder13() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("mD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder14() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("XD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder15() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder16() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wD");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder17() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("ID");
    }
        //e
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder18() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Ce");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder19() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Le");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder20() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("me");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder21() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Xe");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder22() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Ve");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder23() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("we");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder24() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Ie");
    }
        //C
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder25() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("LC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder26() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("mC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder27() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("XC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder28() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder29() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wC");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder30() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("IC");
    }
        //L
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder31() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("mL");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder32() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("XL");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder33() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VL");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder34() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wL");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder35() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("IL");
    }
        //m
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder36() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Xm");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder37() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Vm");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder38() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wm");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder39() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Im");
    }
        //X
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder40() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("VX");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder41() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wX");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder42() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("IX");
    }
        //V
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder43() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("wV");
    }
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder44() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("IV");
    }
        //w
    @Test(expected = MalformedNumberException.class)
    public void testIsValidNumberElbonianInOrder45() throws MalformedNumberException, ValueOutOfBoundsException{
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("Iw");
    }
        //I
        //Everything can come before I

    //Test converting a few numbers from elbonian
    @Test
    public void testToArabic1() throws MalformedNumberException, ValueOutOfBoundsException {
        ElbonianArabicConverter c1 = new ElbonianArabicConverter("MMeCI"); //2501
        assertEquals(c1.toArabic(), 2501);
        //assertEquals(c1.toElbonian, "MMeCI");
    }
}
