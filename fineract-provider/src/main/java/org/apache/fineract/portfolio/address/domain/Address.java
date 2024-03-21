/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.address.domain;

import com.google.gson.JsonObject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.ClientAddress;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "m_address")
public class Address extends AbstractPersistableCustom {

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private Set<ClientAddress> clientaddress;

    @Column(name = "street")
    private String street;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_line_3")
    private String addressLine3;

    @Column(name = "town_village")
    private String townVillage;

    @Column(name = "city")
    private String city;

    @Column(name = "county_district")
    private String countyDistrict;

    @ManyToOne
    @JoinColumn(name = "state_province_id")
    private CodeValue stateProvince;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CodeValue country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private LocalDate createdOn;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_on")
    private LocalDate updatedOn;

    public static Address fromJson(final JsonCommand command, final CodeValue stateProvince, final CodeValue country) {
        return Address.builder().street(command.stringValueOfParameterNamed("street"))
                .addressLine1(command.stringValueOfParameterNamed("addressLine1"))
                .addressLine2(command.stringValueOfParameterNamed("addressLine2"))
                .addressLine3(command.stringValueOfParameterNamed("addressLine3"))
                .townVillage(command.stringValueOfParameterNamed("townVillage")).city(command.stringValueOfParameterNamed("city"))
                .countyDistrict(command.stringValueOfParameterNamed("countyDistrict"))
                .postalCode(command.stringValueOfParameterNamed("postalCode")).latitude(command.bigDecimalValueOfParameterNamed("latitude"))
                .stateProvince(stateProvince).country(country).longitude(command.bigDecimalValueOfParameterNamed("longitude"))
                .createdBy(command.stringValueOfParameterNamed("createdBy")).createdOn(command.localDateValueOfParameterNamed("createdOn"))
                .updatedBy(command.stringValueOfParameterNamed("updatedBy")).updatedOn(command.localDateValueOfParameterNamed("updatedOn"))
                .build();
    }

    public static Address fromJsonObject(final JsonObject jsonObject, final CodeValue province, final CodeValue country) {
        Address.AddressBuilder addressBuilder = Address.builder();

        addressBuilder.stateProvince(province).country(country);

        if (jsonObject.has("street")) {
            addressBuilder.street(jsonObject.get("street").getAsString());
        }

        if (jsonObject.has("addressLine1")) {
            addressBuilder.addressLine1(jsonObject.get("addressLine1").getAsString());
        }
        if (jsonObject.has("addressLine2")) {

            addressBuilder.addressLine2(jsonObject.get("addressLine2").getAsString());
        }
        if (jsonObject.has("addressLine3")) {
            addressBuilder.addressLine3(jsonObject.get("addressLine3").getAsString());
        }
        if (jsonObject.has("townVillage")) {
            addressBuilder.townVillage(jsonObject.get("townVillage").getAsString());
        }
        if (jsonObject.has("city")) {
            addressBuilder.city(jsonObject.get("city").getAsString());
        }
        if (jsonObject.has("countyDistrict")) {
            addressBuilder.countyDistrict(jsonObject.get("countyDistrict").getAsString());
        }
        if (jsonObject.has("postalCode")) {

            addressBuilder.postalCode(jsonObject.get("postalCode").getAsString());
        }
        if (jsonObject.has("latitude")) {

            addressBuilder.latitude(jsonObject.get("latitude").getAsBigDecimal());
        }
        if (jsonObject.has("longitude")) {

            addressBuilder.longitude(jsonObject.get("longitude").getAsBigDecimal());
        }

        if (jsonObject.has("createdBy")) {
            addressBuilder.createdBy(jsonObject.get("createdBy").getAsString());
        }
        if (jsonObject.has("createdOn")) {
            addressBuilder.createdOn(LocalDate.parse(jsonObject.get("createdOn").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        }
        if (jsonObject.has("updatedBy")) {
            addressBuilder.updatedBy(jsonObject.get("updatedBy").getAsString());
        }
        if (jsonObject.has("updatedOn")) {
            addressBuilder.updatedOn(LocalDate.parse(jsonObject.get("updatedOn").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        return addressBuilder.build();
    }
}
