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
 * Portions created by the Initial Developer are Copyright (C) 1998-2013 the
 * Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */

package com.trollworks.gcs.app;

import static com.trollworks.gcs.app.GCSApp_LS.*;

import com.trollworks.gcs.advantage.Advantage;
import com.trollworks.gcs.character.SheetWindow;
import com.trollworks.gcs.common.ListCollectionThread;
import com.trollworks.gcs.equipment.Equipment;
import com.trollworks.gcs.library.LibraryFile;
import com.trollworks.gcs.menu.data.DataMenu;
import com.trollworks.gcs.menu.edit.EditMenu;
import com.trollworks.gcs.menu.file.FileMenu;
import com.trollworks.gcs.menu.file.NewCharacterSheetCommand;
import com.trollworks.gcs.menu.help.HelpMenu;
import com.trollworks.gcs.menu.item.ItemMenu;
import com.trollworks.gcs.preferences.SheetPreferences;
import com.trollworks.gcs.skill.Skill;
import com.trollworks.gcs.spell.Spell;
import com.trollworks.gcs.template.TemplateWindow;
import com.trollworks.ttk.annotation.LS;
import com.trollworks.ttk.annotation.Localized;
import com.trollworks.ttk.cmdline.CmdLine;
import com.trollworks.ttk.menu.StdMenuBar;
import com.trollworks.ttk.menu.window.WindowMenu;
import com.trollworks.ttk.preferences.FontPreferences;
import com.trollworks.ttk.preferences.MenuKeyPreferences;
import com.trollworks.ttk.preferences.PreferencesWindow;
import com.trollworks.ttk.utility.App;
import com.trollworks.ttk.utility.UpdateChecker;
import com.trollworks.ttk.utility.WindowsRegistry;
import com.trollworks.ttk.widgets.AppWindow;

import java.io.File;
import java.util.HashMap;

@Localized({
				@LS(key = "SHEET_DESCRIPTION", msg = "GURPS Character Sheet"),
				@LS(key = "LIBRARY_DESCRIPTION", msg = "GCS Library"),
				@LS(key = "TEMPLATE_DESCRIPTION", msg = "GCS Character Template"),
				@LS(key = "TRAITS_DESCRIPTION", msg = "GCS Traits"),
				@LS(key = "EQUIPMENT_DESCRIPTION", msg = "GCS Equipment"),
				@LS(key = "SKILLS_DESCRIPTION", msg = "GCS Skills"),
				@LS(key = "SPELLS_DESCRIPTION", msg = "GCS Spells"),
})
/** The main application user interface. */
public class GCSApp extends App {
	/** The one and only instance of this class. */
	public static final GCSApp	INSTANCE	= new GCSApp();

	private GCSApp() {
		super();
		AppWindow.setDefaultWindowIcon(GCSImages.getDefaultWindowIcon());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configureApplication(CmdLine cmdLine) {
		HashMap<String, String> map = new HashMap<>();
		map.put(SheetWindow.SHEET_EXTENSION.substring(1), SHEET_DESCRIPTION);
		map.put(LibraryFile.EXTENSION.substring(1), LIBRARY_DESCRIPTION);
		map.put(TemplateWindow.EXTENSION.substring(1), TEMPLATE_DESCRIPTION);
		map.put(Advantage.OLD_ADVANTAGE_EXTENSION.substring(1), TRAITS_DESCRIPTION);
		map.put(Equipment.OLD_EQUIPMENT_EXTENSION.substring(1), EQUIPMENT_DESCRIPTION);
		map.put(Skill.OLD_SKILL_EXTENSION.substring(1), SKILLS_DESCRIPTION);
		map.put(Spell.OLD_SPELL_EXTENSION.substring(1), SPELLS_DESCRIPTION);
		WindowsRegistry.register("GCS", map, new File(APP_HOME_DIR, "gcs.bat"), new File(APP_HOME_DIR, "GURPS Character Sheet.app/Contents/Resources")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		UpdateChecker.check("gcs", "http://gurpscharactersheet.com/current.txt", "http://gurpscharactersheet.com"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		ListCollectionThread.get();

		StdMenuBar.configure(FileMenu.class, EditMenu.class, DataMenu.class, ItemMenu.class, WindowMenu.class, HelpMenu.class);
		SheetPreferences.initialize();
		PreferencesWindow.addCategory(SheetPreferences.class);
		PreferencesWindow.addCategory(FontPreferences.class);
		PreferencesWindow.addCategory(MenuKeyPreferences.class);
	}

	@Override
	public void noWindowsAreOpenAtStartup(boolean finalChance) {
		if (finalChance) {
			NewCharacterSheetCommand.newSheet();
		} else {
			StartupDialog sd = new StartupDialog();
			sd.setVisible(true);
		}
	}

	@Override
	public void finalStartup() {
		super.finalStartup();
		// RAW: With any luck, this will fix the issue that cropped up in Java 7 on the Mac where
		// the menu bar disappears after a dialog comes up. Unfortunately, this bug
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8022667 shows that it won't work until
		// at least Java 7u60. (sigh)
		setDefaultMenuBar(new StdMenuBar());
	}
}