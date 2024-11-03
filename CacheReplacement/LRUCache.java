package CacheReplacement;

import java.util.HashMap;

public class LRUCache implements Cache {

    private Integer cacheCapacity;
        private HashMap<Integer, Node> hashMap;
        private Node head;
        private Node tail;
    
        public LRUCache(Integer cacheCapacity) {
            this.cacheCapacity = cacheCapacity;
            
            head = new Node();
            tail = new Node();
            this.head.key = null;
            this.head.value = null;
            this.head.next = this.tail;
            this.head.previous = null;
            this.tail.key = null;
            this.tail.value = null;
            this.tail.next = null;
            this.tail.previous = this.head;
    
            hashMap = new HashMap<>();
        }
    
        private class Node {
            Integer key;
            Integer value;
            Node next;
            Node previous;
        }
    
        private void insertNode(Node node) {
            node.previous = head;
            head.next.previous = node;
            node.next = head.next;
            head.next = node;
    
        }
    
        private void deleteNode(Node node) {
            Node left = node.previous;
            Node right = node.next;
    
            left.next = right;
            right.previous = left;
    
        }
    
        private void reorderNode(Node node) {
    
            deleteNode(node);
            insertNode(node);
    
        }
    
        @Override
        public void put(Object Key, Object Value) {
            if (hashMap.containsKey(Key)) {
                Node tempNode = hashMap.get(Key);
                tempNode.value = (Integer) Value;
                reorderNode(tempNode);
            } else {
    
                Node newNode = new Node();
                newNode.key = (Integer) Key;
                newNode.value = (Integer) Value;
                if (cacheCapacity == 0)
                    evict();
                insertNode(newNode);
                hashMap.put((Integer) Key, newNode);
                cacheCapacity--;
            }
    
        }
    
        @Override
        public Object get(Object key) {
            if (!hashMap.containsKey(key))
                return -1;
            Node node = hashMap.get(key);
            reorderNode(node);
            return node.value;
    
        }
    
        @Override
        public void evict() {
            Node lruNode = tail.previous;
            hashMap.remove(lruNode.key);
            deleteNode(lruNode);
            lruNode = null;
            cacheCapacity++;

    }

}
