package org.simpleframework.xml.stream;

/* loaded from: classes.dex */
class InputStack extends Stack<InputNode> {
    public InputStack() {
        super(6);
    }

    public boolean isRelevant(InputNode value) {
        return contains(value) || isEmpty();
    }
}
