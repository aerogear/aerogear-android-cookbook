/**
 * JBoss,HomeofProfessionalOpenSource
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.keycloakaccountprovider;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Summers on 9/13/2014.
 */
public class KeyCloakAccount {

    private String name,
            givenName,
            familyName,
            middleName,
            nickname,
            preferredUsername,
            profile,
            picture,
            website,
            email,
            emailVerified,
            gender,
            birthdate,
            zoneinfo,
            locale,
            phoneNumber,
            phoneNumberVerified,
            address,
            updatedAt,
            formatted,
            streetAddress,
            locality,
            region,
            postalCode,
            country,
            claimsLocales,
            accessToken,
            expiresIn,
            refreshToken,
            tokenType,
            idToken,
            notBeforePolicy,
            sessionState;

    private Long expiresOn;

    public KeyCloakAccount() {
    }


    private void extractIdProperties(JSONObject token) {
        name = token.optString("name");
        givenName = token.optString("given_name");
        familyName = token.optString("family_name");
        middleName = token.optString("middle_name");
        nickname = token.optString("nickname");
        preferredUsername = token.optString("preferred_username");
        profile = token.optString("profile");
        picture = token.optString("picture");
        website = token.optString("website");
        email = token.optString("email");
        emailVerified = token.optString("email_verified");
        gender = token.optString("gender");
        birthdate = token.optString("birthdate");
        zoneinfo = token.optString("zoneinfo");
        locale = token.optString("locale");
        phoneNumber = token.optString("phone_number");
        phoneNumberVerified = token.optString("phone_number_verified");
        address = token.optString("address");
        updatedAt = token.optString("updated_at");
        formatted = token.optString("formatted");
        streetAddress = token.optString("street_address");
        locality = token.optString("locality");
        region = token.optString("region");
        postalCode = token.optString("postal_code");
        country = token.optString("country");
        claimsLocales = token.optString("claims_locales");
        expiresOn = new Date().getTime() + Long.parseLong(expiresIn) * 1000;
    }

    public void extractTokenProperties(JSONObject token) {
        accessToken = token.optString("access_token");
        expiresIn = token.optString("expires_in");
        refreshToken = token.optString("refresh_token");
        tokenType = token.optString("token_type");
        idToken = token.optString("id_token");
        notBeforePolicy = token.optString("not-before-policy");
        sessionState = token.optString("session-state");

        try {
            extractIdProperties(new JSONObject(new String(Base64.decode(idToken.split("\\.")[1].getBytes(), Base64.DEFAULT))));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(String phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getClaimsLocales() {
        return claimsLocales;
    }

    public void setClaimsLocales(String claimsLocales) {
        this.claimsLocales = claimsLocales;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
        expiresOn = new Date().getTime() + Long.parseLong(expiresIn) * 1000;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getNotBeforePolicy() {
        return notBeforePolicy;
    }

    public void setNotBeforePolicy(String notBeforePolicy) {
        this.notBeforePolicy = notBeforePolicy;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public Long getExpiresOn() {
        return expiresOn;
    }

}
