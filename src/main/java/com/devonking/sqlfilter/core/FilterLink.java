package com.devonking.sqlfilter.core;

/**
 * description in here
 *
 * @author dexian.jin
 * @date 2018/9/8 14:28
 */
public class FilterLink {

    private Link link;

    private Filter filter;

    public FilterLink(Link link, Filter filter) {
        this.link = link;
        this.filter = filter;
    }

    public String toSql() {
        switch (link) {
            case AND:
                return "AND " + filter.toSql();

            case OR:
                return "OR " + filter.toSql();

            case EMPTY:
                return filter.toSql();
        }

        return "";
    }
}
