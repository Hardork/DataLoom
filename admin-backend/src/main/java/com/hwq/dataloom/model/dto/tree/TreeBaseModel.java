package com.hwq.dataloom.model.dto.tree;

import java.io.Serializable;

public interface TreeBaseModel<T> extends Serializable {

    Long getId();

    Long getPid();

    String getName();

    void setName(String name);
}
