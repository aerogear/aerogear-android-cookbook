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
package org.jboss.aerogear.android.cookbook.agreddit.reddit;

import java.util.List;

public class T3 {

	private String kind;
	private List<T3Data> data;
	private String subreddit;
	private String selftext_html;
	private String selftext;
	private String likes;
	private String link_flair_text;
	private String id;
	private Boolean clicked;
	private String title;
	private Long num_comments;
	private Long score;
	private String approved_by;
	private Boolean over_18;
	private Boolean hidden;
	private String thumbnail;
	private String subreddit_id;
	private Long edited;
	private String link_flair_css_class;
	private String author_flair_css_class;
	private Long downs;
	private Boolean saved;
	private Boolean is_self;
	private String permalink;
	private String name;
	private Long created;
	private String url;
	private String author_flair_text;
	private String author;
	private Long created_utc;
	private String media;
	private String num_reports;
	private Long ups;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public List<T3Data> getData() {
		return data;
	}

	public void setData(List<T3Data> data) {
		this.data = data;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	public String getSelftext_html() {
		return selftext_html;
	}

	public void setSelftext_html(String selftext_html) {
		this.selftext_html = selftext_html;
	}

	public String getSelftext() {
		return selftext;
	}

	public void setSelftext(String selftext) {
		this.selftext = selftext;
	}

	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	public String getLink_flair_text() {
		return link_flair_text;
	}

	public void setLink_flair_text(String link_flair_text) {
		this.link_flair_text = link_flair_text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getClicked() {
		return clicked;
	}

	public void setClicked(Boolean clicked) {
		this.clicked = clicked;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getNum_comments() {
		return num_comments;
	}

	public void setNum_comments(Long num_comments) {
		this.num_comments = num_comments;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public String getApproved_by() {
		return approved_by;
	}

	public void setApproved_by(String approved_by) {
		this.approved_by = approved_by;
	}

	public Boolean getOver_18() {
		return over_18;
	}

	public void setOver_18(Boolean over_18) {
		this.over_18 = over_18;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getSubreddit_id() {
		return subreddit_id;
	}

	public void setSubreddit_id(String subreddit_id) {
		this.subreddit_id = subreddit_id;
	}

	public Long getEdited() {
		return edited;
	}

	public void setEdited(Long edited) {
		this.edited = edited;
	}

	public String getLink_flair_css_class() {
		return link_flair_css_class;
	}

	public void setLink_flair_css_class(String link_flair_css_class) {
		this.link_flair_css_class = link_flair_css_class;
	}

	public String getAuthor_flair_css_class() {
		return author_flair_css_class;
	}

	public void setAuthor_flair_css_class(String author_flair_css_class) {
		this.author_flair_css_class = author_flair_css_class;
	}

	public Long getDowns() {
		return downs;
	}

	public void setDowns(Long downs) {
		this.downs = downs;
	}

	public Boolean getSaved() {
		return saved;
	}

	public void setSaved(Boolean saved) {
		this.saved = saved;
	}

	public Boolean getIs_self() {
		return is_self;
	}

	public void setIs_self(Boolean is_self) {
		this.is_self = is_self;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor_flair_text() {
		return author_flair_text;
	}

	public void setAuthor_flair_text(String author_flair_text) {
		this.author_flair_text = author_flair_text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getCreated_utc() {
		return created_utc;
	}

	public void setCreated_utc(Long created_utc) {
		this.created_utc = created_utc;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getNum_reports() {
		return num_reports;
	}

	public void setNum_reports(String num_reports) {
		this.num_reports = num_reports;
	}

	public Long getUps() {
		return ups;
	}

	public void setUps(Long ups) {
		this.ups = ups;
	}

	@Override
	public String toString() {
		return title;
	}

}
