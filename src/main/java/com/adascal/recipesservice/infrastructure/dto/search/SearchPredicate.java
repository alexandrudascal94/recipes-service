package com.adascal.recipesservice.infrastructure.dto.search;

import com.adascal.recipesservice.domain.exception.ErrorRule;
import com.adascal.recipesservice.domain.exception.RecipeServiceException;
import org.springframework.data.mongodb.core.query.Criteria;

public enum SearchPredicate {
    EQUAL {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            if (isBoolean(value)) {
                return criteria.is(Boolean.valueOf(value));
            } else if (isInteger(value)) {
                return criteria.is(Integer.valueOf(value));
            }
            return criteria.is(value);
        }
    },
    NOT_EQUAL {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            if (isBoolean(value)) {
                return criteria.ne(Boolean.valueOf(value));
            } else if (isInteger(value)) {
                return criteria.ne(Integer.valueOf(value));
            }
            return criteria.ne(value);
        }
    },
    GREATER {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            try {
                var number = Integer.parseInt(value);
                return criteria.gt(number);
            } catch (Exception e) {
                throw new RecipeServiceException(ErrorRule.INVALID_INPUT,
                        "GREATER predicate accept only numbers");
            }
        }
    },
    LESS {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            try {
                var number = Integer.parseInt(value);
                return criteria.lt(number);
            } catch (Exception e) {
                throw new RecipeServiceException(ErrorRule.INVALID_INPUT,
                        "LESS predicate accept only numbers");
            }
        }
    },
    INCLUDE {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            return criteria.elemMatch(Criteria.where("name").is(value));
        }
    },
    EXCLUDE {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            return criteria.elemMatch(Criteria.where("name").ne(value));
        }
    },
    CONTAINS_TEXT {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            return criteria.regex(value);
        }
    },
    NOT_CONTAINS_TEXT {
        @Override
        public Criteria mapCriteria(Criteria criteria, String value) {
            return criteria.not().regex(value);
        }
    };

    public boolean isBoolean(String value) {
        return "true".equals(value) || "false".equals(value);
    }

    public boolean isInteger(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public abstract Criteria mapCriteria(Criteria criteria, String value);
}
