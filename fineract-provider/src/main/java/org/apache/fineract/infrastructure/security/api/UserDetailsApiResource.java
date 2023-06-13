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
package org.apache.fineract.infrastructure.security.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.constants.TwoFactorConstants;
import org.apache.fineract.infrastructure.security.data.AuthenticatedOauthUserData;
import org.apache.fineract.infrastructure.security.data.FineractJwtAuthenticationToken;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/*
 * Implementation of Oauth2 authentication APIs, loaded only when "oauth" profile is enabled.
 */
@Path("/v1/userdetails")
@Component
@ConditionalOnProperty("fineract.security.oauth.enabled")
@Tag(name = "Fetch authenticated user details", description = "")
@RequiredArgsConstructor
public class UserDetailsApiResource {

    private final ToApiJsonSerializer<AuthenticatedOauthUserData> apiJsonSerializerService;

    @Value("${fineract.security.2fa.enabled}")
    private boolean twoFactorEnabled;

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(summary = "Fetch authenticated user details\n", description = "checks the Authentication and returns the set roles and permissions allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserDetailsApiResourceSwagger.GetUserDetailsResponse.class))) })
    public String fetchAuthenticatedUserData(@Context AppUser user) {
        // TODO: @vidakovic check permission BYPASS_TWO_FACTOR_PERMISSION (only if 2FA is enabled)
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }

        final FineractJwtAuthenticationToken authentication = (FineractJwtAuthenticationToken) context.getAuthentication();
        if (authentication == null) {
            return null;
        }

        final Collection<String> permissions = new ArrayList<>();
        AuthenticatedOauthUserData authenticatedUserData;

        final Collection<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        for (final GrantedAuthority grantedAuthority : authorities) {
            permissions.add(grantedAuthority.getAuthority());
        }

        final Long officeId = user.getOffice().getId();
        final String officeName = user.getOffice().getName();

        final Long staffId = user.getStaffId();
        final String staffDisplayName = user.getStaffDisplayName();

        final EnumOptionData organisationalRole = user.organisationalRoleData();

        boolean isTwoFactorRequired = this.twoFactorEnabled
                && !user.hasSpecificPermissionTo(TwoFactorConstants.BYPASS_TWO_FACTOR_PERMISSION);
        if (!user.isCredentialsNonExpired()) {
            authenticatedUserData = new AuthenticatedOauthUserData().setUsername(user.getUsername()).setUserId(user.getId())
                    .setAccessToken(authentication.getToken().getTokenValue()).setAuthenticated(true).setShouldRenewPassword(true)
                    .setTwoFactorAuthenticationRequired(isTwoFactorRequired);
        } else {
            authenticatedUserData = new AuthenticatedOauthUserData().setUsername(user.getUsername()).setOfficeId(officeId)
                    .setOfficeName(officeName).setStaffId(staffId).setStaffDisplayName(staffDisplayName)
                    .setOrganisationalRole(organisationalRole).setRoles(user.getRoles().stream().map(Role::toData).toList())
                    .setPermissions(permissions).setUserId(user.getId()).setAccessToken(authentication.getToken().getTokenValue())
                    .setAuthenticated(true).setTwoFactorAuthenticationRequired(isTwoFactorRequired);
        }

        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}
