/*
 * Remarkable API - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.remarkable.api.model.content;

/**
 * The Enum Type.
 */
public enum Type {

	/** The document. */
	DOCUMENT("Document"),

	/** The collection. */
	COLLECTION("Collection");

	/** The type. */
	private final String type;

	/**
	 * Instantiates a new type.
	 *
	 * @param type the type
	 */
	private Type(String type) {
		this.type = type + "Type";
	}

	/**
	 * Gets the.
	 *
	 * @param type the type
	 * @return the type
	 */
	public static Type get(String type) {
		Type[] types = Type.values();
		for (Type t : types) {
			if (type.equals(t.getType())) {
				return t;
			}
		}

		return null;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

}
