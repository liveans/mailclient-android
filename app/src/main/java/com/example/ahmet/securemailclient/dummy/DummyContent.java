package com.example.ahmet.securemailclient.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;


public class DummyContent {


    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.subject, item);
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
            return content.toString();
        }
    }
}
