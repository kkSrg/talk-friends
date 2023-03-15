package com.tanhua.enums;

/**
 * 评论类型：1-猫头鹰，2-白兔，3-狐狸, 4-狮子
 */
public enum ConclusionType {

    OWL(1), RABBIT(2), FOX(3) ,LION(4);

    int type;

    ConclusionType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}