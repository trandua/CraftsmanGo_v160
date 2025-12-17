package com.microsoft.xbox.idp.util;

import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class ObjectLoaderCache implements ObjectLoader.Cache {
    private final HashMap<Object, ObjectLoader.Result<?>> map = new HashMap<>();

    @Override // com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache
    public void clear() {
        this.map.clear();
    }

    @Override // com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache
    public <T> ObjectLoader.Result<T> get(Object obj) {
        return (ObjectLoader.Result<T>) this.map.get(obj);
    }

    @Override // com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache
    public <T> ObjectLoader.Result<T> put(Object obj, ObjectLoader.Result<T> result) {
        return (ObjectLoader.Result<T>) this.map.put(obj, result);
    }

    @Override // com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache
    public <T> ObjectLoader.Result<T> remove(Object obj) {
        return (ObjectLoader.Result<T>) this.map.remove(obj);
    }
}
