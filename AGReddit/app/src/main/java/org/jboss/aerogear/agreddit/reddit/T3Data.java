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
package org.jboss.aerogear.agreddit.reddit;

public class T3Data {

	private String domain;
	private String banned_by;
	private MediaEmbed media_embed;
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getBanned_by() {
		return banned_by;
	}
	public void setBanned_by(String banned_by) {
		this.banned_by = banned_by;
	}
	public MediaEmbed getMedia_embed() {
		return media_embed;
	}
	public void setMedia_embed(MediaEmbed media_embed) {
		this.media_embed = media_embed;
	}
	
	
	
}
