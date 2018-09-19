package com.devonking.sqlfilter.core;

import com.devonking.sqlfilter.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Sql filter
 *
 * <p>E.g.,
 *      x > y
 * </p>
 *
 * @author dexian.jin
 * @date 2018/9/8 13:48
 */
public class Filter {
    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    /**
     * An id generator
     */
    private static final ThreadLocal<AtomicInteger> ID_GENERATOR = new ThreadLocal<>();

    /**
     * An identity of this object's instance within current thread
     */
    private int id;

    /**
     * The left operand of the expression
     */
    private String key;

    /**
     * The operator of the expression
     */
    private Operator operator;

    /**
     * The right operand of the expression
     */
    private Object value;

    public Filter(String key, Object value) {
        this(key, Operator.EQUAL_TO, value);
    }

    public Filter(String key, Operator operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;

        synchronized (this) {
            if (null == ID_GENERATOR.get()) {
                ID_GENERATOR.set(new AtomicInteger());
            }
        }
        this.id = ID_GENERATOR.get().incrementAndGet();

        if (null == value) {
            log.debug("{} - created: {} {}", getName(), key, operator);
        } else {
            log.debug("{} - created: {} {} {}", getName(), key, operator, value);
        }
    }

    public String getName() {
        return "filter-" + id + "@thread-" + Thread.currentThread().getId() ;
    }

    /**
     * To string Sql expression
     *
     * @return
     */
    public String toSql() {
        stopSQLInjection(key, value);

        StringBuilder filterSql = new StringBuilder();
        switch (operator) {
            case EQUAL_TO:
                filterSql.append(key).append(" = '").append(value).append("'");
                break;
            case NOT_EQUAL_TO:
                filterSql.append(key).append(" != '").append(value).append("'");
                break;
            case GREATER_THAN:
                filterSql.append(key).append(" > '").append(value).append("'");
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                filterSql.append(key).append(" >= '").append(value).append("'");
                break;
            case LESS_THAN:
                filterSql.append(key).append(" < '").append(value).append("'");
                break;
            case LESS_THAN_OR_EQUAL_TO:
                filterSql.append(key).append(" <= '").append(value).append("'");
                break;
            case LIKE:
                filterSql.append(key).append(" LIKE '%").append(value).append("%'");
                break;
            case NOT_LIKE:
                filterSql.append(key).append(" NOT LIKE '%").append(value).append("%'");
                break;
            case LEFT_LIKE:
                filterSql.append(key).append(" LIKE '%").append(value).append("'");
                break;
            case NOT_LEFT_LIKE:
                filterSql.append(key).append(" NOT LIKE '%").append(value).append("'");
                break;
            case RIGHT_LIKE:
                filterSql.append(key).append(" LIKE '").append(value).append("%'");
                break;
            case NOT_RIGHT_LIKE:
                filterSql.append(key).append(" NOT LIKE '").append(value).append("%'");
                break;
            case BETWEEN_AND:
                if(value instanceof List) {
                    filterSql.append(key).append(" BETWEEN '").append(((List) value).get(0)).append("' AND '").append(((List) value).get(1)).append("'");
                } else {
                    throw new RuntimeException("#SQL Error - BETWEEN x AND y: java.lang.List type of value was expected");
                }
                break;
            case IS_NULL:
                filterSql.append(key).append(" IS NULL");
                break;
            case IS_NOT_NULL:
                filterSql.append(key).append(" IS NOT NULL");
                break;
            case IN:
                if(value instanceof List) {
                    filterSql.append(key).append(" IN ").append(combineValues((List) value));
                } else if (value instanceof String) {
                    filterSql.append(key).append(" IN ").append(splitValues((String) value));
                } else {
                    filterSql.append(key).append(" IN ('").append(value).append("')");
                }
                break;
            case NOT_IN:
                if(value instanceof List) {
                    filterSql.append(key).append(" NOT IN ").append(combineValues((List) value));
                } else if (value instanceof String) {
                    filterSql.append(key).append(" NOT IN ").append(splitValues((String) value));
                } else {
                    filterSql.append(key).append(" NOT IN ('").append(value).append("')");
                }
                break;
            default:
                throw new RuntimeException("#SQL Error - Operator not supported");
        }

        return filterSql.toString();
    }

    /**
     * Split values and re-combine them into a string
     *
     * @param values
     * @return
     */
    private String splitValues(String values) {
        return combineValues(Arrays.asList(values.split(",")));
    }

    /**
     * Combining values into a string
     *
     * @param values
     * @return
     */
    private String combineValues(List values) {
        StringBuilder combinedValues = new StringBuilder();

        values.forEach(v -> combinedValues.append("'").append(v).append("', "));

        if(combinedValues.length() > 0) {
            combinedValues.delete(combinedValues.length() - 2, combinedValues.length());
        }

        return "(" + combinedValues.toString() + ")";
    }

    /**
     * Dangerous keywords may cause SQL injection attack
     */
    private static final String[] DANGEROUS_WORDS = {"(", ")", "*", "%", ";"};

    /**
     * Check Sql injection
     *
     * @param key
     * @param value
     */
    private void stopSQLInjection(String key, Object value) {
        boolean dangerousFound = false;

        for(String word : DANGEROUS_WORDS) {
            if(ObjectUtil.notBlank(key)) {
                if(-1 != key.toUpperCase().indexOf(word)) {
                    dangerousFound = true;
                    break;
                }
            }

            if(ObjectUtil.notNull(value)) {
                if(findPieceInTheCake(word, value)) {
                    dangerousFound = true;
                    break;
                }

                // Check dangerous collection type of value
                if(value instanceof List && ObjectUtil.notEmpty((List) value)) {
                    List values = (List) value;
                    for(Object v : values) {
                        if(findPieceInTheCake(word, v)) {
                            dangerousFound = true;
                            break;
                        }
                    }
                }
            }
        }

        if(dangerousFound) {
            throw new RuntimeException("#Filter - Caution! Caution! Caution! something bad and dangerous found in SQL: " + key + " " + operator + " " + value);
        }
    }

    /**
     * Can you find the piece in the cake?
     *
     * @param piece
     * @param cake
     * @return
     */
    private boolean findPieceInTheCake(String piece, Object cake) {

        return cake instanceof String
                && ObjectUtil.notBlank((String) cake)
                && -1 != ((String) cake).toUpperCase().indexOf(piece);
    }

    /**
     * Clear resource
     */
    public static void clear() {
        ID_GENERATOR.remove();
    }
}
