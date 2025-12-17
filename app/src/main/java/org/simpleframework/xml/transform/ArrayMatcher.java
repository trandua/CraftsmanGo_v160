package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class ArrayMatcher implements Matcher {
    private final Matcher primary;

    public ArrayMatcher(Matcher primary) {
        this.primary = primary;
    }

    @Override // org.simpleframework.xml.transform.Matcher
    public Transform match(Class type) throws Exception {
        Class entry = type.getComponentType();
        if (entry == Character.TYPE) {
            return new CharacterArrayTransform(entry);
        }
        if (entry == Character.class) {
            return new CharacterArrayTransform(entry);
        }
        if (entry == String.class) {
            return new StringArrayTransform();
        }
        return matchArray(entry);
    }

    private Transform matchArray(Class entry) throws Exception {
        Transform transform = this.primary.match(entry);
        if (transform == null) {
            return null;
        }
        return new ArrayTransform(transform, entry);
    }
}
