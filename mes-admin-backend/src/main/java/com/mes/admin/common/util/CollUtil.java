package com.mes.admin.common.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * <p>
 * 功能包括：
 * - 集合判空
 * - 列表操作（交集、并集、差集）
 * - 列表去重
 * - 列表分页
 * - 列表转 Map
 * - 列表分组
 * </p>
 *
 * @author MES Admin
 */
public class CollUtil {

    // ==================== 判空方法 ====================

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    // ==================== 创建集合 ====================

    /**
     * 创建空列表
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        if (elements == null || elements.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * 创建单一元素列表
     */
    public static <T> List<T> singletonList(T element) {
        return Collections.singletonList(element);
    }

    /**
     * 创建空列表
     */
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    /**
     * 创建空 Map
     */
    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    /**
     * 创建空 Set
     */
    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    /**
     * 如果集合为空返回默认集合
     */
    public static <T> List<T> defaultIfEmpty(List<T> list, List<T> defaultList) {
        return isEmpty(list) ? defaultList : list;
    }

    /**
     * 如果集合为空返回默认集合
     */
    public static <T> Set<T> defaultIfEmpty(Set<T> set, Set<T> defaultSet) {
        return isEmpty(set) ? defaultSet : set;
    }

    // ==================== 列表操作 ====================

    /**
     * 获取列表第一个元素
     */
    public static <T> T getFirst(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    /**
     * 获取列表最后一个元素
     */
    public static <T> T getLast(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    /**
     * 列表分页
     */
    public static <T> List<T> subList(List<T> list, int page, int pageSize) {
        if (isEmpty(list) || page < 1 || pageSize < 1) {
            return new ArrayList<>();
        }
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= list.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 列表去重
     */
    public static <T> List<T> distinct(List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 列表去重（按指定函数）
     */
    public static <T, R> List<T> distinct(List<T> list, Function<T, R> keyExtractor) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<R, Boolean> seen = new LinkedHashMap<>();
        List<T> result = new ArrayList<>();
        for (T item : list) {
            R key = keyExtractor.apply(item);
            if (!seen.containsKey(key)) {
                seen.put(key, true);
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 列表交集
     */
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list1);
        result.retainAll(list2);
        return result;
    }

    /**
     * 列表并集
     */
    public static <T> List<T> union(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(isEmpty(list1) ? new ArrayList<>() : list1);
        if (isNotEmpty(list2)) {
            for (T item : list2) {
                if (!result.contains(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 列表差集（list1 - list2）
     */
    public static <T> List<T> difference(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return new ArrayList<>();
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        List<T> result = new ArrayList<>();
        for (T item : list1) {
            if (!list2.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 列表对称差集
     */
    public static <T> List<T> symmetricDifference(List<T> list1, List<T> list2) {
        List<T> diff1 = difference(list1, list2);
        List<T> diff2 = difference(list2, list1);
        return union(diff1, diff2);
    }

    // ==================== 列表转 Map ====================

    /**
     * 列表转 Map
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Function<T, K> keyExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    /**
     * 列表转 Map（支持重复 key）
     */
    public static <T, K> Map<K, T> toMapMerge(List<T> list, Function<T, K> keyExtractor,
                                               java.util.function.BinaryOperator<T> mergeFunction) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity(), mergeFunction));
    }

    /**
     * 列表转 Map（值也通过函数转换）
     */
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyExtractor,
                                            Function<T, V> valueExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, valueExtractor));
    }

    /**
     * 列表转 LinkedMap
     */
    public static <T, K> Map<K, T> toLinkedMap(List<T> list, Function<T, K> keyExtractor) {
        if (isEmpty(list)) {
            return new LinkedHashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity(),
                (e1, e2) -> e1, new java.util.function.Supplier<LinkedHashMap<K, T>>() {
                    @Override
                    public LinkedHashMap<K, T> get() {
                        return new LinkedHashMap<>();
                    }
                }));
    }

    // ==================== 列表分组 ====================

    /**
     * 列表分组
     */
    public static <T, K> Map<K, List<T>> groupBy(List<T> list, Function<T, K> keyExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.groupingBy(keyExtractor));
    }

    /**
     * 列表分组（保持顺序）
     */
    public static <T, K> Map<K, List<T>> groupByLinked(List<T> list, Function<T, K> keyExtractor) {
        if (isEmpty(list)) {
            return new LinkedHashMap<>();
        }
        return list.stream().collect(Collectors.groupingBy(keyExtractor, LinkedHashMap::new, Collectors.toList()));
    }

    // ==================== 列表过滤 ====================

    /**
     * 列表过滤
     */
    public static <T> List<T> filter(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 列表转换
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 列表转换并过滤 null
     */
    public static <T, R> List<R> mapNotNull(List<T> list, Function<T, R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // ==================== 集合转列表 ====================

    /**
     * Set 转 List
     */
    public static <T> List<T> setToList(Set<T> set) {
        if (isEmpty(set)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(set);
    }

    /**
     * 数组转 List
     */
    @SafeVarargs
    public static <T> List<T> arrayToList(T... array) {
        if (isEmpty(array)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    // ==================== 集合操作 ====================

    /**
     * 合并多个集合
     */
    @SafeVarargs
    public static <T> List<T> merge(Collection<T>... collections) {
        List<T> result = new ArrayList<>();
        for (Collection<T> collection : collections) {
            if (isNotEmpty(collection)) {
                result.addAll(collection);
            }
        }
        return result;
    }

    /**
     * 集合是否包含所有元素
     */
    public static <T> boolean containsAll(Collection<T> coll, Collection<T> items) {
        if (isEmpty(coll) || isEmpty(items)) {
            return false;
        }
        return coll.containsAll(items);
    }

    /**
     * 集合是否有交集
     */
    public static <T> boolean hasIntersection(Collection<T> coll1, Collection<T> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return false;
        }
        for (T item : coll1) {
            if (coll2.contains(item)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 统计方法 ====================

    /**
     * 列表求和
     */
    public static int sumInt(List<Integer> list) {
        if (isEmpty(list)) {
            return 0;
        }
        return list.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 列表求和
     */
    public static long sumLong(List<Long> list) {
        if (isEmpty(list)) {
            return 0L;
        }
        return list.stream().mapToLong(Long::longValue).sum();
    }

    /**
     * 列表求和
     */
    public static double sumDouble(List<Double> list) {
        if (isEmpty(list)) {
            return 0.0;
        }
        return list.stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * 列表平均值
     */
    public static double average(List<Integer> list) {
        if (isEmpty(list)) {
            return 0.0;
        }
        return list.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // ==================== 其他实用方法 ====================

    /**
     * 列表反转
     */
    public static <T> List<T> reverse(List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }

    /**
     * 列表排序
     */
    public static <T> List<T> sort(List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list);
        Collections.sort(result);
        return result;
    }

    /**
     * 列表排序（降序）
     */
    public static <T> List<T> sortDesc(List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list);
        result.sort(Collections.reverseOrder());
        return result;
    }

    /**
     * 列表排序（自定义比较器）
     */
    public static <T> List<T> sort(List<T> list, Comparator<T> comparator) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list);
        result.sort(comparator);
        return result;
    }

    /**
     * 获取元素在列表中的索引（第一个匹配）
     */
    public static <T> int indexOf(List<T> list, T element) {
        if (isEmpty(list)) {
            return -1;
        }
        return list.indexOf(element);
    }

    /**
     * 获取元素在列表中的索引（最后一个匹配）
     */
    public static <T> int lastIndexOf(List<T> list, T element) {
        if (isEmpty(list)) {
            return -1;
        }
        return list.lastIndexOf(element);
    }

    /**
     * 列表是否包含匹配元素
     */
    public static <T> boolean contains(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    /**
     * 查找第一个匹配元素
     */
    public static <T> T find(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        return list.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * 统计匹配元素数量
     */
    public static <T> long count(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return 0;
        }
        return list.stream().filter(predicate).count();
    }

    /**
     * 判断列表是否所有元素都匹配
     */
    public static <T> boolean allMatch(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return true;
        }
        return list.stream().allMatch(predicate);
    }

    /**
     * 判断列表是否没有任何元素匹配
     */
    public static <T> boolean noneMatch(List<T> list, java.util.function.Predicate<T> predicate) {
        if (isEmpty(list)) {
            return true;
        }
        return list.stream().noneMatch(predicate);
    }
}
