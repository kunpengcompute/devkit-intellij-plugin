/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.kunpeng.devkit.listen;

import com.intellij.ide.plugins.PluginNode;
import com.intellij.openapi.extensions.PluginId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * ListenerManager
 *
 * @since 2021-08-25
 */
public class ListenerManager {
    private static final Map<String, List<? extends ListenerManager
            .BaseListener>> LISTENER_MAP = new ConcurrentHashMap(0);

    static {
        registerListenerType(ListenerManager.PluginEnableStatusChange.class.getName());
        registerListenerType(ListenerManager.PluginActionDone.class.getName());
        registerListenerType(ListenerManager.HostChange.class.getName());
        registerListenerType(ListenerManager.PluginChange.class.getName());
    }

    /**
     * ListenerManager
     */
    public ListenerManager() {
    }

    /**
     * registerListenerType registerListenerType
     *
     * @param className class name.
     */
    public static void registerListenerType(String className) {
        if (!LISTENER_MAP.containsKey(className)) {
            LISTENER_MAP.put(className, new ArrayList(0));
        }
    }

    /**
     * registerListener
     *
     * @param t1  t1
     * @param <T> <T>
     */
    public static <T extends ListenerManager.BaseListener> void registerListener(T t1) {
        Stream.of(t1.getClass().getInterfaces())
                .map(Class::getName).forEach(className -> registerListener(className, t1));
    }

    /**
     * registerListener
     *
     * @param className className
     * @param t1        t1
     * @param <T>       <T>
     */
    public static <T extends ListenerManager.BaseListener> void registerListener(String className, T t1) {
        List listeners = LISTENER_MAP.get(className);
        if (listeners != null) {
            listeners.add(t1);
        }
    }

    /**
     * trigger
     *
     * @param tClass tClass
     * @param entity entity
     * @param <T>    <T>
     * @param <E>    <E>
     */
    public static <T extends ListenerManager.BaseListener, E> void trigger(Class<T> tClass, E entity) {
        List<? extends ListenerManager.BaseListener> listeners = LISTENER_MAP.get(tClass.getName());
        if (listeners != null && listeners.size() != 0) {
            listeners.forEach(listener -> listener.action(entity));
        }
    }

    /**
     * removeListener
     *
     * @param t1  t1
     * @param <T> <T>
     */
    public static <T extends ListenerManager.BaseListener> void removeListener(T t1) {
        Stream.of(t1.getClass().getInterfaces()).map(Class::getName).forEach(className -> {
            List<? extends ListenerManager.BaseListener> listeners = LISTENER_MAP.get(className);
            if (listeners != null) {
                listeners.remove(t1);
            }
        });
    }

    /**
     * PluginChange
     */
    public interface PluginChange extends ListenerManager.BaseListener<PluginNode> {}

    /**
     * HostChange
     */
    public interface HostChange extends ListenerManager.BaseListener<String> {}

    /**
     * PluginActionDone
     */
    public interface PluginActionDone extends ListenerManager.BaseListener<PluginId> {}

    /**
     * PluginEnableStatusChange
     */
    public interface PluginEnableStatusChange extends ListenerManager.BaseListener<Boolean> {}

    /**
     * BaseListener
     *
     * @param <E> E
     */
    public interface BaseListener<E> {
        void action(E var1);
    }
}