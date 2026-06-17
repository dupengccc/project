package com.mes.admin.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

public class TreeUtil {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreeNode<T> {
        private Long id;
        private Long parentId;
        private List<TreeNode<T>> children = new ArrayList<>();
        private T data;
    }

    public static <T> List<TreeNode<T>> buildTree(List<T> nodes,
                                                  Function<T, Long> idGetter,
                                                  Function<T, Long> parentIdGetter,
                                                  Long rootParentId) {
        if (nodes == null || nodes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, TreeNode<T>> nodeMap = new LinkedHashMap<>();
        for (T item : nodes) {
            TreeNode<T> treeNode = new TreeNode<>();
            treeNode.setId(idGetter.apply(item));
            treeNode.setParentId(parentIdGetter.apply(item));
            treeNode.setData(item);
            nodeMap.put(treeNode.getId(), treeNode);
        }

        List<TreeNode<T>> roots = new ArrayList<>();
        for (TreeNode<T> node : nodeMap.values()) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId.equals(rootParentId) || !nodeMap.containsKey(parentId)) {
                roots.add(node);
            } else {
                TreeNode<T> parent = nodeMap.get(parentId);
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    public static <T> List<TreeNode<T>> flattenTree(List<TreeNode<T>> tree) {
        List<TreeNode<T>> result = new ArrayList<>();
        if (tree == null || tree.isEmpty()) {
            return result;
        }
        Queue<TreeNode<T>> queue = new LinkedList<>(tree);
        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.poll();
            result.add(node);
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                queue.addAll(node.getChildren());
            }
        }
        return result;
    }
}
