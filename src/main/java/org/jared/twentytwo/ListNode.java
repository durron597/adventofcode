package org.jared.twentytwo;

import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.Value;

@Value
public class ListNode implements Comparable<ListNode> {
    List<Either<Integer, ListNode>> contents;

    @Override
    public int compareTo(ListNode o) {
        for (int i = 0; i < this.getContents().size(); i++) {
            if (i >= o.getContents().size()) {
                return 1;
            }
            Either<Integer, ListNode> leftCurrent = this.getContents().get(i);
            Either<Integer, ListNode> rightCurrent = o.contents.get(i);
            if (leftCurrent.isLeft() && rightCurrent.isLeft()) {
                int compare = Integer.compare(leftCurrent.getLeft(), rightCurrent.getLeft());
                if (compare != 0) return compare;
                continue;
            }
            ListNode leftNode = leftCurrent.getOrElseGet(a -> new ListNode(List.of(Either.left(a))));
            ListNode rightNode = rightCurrent.getOrElseGet(a -> new ListNode(List.of(Either.left(a))));
            int compare = leftNode.compareTo(rightNode);
            if (compare != 0) return compare;
        }
        if (this.getContents().size() < o.getContents().size()) {
            return -1;
        }

        System.out.println("RETURNING ZERO!!");

        return 0;
    }
}
