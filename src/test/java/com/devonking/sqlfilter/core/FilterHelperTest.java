package com.devonking.sqlfilter.core;

import org.junit.Test;


/**
 * description in here
 *
 * @author dexian.jin
 * @date 2018/9/19 10:22
 */
public class FilterHelperTest {

    @Test
   public void test01() {
        FilterHelper helper = new FilterHelper();
        helper
                .and("x", 1)
                .and("y", 2);
        helper.clearFilter();

        System.out.println(helper.sql());
   }

   @Test
   public void test02() {
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

        System.out.println(helper.sql());
   }
}