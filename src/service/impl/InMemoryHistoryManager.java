package service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Task;
import service.HistoryManager;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Long, Node<Task>> historyMap = new HashMap<>();

    private static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private final T data;

        private Node(T data) {
            this.data = data;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());

        Node<Task> node = new Node<>(task);
        if (null == head) {
            head = node;
        }

        if (null != tail) {
            node.prev = tail;
            tail.next = node;
        }

        tail = node;

        historyMap.put(task.getId(), node);
    }

    public void remove(Long id) {
        Node<Task> node = historyMap.get(id);
        if (null == node) {
            return;
        }

        historyMap.remove(id);

        if (null != node.prev && null != node.next) {
            node.prev.next = node.next;
            node.next.prev = node.prev;

            return;
        }

        if (null == node.prev) {
            if (null != node.next) {
                node.next.prev = null;
                head = node.next;
            } else {
                head = null;
            }
        }

        if (null == node.next) {
            if (null != node.prev) {
                node.prev.next = null;
                tail = node.prev;
            } else {
                tail = null;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();

        Node<Task> node = head;
        while (null != node) {
            history.add(node.data);

            node = node.next;
        }

        return history;
    }

}
