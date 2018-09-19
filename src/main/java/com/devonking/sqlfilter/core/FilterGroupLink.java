package com.devonking.sqlfilter.core;

/**
 * description in here
 *
 * @author dexian.jin
 * @date 2018/9/8 17:01
 */
public class FilterGroupLink {

    private Link link;

    private FilterGroup filterGroup;

    public FilterGroupLink(Link link, FilterGroup filterGroup) {
        this.link = link;
        this.filterGroup = filterGroup;
    }

    public String toSql() {
        switch (link) {
            case AND:
                return "AND " + filterGroup.toSql();

            case OR:
                return "OR " + filterGroup.toSql();

            case EMPTY:
                return filterGroup.toSql();
        }

        return "";
    }
}
