package com.example.ahmet.securemailclient.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DummyContent {


    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    /*static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }*/

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.subject, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, "Content"+position);
    }


    public static class DummyItem {
        public final String fromWho;
        public final String subject;
        public final String content;

        public DummyItem(String fromWho, String subject, String content) {
            this.fromWho = fromWho;
            this.content = content;
            this.subject = subject;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
