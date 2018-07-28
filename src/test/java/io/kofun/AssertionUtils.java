package io.kofun;

import org.junit.Assert;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

final class AssertionUtils {

    private AssertionUtils() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class " + AssertionUtils.class.toString() + " is not meant to be instantiated");
    }

    static void assertPresent(String message, Optional<?> optional) {
        Assert.assertTrue(message, optional.isPresent());
    }

    static void assertNotPresent(String message, Optional<?> optional) {
        Assert.assertFalse(message, optional.isPresent());
    }

    static <T> void assertMatchesTruthTable(Function<TruthTable, TruthTable> truthTableBuilder,
                                            BiFunction<Predicate<T>, Predicate<? super T>, Predicate<? extends T>> functionToTest) {
        truthTableBuilder.apply(new TruthTable())
                         .rows()
                         .forEach(row -> {
                             final boolean actualResult = functionToTest.apply(any -> row.first, any -> row.second)
                                                                        .test(null);
                             assertEquals("expected " + row.result + " for arguments " + row.first + " and " + row.second + " but got " + actualResult,
                                          row.result,
                                          actualResult);
                         });
    }

    static class TruthTable {

        private final List<TruthRow> truthRows;

        private TruthTable() {
            truthRows = emptyList();
        }

        private TruthTable(List<TruthRow> truthRows) {
            this.truthRows = truthRows;
        }

        TruthTable withRow(int first, int second, int result) {
            final ArrayList<TruthRow> newTruthRows = new ArrayList<>(this.truthRows);
            newTruthRows.add(new TruthRow(first != 0, second != 0, result != 0));
            return new TruthTable(newTruthRows);
        }

        List<TruthRow> rows() {
            return truthRows;
        }

        static class TruthRow {
            private final boolean first;
            private final boolean second;
            private final boolean result;

            private TruthRow(boolean first, boolean second, boolean result) {
                this.first = first;
                this.second = second;
                this.result = result;
            }

            public boolean first() {
                return first;
            }

            public boolean second() {
                return second;
            }

            public boolean result() {
                return result;
            }
        }
    }

}
