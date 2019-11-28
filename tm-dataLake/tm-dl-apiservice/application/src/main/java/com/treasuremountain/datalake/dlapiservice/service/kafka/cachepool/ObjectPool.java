package com.treasuremountain.datalake.dlapiservice.service.kafka.cachepool;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @version 1.0
 * @program: dlapiservice->ObjectPool
 * @description: ObjectPool
 * @author: Axin
 * @create: 2019-11-16 16:40
 **/
public abstract class ObjectPool<T> {
    protected Vector<PooledObject<T>> objects = null; // 存放对象池中对象的向量(PooledObject类型)

    public ObjectPool() {
    }

    /*** 创建一个对象池 ***/
    public synchronized void createPool(int num) {
        // 确保对象池没有创建。如果创建了，保存对象的向量 objects 不会为空
        if (objects != null) {
            return; // 如果己经创建，则返回
        }
        // 创建保存对象的向量 , 初始时有 0 个元素
        objects = new Vector<PooledObject<T>>();
        for (int i = 0; i < num; i++) {
            objects.addElement(create());
        }
    }

    public abstract PooledObject<T> create();

    public synchronized T getObject() {
        // 确保对象池己被创建
        if (objects == null) {
            return null; // 对象池还没创建，则返回 null
        }
        T t = getFreeObject(); // 获得一个可用的对象
        // 如果目前没有可以使用的对象，即所有的对象都在使用中
        while (t == null) {
            wait(250);
            t = getFreeObject(); // 重新再试，直到获得可用的对象，如果
            // getFreeObject() 返回的为 null，则表明创建一批对象后也不可获得可用对象
        }
        return t;// 返回获得的可用的对象
    }

    /**
     * 本函数从对象池对象 objects 中返回一个可用的的对象，如果 当前没有可用的对象，则创建几个对象，并放入对象池中。
     * 如果创建后，所有的对象都在使用中，则返回 null
     */
    private T getFreeObject() {
        // 从对象池中获得一个可用的对象
        T obj = findFreeObject();
        if (obj == null) {
            return null;
        }
        return obj;
    }

    /**
     * 查找对象池中所有的对象，查找一个可用的对象， 如果没有可用的对象，返回 null
     */
    private T findFreeObject() {
        T obj = null;
        PooledObject<T> pObj = null;
        // 获得对象池向量中所有的对象
        Enumeration<PooledObject<T>> enumerate = objects.elements();
        // 遍历所有的对象，看是否有可用的对象
        while (enumerate.hasMoreElements()) {
            pObj = (PooledObject<T>) enumerate.nextElement();

            // 如果此对象不忙，则获得它的对象并把它设为忙
            if (!pObj.isBusy()) {
                obj = pObj.getObject();
                pObj.setBusy(true);
            }
        }
        return obj;// 返回找到到的可用对象
    }

    /**
     * 此函数返回一个对象到对象池中，并把此对象置为空闲。 所有使用对象池获得的对象均应在不使用此对象时返回它。
     */

    public void returnObject(T obj) {
        // 确保对象池存在，如果对象没有创建（不存在），直接返回
        if (objects == null) {
            return;
        }
        PooledObject<T> pObj = null;
        Enumeration<PooledObject<T>> enumerate = objects.elements();
        // 遍历对象池中的所有对象，找到这个要返回的对象对象
        while (enumerate.hasMoreElements()) {
            pObj = (PooledObject<T>) enumerate.nextElement();
            // 先找到对象池中的要返回的对象对象
            if (obj == pObj.getObject()) {
                // 找到了 , 设置此对象为空闲状态
                pObj.setBusy(false);
                break;
            }
        }
    }

    /**
     * 关闭对象池中所有的对象，并清空对象池。
     */
    public synchronized void closeObjectPool() {
        // 确保对象池存在，如果不存在，返回
        if (objects == null) {
            return;
        }
        PooledObject<T> pObj = null;
        Enumeration<PooledObject<T>> enumerate = objects.elements();
        while (enumerate.hasMoreElements()) {
            pObj = (PooledObject<T>) enumerate.nextElement();
            // 如果忙，等 0.5 秒
            if (pObj.isBusy()) {
                wait(500); // 等
            }
            // 从对象池向量中删除它
            objects.removeElement(pObj);
        }
        // 置对象池为空
        objects = null;
    }

    /**
     * 使程序等待给定的毫秒数
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
        }
    }
}