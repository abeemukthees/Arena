/*
 * Copyright 2017, Abhi Muktheeswarar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msa.data.entities.remote.medi;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abhimuktheeswarar on 05-05-2017
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "tablet_name",
        "weight",
        "volume",
        "company_name",
        "molecule_name",
        "something1",
        "created_at",
        "updated_at",
        "created_by",
        "updated_by",
        "is_deleted"
})
public class SearchMedResult {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("tablet_name")
    private String tabletName;
    @JsonProperty("weight")
    private String weight;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("molecule_name")
    private String moleculeName;
    @JsonProperty("something1")
    private String something1;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("created_by")
    private Object createdBy;
    @JsonProperty("updated_by")
    private Object updatedBy;
    @JsonProperty("is_deleted")
    private Integer isDeleted;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("tablet_name")
    public String getTabletName() {
        return tabletName;
    }

    @JsonProperty("tablet_name")
    public void setTabletName(String tabletName) {
        this.tabletName = tabletName;
    }

    @JsonProperty("weight")
    public String getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(String weight) {
        this.weight = weight;
    }

    @JsonProperty("volume")
    public String getVolume() {
        return volume;
    }

    @JsonProperty("volume")
    public void setVolume(String volume) {
        this.volume = volume;
    }

    @JsonProperty("company_name")
    public String getCompanyName() {
        return companyName;
    }

    @JsonProperty("company_name")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @JsonProperty("molecule_name")
    public String getMoleculeName() {
        return moleculeName;
    }

    @JsonProperty("molecule_name")
    public void setMoleculeName(String moleculeName) {
        this.moleculeName = moleculeName;
    }

    @JsonProperty("something1")
    public String getSomething1() {
        return something1;
    }

    @JsonProperty("something1")
    public void setSomething1(String something1) {
        this.something1 = something1;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("created_by")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("created_by")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("updated_by")
    public Object getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updated_by")
    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty("is_deleted")
    public Integer getIsDeleted() {
        return isDeleted;
    }

    @JsonProperty("is_deleted")
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}