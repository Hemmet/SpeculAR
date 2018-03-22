package com.mate.specular;

import com.mate.specular.model.Color;

import org.junit.Test;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test(){
        //Right now there are only 4 colors on use so I'll hardcode this.
        // Normally it'd have been able to get which colors are on use.
        List<Color> possibleColorsToBeUsed = new ArrayList<>();
        possibleColorsToBeUsed.add(Color.RED);
        possibleColorsToBeUsed.add(Color.BLUE);
        possibleColorsToBeUsed.add(Color.GREEN);
        possibleColorsToBeUsed.add(Color.GOLD);

        List<List<Color>> permutations = Generator
                .permutation(possibleColorsToBeUsed)
                .withRepetitions(4)
                .stream()
                .collect(Collectors.<List<Color>>toList());
        for (List<Color> p:
             permutations) {
            System.out.println(p);
        }
    }
}