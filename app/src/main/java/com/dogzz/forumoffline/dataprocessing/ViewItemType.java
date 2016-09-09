package com.dogzz.forumoffline.dataprocessing;/*
* @Author: dogzz
* @Created: 9/7/2016
*/

public enum ViewItemType {
    SECTION (1),
    THREAD (2),
    SAVED (3);

    int typeId;

    ViewItemType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static ViewItemType fromInt(int typeId) {
        for (ViewItemType type : ViewItemType.values()) {
            if (type.getTypeId() == typeId) {
                return type;
            }
        }
        return null;
    }
}
