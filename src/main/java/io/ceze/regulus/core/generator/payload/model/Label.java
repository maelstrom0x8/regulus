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
package io.ceze.regulus.core.generator.payload.model;

public enum Label
{
	BIO_WASTE("Biological Waste", "Animal and Agriculture wastes"),
	HAZARDOUS(
		"Hazardous Waste",
		"Chemicals, toxins, and substances harmful to health or the environment"),
	MEDICAL(
		"Medical Waste",
		"Waste generated in healthcare facilities, such as hospitals, clinics, and"
			+ " laboratories"),
	RADIOACTIVE(
		"Radioactive Waste",
		"Waste containing radioactive materials with harmful radiation levels"),
	MSW(
		"Municipal Solid Waste",
		"General household waste and trash from residential, commercial, and industrial"
			+ " sources");

	private final String name;
	private final String description;

	Label(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}
}
