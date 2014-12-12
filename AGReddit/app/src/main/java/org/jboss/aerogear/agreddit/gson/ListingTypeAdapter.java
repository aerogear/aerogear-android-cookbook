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
package org.jboss.aerogear.agreddit.gson;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.jboss.aerogear.agreddit.reddit.Listing;
import org.jboss.aerogear.agreddit.reddit.ListingData;
import org.jboss.aerogear.agreddit.reddit.T3;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListingTypeAdapter implements InstanceCreator<Listing>,
		JsonDeserializer<Listing> {

	public Listing deserialize(JsonElement element, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		Listing listing = new Listing();
		if (element.getAsJsonObject().get("after") != null)
			listing.setAfter(element.getAsJsonObject().get("after")
					.getAsString());
		if (element.getAsJsonObject().get("before") != null)
			listing.setBefore(element.getAsJsonObject().get("before")
					.getAsString());
		listing.setKind(element.getAsJsonObject().get("kind").getAsString());
		listing.setData(getListingData(element.getAsJsonObject().get("data")
				.getAsJsonObject()));

		return listing;
	}

	private ListingData getListingData(JsonObject element) {
		ListingData data = new ListingData();

		data.setModhash(element.get("modhash").getAsString());
		data.setChildren(getChildren(element.get("children").getAsJsonArray()));

		return data;
	}

	private List<T3> getChildren(JsonArray element) {
		List<T3> children = new ArrayList<T3>(element.size());
		for (int i = 0; i < element.size(); i++) {
			children.add(getChild(element.get(i).getAsJsonObject()));
		}

		return children;
	}

	private T3 getChild(JsonObject element) {
		T3 child = new T3();
		element = element.get("data").getAsJsonObject();
		if (element == null) {
			return child;
		}
		if (element.get("title") != null)
			child.setTitle(element.get("title").getAsString());
		if (element.get("url") != null)
			child.setUrl(element.get("url").getAsString());
		if (element.get("score") != null)
			child.setScore(element.get("score").getAsLong());
		if (element.get("author") != null) {
			child.setAuthor(element.get("author").getAsString());
		}
		
		if (element.get("subreddit") != null) {
			child.setSubreddit(element.get("subreddit").getAsString());
		}
		
		return child;
	}

	public Listing createInstance(Type arg0) {
		return new Listing();
	}

}
