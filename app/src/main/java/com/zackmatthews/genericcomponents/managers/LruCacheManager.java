package com.zackmatthews.genericcomponents.managers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by zackmatthews on 11/3/17.
 */

public class LruCacheManager {
    private static final int MAX_CACHE = 20;
    private HashMap<String, File> map = new HashMap<>();
    private LinkedList<File> cache = new LinkedList<>();

    public static LruCacheManager getInstance() {
        if(instance == null){
            instance = new LruCacheManager();
        }
        return instance;
    }

    private static LruCacheManager instance;

    public void put(String key, File obj){
        if(cache.contains(obj)){
            cache.remove(obj);
            cache.push(obj);
            map.put(key, obj);
            return;
        }

        cache.push(obj);
        map.put(key, cache.peekFirst());

        if(cache.size() > MAX_CACHE){
            cache.removeLast();
            map.remove(key);
        }
    }

    public File get(String key){
        return map.get(key);
    }

}
