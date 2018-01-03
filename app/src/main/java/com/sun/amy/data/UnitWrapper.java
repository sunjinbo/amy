package com.sun.amy.data;

import java.io.File;
import java.io.Serializable;

/**
 * UnitWrapper class.
 */
public class UnitWrapper implements Serializable {
    public UnitBean unitBean;
    public File unit;
    public String unitName;

    public UnitWrapper(String unitName, UnitBean unitBean, File unit) {
        this.unitName = unitName;
        this.unitBean = unitBean;
        this.unit = unit;
    }
}
