package org.jared;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.jared.twentytwo.RockPaperScissors.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RockPaperScissorsTest {
    @Test
    public void testA_X() {
        assertThat(score('A', 'X'), equalTo(3)); // 3
    }

    @Test
    public void testA_Y() {
        assertThat(score('A', 'Y'), equalTo(4)); // 1
    }

    @Test
    public void testA_Z() {
        assertThat(score('A', 'Z'), equalTo(8)); // 2
    }

    @Test
    public void testB_X() {
        assertThat(score('B', 'X'), equalTo(1)); // 1
    }

    @Test
    public void testB_Y() {
        assertThat(score('B', 'Y'), equalTo(5)); // 2
    }

    @Test
    public void testB_Z() {
        assertThat(score('B', 'Z'), equalTo(9)); // 3
    }

    @Test
    public void testC_X() {
        assertThat(score('C', 'X'), equalTo(2)); // 2
    }

    @Test
    public void testC_Y() {
        assertThat(score('C', 'Y'), equalTo(6)); // 3
    }

    @Test
    public void testC_Z() {
        assertThat(score('C', 'Z'), equalTo(7)); // 1
    }
}