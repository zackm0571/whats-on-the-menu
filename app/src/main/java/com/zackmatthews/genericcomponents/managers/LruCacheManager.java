package com.zackmatthews.genericcomponents.managers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by zackmatthews on 11/3/17.
 */

public class LruCacheManager {
    private static final int MAX_CACHE = 20;
    private HashMap<String, Pair<String, Bitmap>> map = new HashMap<>();
    private LinkedList<Pair<String, Bitmap>> cache = new LinkedList<>();

    public static LruCacheManager getInstance() {
        if(instance == null){
            instance = new LruCacheManager();
        }
        return instance;
    }

    private static LruCacheManager instance;

    public void put(String key, Bitmap obj){
        Pair<String, Bitmap> pair = map.get(key);
        if(cache.contains(pair)){
            cache.remove(pair);
            map.remove(key);
        }

        if(pair == null){
            pair = new Pair<>(key, obj);
        }
        cache.push(pair);
        map.put(key, pair);

        if(cache.size() > MAX_CACHE){
            for(int i = 0; i < MAX_CACHE / 2; i++) {
                Pair<String, Bitmap> node = cache.removeLast();
                map.remove(node.first);
            }
        }
    }

    public Bitmap get(String key){
        Pair<String, Bitmap> node = map.get(key);
        if(node != null) {
            return node.second;
        }
        return null;
    }

}
