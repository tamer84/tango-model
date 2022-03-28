package com.tamer84.tango.model.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections4.SetUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MergeUtils {

    // Custom class to support ignored fields and skipping null values
    private static class MergeUtilsBean extends BeanUtilsBean {

        private final Set<String> ignoredFields;

        public MergeUtilsBean(Set<String> ignoredFields) {
            this.ignoredFields = SetUtils.emptyIfNull(ignoredFields);
        }

        @Override
        public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if(value==null || ignoredFields.contains(name)) return;
            super.copyProperty(dest, name, value);
        }
    }
    /**
     * Set all non-null fields of source object into the target object. <br>
     * Null fields were ignored
     *
     * NOTE: This is a shallow merge!!!
     *
     * @param source - source object
     * @param target - target object
     */
    public static void shallowMerge(Object source, Object target) {
        if(source == null || target == null || Objects.equals(source, target)) return;
        shallowMerge(source, target, Collections.emptySet());
    }

    /**
     * Set all non-null fields of source object into the target object. <br>
     * Null fields were ignored
     *
     * NOTE: This is a shallow merge!!!
     *
     * @param source - source object
     * @param target - target object
     * @param ignoredFields - field names, which are ignored
     */
    public static void shallowMerge(Object source, Object target, Set<String> ignoredFields) {

        try {
            if(source == null || target == null || Objects.equals(source, target)) return;
            new MergeUtilsBean(ignoredFields).copyProperties(target, source);
        }
        catch(IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException("Failed to merge objects", e);
        }
    }

    /**
     * Compares if two containers have changed, in case the new container is null it returns false
     */
    public static boolean isValueChanged(final Object newValue, final Object oldValue) {
        return Optional.ofNullable(newValue).map(o -> !o.equals(oldValue)).orElse(false);
    }

}
