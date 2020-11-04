package com.zerohuang.subway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.*;

/**
 * @author ZeroHuang
 * @date 2020/10/26 11:05 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyArrayList<T> implements Iterable<T>{
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    private static final Object[] EMPTY_ELEMENTDATA = {};

    private static final int DEFAULT_CAPACITY = 10;

    protected transient int modCount = 0;

    /**
     * 数组
     */
    private T[] arrayList;

    /**
     * 实际的数组大小
     */
    private int size;

    /**
     * 定义的容量大小，默认为10
     */
    private int capacity = DEFAULT_CAPACITY;

    public MyArrayList() {
        arrayList = (T[]) new Object[capacity];
    }

    public MyArrayList(int capacity){
        if(capacity <= 0) {
            throw new RuntimeException("必须大于0");
        }
        this.capacity = capacity;
        arrayList = (T[]) new Object[capacity];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    /**
     * 线性表扩充
     */
    public void extendsArrayList() {
        if(size >= capacity) {
            T[] newArrayList = (T[]) new Object[size * 2 + 1];
            this.capacity = size * 2 + 1;
            for(int i=0; i<size; i++) {
                newArrayList[i] = arrayList[i];
            }
            arrayList = newArrayList;
        }
    }

    /**
     * 添加元素
     * @param obj
     * @return
     */
    public boolean add(T obj) {
        extendsArrayList();
        arrayList[size] = obj;
        size++;
        return true;
    }

    /**
     * 指定位置添加元素
     * @param index
     * @param obj
     * @return
     */
    public boolean add(int index,T obj) {
        extendsArrayList();

        if(index < size && index >=0) {
            for(int i=size; i>=index; i--) {
                arrayList[i+1] = arrayList[i];
            }
            arrayList[index] = obj;
            size++;
            return true;
        } else if(index == size){
            add(obj);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除指定位置元素
     * @param index
     * @return
     */
    public boolean remove(int index) {
        if(index < size) {
            for(int i=index; i<size -1; i++) {
                arrayList[i] = arrayList[i+1];
            }
            arrayList[size] = null;
            size--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除元素
     * @param obj
     * @return
     */
    public boolean remove(T obj) {
        for(int i=0; i<size; i++) {
            if(arrayList[i].equals(obj)) {
                remove(i);
                break;
            }
        }

        return false;
    }

    /**
     * 修改指定位置元素
     * @param index
     * @param obj
     * @return
     */
    public boolean set(int index, T obj) {
        if(index < size) {
            arrayList[index] = obj;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取指定位置元素
     * @param index
     * @return
     */
    public T get(int index) {
        if(index <size && index >=0) {
            return (T) arrayList[index];
        } else {
            throw new RuntimeException("数组越界 size:" + size + "index:" + index);
        }
    }

    /**
     * 返回指定元素位置
     * @param obj
     * @return
     */
    public int indexOf(T obj) {

        for(int i=0; i<size; i++) {
            if(obj.equals(arrayList[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 加入另一个集合
     * @param list
     * @return
     */
    public boolean addAll(MyArrayList<T> list){
        Object[] a = list.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, arrayList, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(arrayList, minCapacity));
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - arrayList.length > 0)
            grow(minCapacity);
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = arrayList.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        arrayList = Arrays.copyOf(arrayList, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * 返回数组
     * @return
     */
    public T[] toArray() {
        return arrayList;
    }

    /**
     * 查看是否包含元素
     * @param obj
     * @return
     */
    public boolean contains(T obj) {
        for(int i=0; i<size; i++) {
            if(arrayList[i].equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 释放空间
     */
    public void clear(){
        modCount++;
        // clear to let GC do its work
        for (int i = 0; i < size; i++) {
            arrayList[i] = null;
        }
        size = 0;
        capacity = 10;
    }

    public void trimToSize() {
        modCount++;
        if (size < arrayList.length) {
            arrayList = (size == 0) ? (T[]) EMPTY_ELEMENTDATA : Arrays.copyOf(arrayList, size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<T> {
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return get(current++);
        }

        public void remove() {
            MyArrayList.this.remove(--current);
        }
    }
}
