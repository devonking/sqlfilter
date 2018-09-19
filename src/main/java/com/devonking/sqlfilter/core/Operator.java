package com.devonking.sqlfilter.core;

/**
 * description in here
 *
 * @author dexian.jin
 * @date 2018/9/8 13:51
 */
public enum Operator {

    /**
     * 等于 x = y
     */
    EQUAL_TO(1),
    /**
     * 不等于 x != y
     */
    NOT_EQUAL_TO(2),
    /**
     * 大于 x > y
     */
    GREATER_THAN(3),
    /**
     * 大于等于 x >= y
     */
    GREATER_THAN_OR_EQUAL_TO(4),
    /**
     * 小于 x < y
     */
    LESS_THAN(5),
    /**
     * 小于等于 x <= y
     */
    LESS_THAN_OR_EQUAL_TO(6),
    /**
     * 包含 k like '%y%'
     */
    LIKE(7),
    /**
     * 不包含 x not like '%y%'
     */
    NOT_LIKE(8),
    /**
     * 左包含 x like '%y'
     */
    LEFT_LIKE(9),
    /**
     * 不左包含 x not like '%y'
     */
    NOT_LEFT_LIKE(10),
    /**
     * 右包含 x like 'y%'
     */
    RIGHT_LIKE(11),
    /**
     * 不右包含 x not like 'y%'
     */
    NOT_RIGHT_LIKE(12),
    /**
     * 区间 k between x and y
     */
    BETWEEN_AND(13),
    /**
     * 为空 k is null
     */
    IS_NULL(14),
    /**
     * 不为空 k is not null
     */
    IS_NOT_NULL(15),
    /**
     * 属于 x in (1, 2, 3)
     */
    IN(16),
    /**
     * 不属于 x not in (1, 2, 3)
     */
    NOT_IN(17);

    private int code;

    Operator(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Operator get(int code) {
        for(Operator o : Operator.values()) {
            if(o.code == code) {
                return o;
            }
        }

        throw new RuntimeException("#Operator #get() enum item not found: code - " + code);
    }
}
