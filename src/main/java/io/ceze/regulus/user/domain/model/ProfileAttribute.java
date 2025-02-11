/*
 * Copyright (C) 2024 Emmanuel Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ceze.regulus.user.domain.model;

import io.ceze.config.MapConvert;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.ColumnTransformer;

@Embeddable
public class ProfileAttribute {


	@Column(name = "attr_key", nullable = false)
	private String key;

	@Column(name = "attr_value", columnDefinition = "jsonb")
	@Convert(converter = MapConvert.class)
	@ColumnTransformer(write = "?::jsonb")
	private Object value;

	public ProfileAttribute () {}

	public ProfileAttribute (String key, Object value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey ()
	{
		return key;
	}

	public void setKey (String key)
	{
		this.key = key;
	}

	public Object getValue ()
	{
		return value;
	}

	public void setValue (Object value)
	{
		this.value = value;
	}
}
