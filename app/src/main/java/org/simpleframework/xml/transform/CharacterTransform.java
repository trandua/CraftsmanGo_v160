package org.simpleframework.xml.transform;

/* loaded from: classes.dex */
class CharacterTransform implements Transform<Character> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.simpleframework.xml.transform.Transform
    public Character read(String value) throws Exception {
        if (value.length() == 1) {
            return Character.valueOf(value.charAt(0));
        }
        throw new InvalidFormatException("Cannot convert '%s' to a character", value);
    }

    public String write(Character value) throws Exception {
        return value.toString();
    }
}
