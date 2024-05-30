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
package org.apache.fineract.apachecon.note.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table("m_appuser")
public class AppUserJdbc implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("username")
    private String username;

    @Column("firstname")
    private String firstname;

    @Column("lastname")
    private String lastname;

    @Column("password")
    private String password;

    @Column("nonexpired")
    private boolean accountNonExpired;

    @Column("nonlocked")
    private boolean accountNonLocked;

    @Column("nonexpired_credentials")
    private boolean credentialsNonExpired;

    @Column("enabled")
    private boolean enabled;

    @Column("firsttime_login_remaining")
    private boolean firstTimeLoginRemaining;

    @Column("is_deleted")
    private boolean deleted;

    @Column("office_id")
    private Long officeId;

    @Column("staff_id")
    private Long staffId;

    @Column("last_time_password_updated")
    private LocalDate lastTimePasswordUpdated;

    @Column("password_never_expires")
    private boolean passwordNeverExpires;

    @Column("is_self_service_user")
    private boolean isSelfServiceUser;

    @Column("cannot_change_password")
    private Boolean cannotChangePassword;
}
