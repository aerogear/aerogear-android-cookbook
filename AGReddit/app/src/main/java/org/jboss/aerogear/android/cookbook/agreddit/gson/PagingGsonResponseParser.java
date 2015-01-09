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
package org.jboss.aerogear.android.cookbook.agreddit.gson;

import com.google.gson.Gson;

import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.paging.PageConfig;
import org.jboss.aerogear.android.pipe.paging.WrappingPagedList;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by summers on 12/9/14.
 */
public class PagingGsonResponseParser<T> extends GsonResponseParser<T> {

    private final PageConfig pagingConfig;
    private Pipe<T> pipe;

    public PagingGsonResponseParser(Gson gson, PageConfig pagingConfig) {
        super(gson);
        this.pagingConfig = pagingConfig;
    }

    @Override
    public List<T> handleResponse(HeaderAndBody httpResponse, Class<T> responseType) {
        List<T> response = super.handleResponse(httpResponse, responseType);
        
        return computePagedList(response, httpResponse, new ReadFilter().getWhere(), pipe);
    }

    private List<T> computePagedList(List<T> result, HeaderAndBody httpResponse, JSONObject where, Pipe<T> requestingPipe) {
        ReadFilter previousRead = null;
        ReadFilter nextRead = null;

        nextRead = pagingConfig.getPageParameterExtractor().getNextFilter(httpResponse, pagingConfig);
        previousRead = pagingConfig.getPageParameterExtractor().getPreviousFilter(httpResponse, pagingConfig);

        if (nextRead != null) {
            nextRead.setWhere(where);
        }

        if (previousRead != null) {
            previousRead.setWhere(where);
        }

        return new WrappingPagedList<T>(requestingPipe, result, nextRead, previousRead);
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }
}
