package com.microsoft.xbox.toolkit.ui.Search;

import com.microsoft.xbox.toolkit.JavaUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/* loaded from: classes3.dex */
public class TrieSearch {
    private static String ComponentName = "com.microsoft.xbox.toolkit.ui.Search.TrieSearch";
    private static int DefaultTrieDepth = 4;
    public TrieNode RootTrieNode;
    public int TrieDepth;
    public Hashtable<String, List<Object>> WordsDictionary;

    public TrieSearch() {
        this.WordsDictionary = new Hashtable<>();
        this.RootTrieNode = new TrieNode();
        this.TrieDepth = DefaultTrieDepth;
    }

    public TrieSearch(int i) {
        this.WordsDictionary = new Hashtable<>();
        this.RootTrieNode = new TrieNode();
        this.TrieDepth = i;
    }

    public static int findWordIndex(String str, String str2) {
        if (JavaUtil.isNullOrEmpty(str) || JavaUtil.isNullOrEmpty(str2)) {
            return -1;
        }
        int indexOf = str.toLowerCase().indexOf(str2.toLowerCase());
        while (indexOf != -1 && indexOf != 0 && !isNullOrWhitespace(str.substring(indexOf - 1, indexOf))) {
            indexOf = str.toLowerCase().indexOf(str2.toLowerCase(), indexOf + 1);
        }
        return indexOf;
    }

    public static List<String> getRemainingWordMatches(TrieNode trieNode, int i, String str) {
        ArrayList arrayList = new ArrayList();
        if (trieNode != null && !JavaUtil.isNullOrEmpty(str)) {
            if (trieNode.IsWord && str.length() <= i) {
                arrayList.add(str);
            }
            if (trieNode.MoreNodes != null) {
                Enumeration<Character> keys = trieNode.MoreNodes.keys();
                while (keys.hasMoreElements()) {
                    char charValue = keys.nextElement().charValue();
                    arrayList.addAll(getRemainingWordMatches(trieNode.MoreNodes.get(Character.valueOf(charValue)), i, str + charValue));
                }
            }
            if (trieNode.Words != null) {
                for (String str2 : trieNode.Words) {
                    if (str2.toLowerCase().startsWith(str.toLowerCase())) {
                        arrayList.add(str2);
                    }
                }
            }
        }
        return arrayList;
    }

    public static TrieNode getTrieNodes(Hashtable<String, List<Object>> hashtable, int i) {
        if (hashtable == null) {
            return null;
        }
        TrieNode trieNode = new TrieNode();
        Enumeration<String> keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String nextElement = keys.nextElement();
            int i2 = 0;
            TrieNode trieNode2 = trieNode;
            while (i2 < nextElement.length() && i2 <= i) {
                char charAt = nextElement.charAt(i2);
                if (trieNode2.MoreNodes == null) {
                    trieNode2.MoreNodes = new Hashtable<>(26);
                }
                if (!trieNode2.MoreNodes.containsKey(Character.valueOf(charAt))) {
                    trieNode2.MoreNodes.put(Character.valueOf(charAt), new TrieNode());
                }
                trieNode2 = trieNode2.MoreNodes.get(Character.valueOf(charAt));
                i2++;
            }
            if (i2 > i) {
                if (trieNode2.Words == null) {
                    trieNode2.Words = new ArrayList();
                }
                trieNode2.Words.add(nextElement);
            }
            if (i2 == nextElement.length()) {
                trieNode2.IsWord = true;
            }
        }
        return trieNode;
    }

    public static List<String> getWordMatches(TrieNode trieNode, int i, String str) {
        ArrayList arrayList = new ArrayList();
        if (JavaUtil.isNullOrEmpty(str)) {
            return arrayList;
        }
        String upperCase = str.toUpperCase();
        int i2 = 0;
        String str2 = "";
        boolean z = false;
        while (true) {
            if (i2 >= upperCase.length() || i2 > i) {
                z = true;
            } else {
                char charAt = upperCase.charAt(i2);
                str2 = str2 + charAt;
                if (trieNode.MoreNodes == null || !trieNode.MoreNodes.containsKey(Character.valueOf(charAt))) {
                    break;
                }
                trieNode = trieNode.MoreNodes.get(Character.valueOf(charAt));
                i2++;
            }
        }
        if (i2 > i) {
            if (trieNode.Words != null) {
                for (String str3 : trieNode.Words) {
                    if (str3.toLowerCase().startsWith(str.toLowerCase())) {
                        arrayList.add(str3);
                    }
                }
            }
        } else if (z) {
            arrayList.addAll(getRemainingWordMatches(trieNode, i, str2));
        }
        return arrayList;
    }

    public static Hashtable<String, List<Object>> getWordsDictionary(List<TrieInput> list) {
        Hashtable<String, List<Object>> hashtable = new Hashtable<>();
        if (list == null) {
            return hashtable;
        }
        for (TrieInput trieInput : list) {
            for (String str : JavaUtil.isNullOrEmpty(trieInput.Text) ? new String[0] : trieInput.Text.split(" ")) {
                int findWordIndex = findWordIndex(trieInput.Text, str);
                if (findWordIndex != -1) {
                    String upperCase = trieInput.Text.substring(findWordIndex).toUpperCase();
                    if (!hashtable.containsKey(upperCase)) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(trieInput.Context);
                        hashtable.put(upperCase, arrayList);
                    } else if (!hashtable.get(upperCase).contains(trieInput.Context)) {
                        hashtable.get(upperCase).add(trieInput.Context);
                    }
                }
            }
        }
        return hashtable;
    }

    private static boolean isNullOrWhitespace(String str) {
        return JavaUtil.isNullOrEmpty(str) || str.trim().isEmpty();
    }

    public void initialize(List<TrieInput> list) {
        Hashtable<String, List<Object>> wordsDictionary = getWordsDictionary(list);
        this.WordsDictionary = wordsDictionary;
        this.RootTrieNode = getTrieNodes(wordsDictionary, this.TrieDepth);
    }

    public List<String> search(String str) {
        return getWordMatches(this.RootTrieNode, this.TrieDepth, str);
    }
}
