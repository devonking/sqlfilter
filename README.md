# sqlfilter
A utility for assembling SQL conditional clause

Usage 1.

FilterHelper helper = new FilterHelper();
helper
        .and("x", 1)
        .and("y", 2);
helper.clearFilter();

==> returns sql: "x = '1' AND y = '2'"


Usage 2.

FilterHelper helper = new FilterHelper();
helper
        .and("x", 1)
        .and("y", 2)
        .begin()
        .and("a", 0)
        .or("a", Operator.IS_NULL)
        .end()
        .and("z", 3);
helper.clearFilter();

==> returns sql: "x = '1' AND y = '2' AND (a = '0' OR a IS NULL) AND z = '3'"