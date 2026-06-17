package com.mes.admin.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

public class PageUtil {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResult<T> {
        private List<T> content;
        private long total;
        private int page;
        private int pageSize;
    }

    public static <T> PageResult<T> toPage(List<T> list, int page, int pageSize) {
        PageResult<T> result = new PageResult<>();
        if (list == null || list.isEmpty()) {
            result.setContent(Collections.emptyList());
            result.setTotal(0L);
            result.setPage(page);
            result.setPageSize(pageSize);
            return result;
        }
        int total = list.size();
        int safePage = page <= 0 ? 1 : page;
        int safePageSize = pageSize <= 0 ? 10 : pageSize;
        int fromIndex = (safePage - 1) * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, total);
        if (fromIndex > total) {
            result.setContent(Collections.emptyList());
        } else {
            result.setContent(list.subList(fromIndex, toIndex));
        }
        result.setTotal(total);
        result.setPage(safePage);
        result.setPageSize(safePageSize);
        return result;
    }
}
