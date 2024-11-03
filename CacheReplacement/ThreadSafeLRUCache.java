/*
 * Code Optimization and refactoring done with the help of ChatGPT
 */
package CacheReplacement;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeLRUCache implements Cache {
    private final int capacity;
    private final HashMap<Integer, Node> cache;
    private final DoublyLinkedList list;
    private final ReentrantLock lock = new ReentrantLock();

    public ThreadSafeLRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.list = new DoublyLinkedList();
    }

    private class Node {
        Integer key;
        Integer value;
        Node next;
        Node previous;

        Node(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private class DoublyLinkedList {
        private final Node head;
        private final Node tail;

        DoublyLinkedList() {
            head = new Node(null, null);
            tail = new Node(null, null);
            head.next = tail;
            tail.previous = head;
        }

        void insertAtFront(Node node) {
            node.previous = head;
            node.next = head.next;
            head.next.previous = node;
            head.next = node;
        }

        void remove(Node node) {
            Node left = node.previous;
            Node right = node.next;
            left.next = right;
            right.previous = left;
        }

        Node removeLast() {
            Node lruNode = tail.previous;
            if (lruNode == head) {
                return null; // List is empty
            }
            remove(lruNode);
            return lruNode;
        }
    }

    @Override
    public void put(Object key, Object value) {
        lock.lock();
        try {
            Integer intKey = (Integer) key;
            Integer intValue = (Integer) value;

            if (cache.containsKey(intKey)) {
                Node node = cache.get(intKey);
                node.value = intValue; // Update the value
                list.remove(node); // Remove and re-insert to update order
                list.insertAtFront(node);
            } else {
                if (cache.size() >= capacity) {
                    Node lruNode = list.removeLast();
                    if (lruNode != null) {
                        cache.remove(lruNode.key); // Remove from cache as well
                    }
                }
                Node newNode = new Node(intKey, intValue);
                list.insertAtFront(newNode);
                cache.put(intKey, newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get(Object key) {
        lock.lock();
        try {
            Integer intKey = (Integer) key;

            if (!cache.containsKey(intKey)) {
                return -1;
            }

            Node node = cache.get(intKey);
            list.remove(node);
            list.insertAtFront(node); // Move the accessed node to the front
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void evict() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evict'");
    }
}
