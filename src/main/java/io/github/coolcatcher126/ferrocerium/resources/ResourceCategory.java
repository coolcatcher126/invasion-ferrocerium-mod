package io.github.coolcatcher126.ferrocerium.resources;

import jdk.jfr.BooleanFlag;

@BooleanFlag
public enum ResourceCategory {
    WOOD(1<<0),
    ORES(1<<1),
    STONE(1<<2),
    ALL((1<<0) + (1<<1) + (1<<2));

    int resourceCategory;

    ResourceCategory(int i) {
        this.resourceCategory = i;
    }

    public long getResourceCategoryValue(){
        return this.resourceCategory;
    }

}
