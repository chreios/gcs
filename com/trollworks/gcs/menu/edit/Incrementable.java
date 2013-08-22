/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is GURPS Character Sheet.
 *
 * The Initial Developer of the Original Code is Richard A. Wilkes.
 * Portions created by the Initial Developer are Copyright (C) 1998-2002,
 * 2005-2013 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */

package com.trollworks.gcs.menu.edit;

/** Objects that can have their data incremented/decremented should implement this interface. */
public interface Incrementable {
	/** @return The title to use for the increment menu item. */
	String getIncrementTitle();

	/** @return The title to use for the decrement menu item. */
	String getDecrementTitle();

	/** @return Whether the data can be incremented. */
	boolean canIncrement();

	/** @return Whether the data can be decremented. */
	boolean canDecrement();

	/** Call to increment the data. */
	void increment();

	/** Call to decrement the data. */
	void decrement();
}