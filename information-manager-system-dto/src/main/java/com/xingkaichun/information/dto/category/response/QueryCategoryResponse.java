package com.xingkaichun.information.dto.category.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xingkaichun.information.dto.category.CategoryDTO;
import lombok.Data;

import java.util.List;

@Data
public class QueryCategoryResponse {

    @JsonProperty("CategoryDTOList")
    private List<CategoryDTO> categoryDTOList;

    public QueryCategoryResponse(List<CategoryDTO> categoryDTOList) {
        this.categoryDTOList = categoryDTOList;
    }

    public List<CategoryDTO> getCategoryDTOList() {
        return categoryDTOList;
    }

    public void setCategoryDTOList(List<CategoryDTO> categoryDTOList) {
        this.categoryDTOList = categoryDTOList;
    }
}
