package design.pattern.behavioral_patterns.iterator;

/**
 * @author liuwei
 * @date 2019-08-01 21:17:51
 * @desc 容器类接口
 * 所有的容器类的内容本质都是一组对象的封装
 * 其实现类可以是任何持有一组对象的类，无论是数组、链表、集合、树、图还是其它类似结构
 * 为遍历数组，需要自持一个具有迭代遍历功能的迭代器(实现自迭代器接口)，并提供给外部访问
 * 外部获取迭代器后即可使用迭代器方法遍历容器内的数组
 * 
 * 实际上，java集合框架中Collection接口就是一个容器接口
 * 该接口下的所有实现类(各种List和Set)，内部本质都是数组对象，且都持有一个迭代器以遍历该数组
 * 
 * Map是独立于Collection的另一个顶层容器接口
 * 所有的map都不持有迭代器，但都继承和实现了Set<K> keySet()和Set<Map.Entry<K, V>> entrySet()方法
 * 首先获取一个Set集合，然后通过Set的迭代器间接实现遍历
 * 
 * 以下列举常见的集合类型内部持有的聚合对象及其迭代遍历算法及使用场景
 * 
 * 1.ArrayList
 * 内部持有的聚合对象：(数组对象)transient Object[] elementData
 * 迭代遍历算法：数组索引递增
 * 迭代使用场景：很少使用，一般直接通过索引值遍历
 * 2.LinkedList
 * 内部持有的聚合对象：(Node<E>组合对象)transient Node<E> first和transient Node<E> last
 * Node对象持有：当前元素值E item;下一个节点对象Node<E> next;上一个节点对象Node<E> prev;
 * 迭代遍历算法：Node对象的单向指针冒泡，可双向遍历
 * 迭代使用场景：总会被用到，虽然一般直接通过索引值遍历，但除了首尾两环，链表是不存储索引对应的指针的
 * 		以正向遍历来说，必须从firstNode开始不断进行链环冒泡，才能找到第n个Node的指针
 * 3.HashSet
 * 内部持有的聚合对象：(HashMap对象)private transient HashMap<E,Object> map
 * 迭代遍历算法：由于聚合的是HashMap，所以遍历的也是HashMap，详参HashMap迭代算法
 * 		敲重点>>>HashSet的迭代器指向HashMap的迭代器，二者完全是一回事
 * 迭代使用场景：详参HashMap迭代使用场景
 * 4.TreeSet
 * 内部持有的聚合对象：(实际为TreeMap对象)private transient NavigableMap<E,Object> m
 * 迭代遍历算法：由于聚合的是TreeMap，所以遍历的也是TreeMap，详参TreeMap迭代算法
 * 		敲重点>>>TreeSet的迭代器指向TreeMap的迭代器，二者完全是一回事
 * 迭代使用场景：详参TreeMap迭代使用场景
 * 5.HashMap
 * 内部持有的聚合对象：(Node<K,V>数组对象)transient Node<K,V>[] table;
 * Node<K,V>对象持有：哈希值final int hash;key值final K key;value值V value;下一个节点Node<K,V> next;
 * 迭代遍历算法：虽然持有的是Node<K,V>数组对象，但并没有提供索引来遍历数组(table为非public属性)
 * 		首先，要获取keySet或entrySet，然后再遍历set集合
 * 		以entrySet遍历为例，其迭代器为HashMap.EntryIterator，next方法返回下一个Node<K,V>
 * 		这种关联节点的遍历和LinkedList是完全一样的
 * 		HashMap的数据结构就像是(去重的单向链表LinkedList)或者说是(有且单方向的哈希集合HashSet)
 * 		实际上，HashSet集合内部持有的聚合对象正是HashMap
 * 迭代使用场景：自身没有迭代器也没有定义直接遍历的方法，必须使用keySet或entrySet遍历
 * 		实际上，HashMap本质上也是数组
 * 		而且既然定义了Node<K,V>[] table数组，本可以对外提供索引来遍历数组的
 * 		JDK没有这样做，应该是出于某些未知原因
 * 6.TreeMap
 * 内部持有的聚合对象：(Entry<K,V>组合对象)private transient Entry<K,V> root;
 * Entry<K,V>对象持有：
 		K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;//黑树标记
 * 迭代遍历算法：详参红黑树算法和JDK源码，具体不详述
 * 迭代使用场景：具体不详述
 */
public interface Container<T> {
	Iterator<T> getIterator();
}
