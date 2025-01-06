package service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Node;
import model.Task;
import service.HistoryManager;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Long, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        remove(task.getId());

        Node<Task> node = new Node<>(task);
        if (null == head) {
            head = node;
        }

        if (null != tail) {
            node.setPrev(tail);
            tail.setNext(node);
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

        if (null != node.getPrev() && null != node.getNext()) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());

            return;
        }

        if (null == node.getPrev()) {
            if (null != node.getNext()) {
                node.getNext().setPrev(null);
                head = node.getNext();
            } else {
                head = null;
            }
        }

        if (null == node.getNext()) {
            if (null != node.getPrev()) {
                node.getPrev().setNext(null);
                tail = node.getPrev();
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
            history.add(node.getData());

            node = node.getNext();
        }

        return history;
    }

}
