package org.jared;

import io.vavr.collection.List;
import io.vavr.control.Either;
import org.jared.twentytwo.ListCompare;
import org.jared.twentytwo.ListNode;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ListCompareTest {

    ListCompare sut = new ListCompare();
    @Test
    void deserialize_simpleCase() {
        String input = "[1,2,3]";

        assertThat(sut.deserialize(input),
                equalTo(List.of(Either.right(1), Either.right(2), Either.right(3))));
    }

    @Test
    void deserialize_oneNestCase() {
        String input = "[1,[2,3],3]";

        ListNode result = sut.deserialize(input);
        ListNode list = new ListNode(List.of(Either.left(1),
                Either.right(new ListNode(List.of(Either.left(2), Either.left(3)))),
                Either.left(3)));
        assertThat(result,
                equalTo(list));
    }
}