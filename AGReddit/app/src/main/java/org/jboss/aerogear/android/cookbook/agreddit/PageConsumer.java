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
package org.jboss.aerogear.android.cookbook.agreddit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.paging.PageConfig;
import org.jboss.aerogear.android.pipe.paging.PageParameterExtractor;

import java.net.URI;

public class PageConsumer implements PageParameterExtractor<PageConfig> {

    @Override
    public ReadFilter getNextFilter(HeaderAndBody result, PageConfig config) {
        ReadFilter filter = new ReadFilter();
        JsonParser parser =new JsonParser();
        JsonElement element = parser.parse(new String(result.getBody()));
        String next = getFromJSON(element, config.getNextIdentifier());
        if (next != null) {
        	filter.setLinkUri(URI.create("?count=25&after=" + next));
        } 
        return filter;
    }
    
    @Override
    public ReadFilter getPreviousFilter(HeaderAndBody result, PageConfig config) {
        ReadFilter filter = new ReadFilter();
        JsonParser parser =new JsonParser();
        JsonElement element = parser.parse(new String(result.getBody()));
        String previous = getFromJSON(element, config.getPreviousIdentifier());
        if (previous != null) {
        	filter.setLinkUri(URI.create("?count=26&before=" + previous));
        } 
        return filter;
    }

    private String getFromJSON(JsonElement element, String nextIdentifier) {
        String[] identifiers = nextIdentifier.split("\\.");
        for( String identifier:identifiers) {
            element = element.getAsJsonObject().get(identifier);
        }
        if (element.isJsonNull()) {
        	return null;
        }
        
        return element.getAsString();
    }

}
