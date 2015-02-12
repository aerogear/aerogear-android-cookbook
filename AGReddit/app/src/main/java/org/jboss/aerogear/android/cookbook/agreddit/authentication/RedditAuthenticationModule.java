/**
 * JBoss,Home of Professional Open Source
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
package org.jboss.aerogear.android.cookbook.agreddit.authentication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpStatus;
import org.jboss.aerogear.android.authentication.AbstractAuthenticationModule;
import org.jboss.aerogear.android.cookbook.agreddit.R;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.jboss.aerogear.android.pipe.module.AuthorizationFields;
import org.jboss.aerogear.android.pipe.module.ModuleFields;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;



public class RedditAuthenticationModule extends AbstractAuthenticationModule {

	private final URL baseURL;
	private final URL loginURL;

	private String authToken = "";
	private boolean isLoggedIn = false;
	private String modHash;
    private CookieManager COOKIE_MANAGER;

	
	public RedditAuthenticationModule(Context context) {
		try {
			baseURL = new URL(context.getString(R.string.reddit_base) + "api");
			loginURL = new URL(baseURL.toString() + "/login");
            
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public URL getBaseURL() {
		return baseURL;
	}

	public String getLoginEndpoint() {
		return "/login";
	}

	public String getLogoutEndpoint() {
		return "/logout";
	}

	public String getEnrollEndpoint() {
		return null;
	}

	public void login(final String username, final String password,
		    final Callback<HeaderAndBody> callback) {

        COOKIE_MANAGER =new CookieManager( null, CookiePolicy.ACCEPT_NONE);
        CookieHandler.setDefault(COOKIE_MANAGER);

        AsyncTask<Void, Void, HeaderAndBody> task = new AsyncTask<Void, Void, HeaderAndBody>() {
            private Exception exception;

            @Override
            protected HeaderAndBody doInBackground(Void... params) {
                HeaderAndBody result;
                try {
                    HttpProvider provider = new HttpRestProvider(getLoginURL(username));
                    provider.setDefaultHeader("User-Agent", "AeroGear StoryList Demo /u/secondsun");
                    provider.setDefaultHeader("Content-Type", "application/x-www-form-urlencoded");
                    String loginData = buildLoginData(username, password);
                    result = provider.post(loginData);
                    Log.d("Auth", new String(result.getBody()));
                    String json = new String(result.getBody());
                    JsonObject obj = new JsonParser().parse(json).getAsJsonObject().get("json").getAsJsonObject();
                    if(obj.getAsJsonArray("errors").size() != 0) {
                        throw new Exception(obj.get("errors").toString());
                    } else {
                        modHash = obj.get("data").getAsJsonObject().get("modhash").getAsString();
                        authToken = obj.get("data").getAsJsonObject().get("cookie").getAsString();
                        isLoggedIn = true;
                    }
                } catch (Exception e) {
                    Log.e(RedditAuthenticationModule.class.getSimpleName(),
                            "Error with Login", e);
                    exception = e;
                    return null;
                }

                return result;

            }

            @Override
            protected void onPostExecute(HeaderAndBody headerAndBody) {
                if (exception == null) {

                    callback.onSuccess(headerAndBody);
                } else {
                    callback.onFailure(exception);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



	}

    private String buildLoginData(String username, String password) {
        StringBuilder builder = new StringBuilder();
        builder.append("user=").append(URLEncoder.encode(username))
                .append("&api_type=json&passwd=").append(URLEncoder.encode(password));
        return builder.toString();
    }

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public AuthorizationFields getAuthorizationFields() {

		AuthorizationFields fields = new AuthorizationFields();
		fields.addHeader("User-Agent", "AeroGear StoryList Demo /u/secondsun");
		if (isLoggedIn) {
            fields.addHeader("Cookie", "reddit_session="+authToken);
            fields.addQueryParameter("uh", modHash);
		}
		return fields;
	}

	private URL getLoginURL(String userName) {
		try {
			return new URL(loginURL.toString() + "/" + userName);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
    public void login(Map<String, String> loginData, Callback<HeaderAndBody> callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ModuleFields loadModule(URI relativeURI, String httpMethod, byte[] requestBody) {
        ModuleFields moduleFields = new ModuleFields();
        AuthorizationFields authFields = getAuthorizationFields();
        
        moduleFields.setHeaders(authFields.getHeaders());
        moduleFields.setQueryParameters(authFields.getQueryParameters());
        return moduleFields;
    }

    @Override
    public boolean handleError(HttpException exception) {
        if (exception.getStatusCode() == HttpStatus.SC_FORBIDDEN || 
                exception.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            return false;
        } else {
            return false;
        }
    }
	
}
