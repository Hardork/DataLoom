package com.hwq.dataloom.model.dto.tree;

import java.util.List;

public interface TreeResultModel<T> {

    void setChildren(List<T> children);
}
