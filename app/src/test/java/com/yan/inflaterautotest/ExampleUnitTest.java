package com.yan.inflaterautotest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String str = "1100";
        int c = 0;
        for (int i = 0; i < str.length(); i++) {
            Character cr = str.charAt(i);
            int icr = (int) (Integer.parseInt(cr.toString()) * Math.pow(2, str.length() - i - 1));
            c += icr;
            System.out.print("\naddition_isCorrect    " + icr);
        }
        System.out.print("\naddition_isCorrect    " + str.length());
        System.out.print("\naddition_isCorrect    " + c);
        System.out.print("\n"+ ((c & 8) == 8) + "   " + ((c & 4) == 4) + "   " + ((c & 2) == 2) + "   " + ((c & 1) == 1));

    }
}