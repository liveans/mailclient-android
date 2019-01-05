package com.example.ahmet.securemailclient.model;

public class Column {

    public enum Type {
        INTEGER,TEXT,BLOB,REAL,NULL
    }

    private final String name;
    private final Type type;
    private boolean isPrimary=false;
    private boolean autoIncrement=false;
    private boolean canNullable=false;

    public Column(String name,Type type) {
        this.name=name;
        this.type=type;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setCanNullable(boolean canNullable) {
        this.canNullable = canNullable;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isCanNullable() {
        return canNullable;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append(name).append(" ");
        builder.append(type.toString());
        if (isPrimary) {
            builder.append(" ").append("PRIMARY KEY");
            if (autoIncrement) {
                builder.append(" ").append("AUTOINCREMENT");
            }
        } else {
            if (!canNullable) {
                builder.append(" ").append("NOT NULL");
            }
        }
        return builder.toString();
    }
}
