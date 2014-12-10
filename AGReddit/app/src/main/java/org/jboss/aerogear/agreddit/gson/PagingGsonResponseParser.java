package org.jboss.aerogear.agreddit.gson;

import com.google.gson.Gson;

import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.pipeline.GsonResponseParser;
import org.jboss.aerogear.android.impl.pipeline.paging.WrappingPagedList;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.paging.PageConfig;
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
