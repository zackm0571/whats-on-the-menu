package com.zackmatthews.genericcomponents.managers;

import android.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by zackmatthews on 11/3/17.
 */

public class LruCacheManager {
    private static final int MAX_CACHE = 20;
    private HashMap<String, Pair<String, File>> map = new HashMap<>();
    private LinkedList<Pair<String, File>> cache = new LinkedList<>();

    public static LruCacheManager getInstance() {
        if(instance == null){
            instance = new LruCacheManager();
        }
        return instance;
    }

    private static LruCacheManager instance;

    public void put(String key, File obj){
        if(cache.contains(map.get(key))){
            return;
        }

        cache.push(new Pair<>(key, obj));
        map.put(key, cache.peekFirst());

        if(cache.size() > MAX_CACHE){
            for(int i = 0; i < MAX_CACHE / 2; i++) {
                Pair<String, File> node = cache.removeLast();
                map.remove(node.first);
                node.second.delete();
            }

        }
    }

    public File get(String key){
        Pair<String, File> node = map.get(key);
        if(node != null) {
            return map.get(key).second;
        }
        return null;
    }

}
