/*
 * Copyright ©1998-2021 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, version 2.0. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.feature;

import com.trollworks.gcs.criteria.IntegerCriteria;
import com.trollworks.gcs.criteria.NumericCompareType;
import com.trollworks.gcs.criteria.StringCompareType;
import com.trollworks.gcs.criteria.StringCriteria;
import com.trollworks.gcs.datafile.DataFile;
import com.trollworks.gcs.skill.Skill;
import com.trollworks.gcs.ui.widget.outline.ListRow;
import com.trollworks.gcs.utility.json.JsonMap;
import com.trollworks.gcs.utility.json.JsonWriter;
import com.trollworks.gcs.utility.text.Enums;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/** A weapon damage bonus. */
public class WeaponDamageBonus extends Bonus {
    public static final  String KEY_ROOT           = "weapon_bonus";
    private static final String KEY_SELECTION_TYPE = "selection_type";
    private static final String KEY_NAME           = "name";
    private static final String KEY_SPECIALIZATION = "specialization";
    private static final String KEY_LEVEL          = "level";
    private static final String KEY_CATEGORY       = "category";

    public static final String THIS_WEAPON_ID         = "\u0001";
    public static final String WEAPON_NAMED_ID_PREFIX = "weapon_named.";

    private WeaponSelectionType mWeaponSelectionType;
    private StringCriteria      mNameCriteria;
    private StringCriteria      mSpecializationCriteria;
    private IntegerCriteria     mRelativeLevelCriteria;
    private StringCriteria      mCategoryCriteria;

    /** Creates a new skill bonus. */
    public WeaponDamageBonus() {
        super(1);
        mWeaponSelectionType = WeaponSelectionType.WEAPONS_WITH_REQUIRED_SKILL;
        mNameCriteria = new StringCriteria(StringCompareType.IS, "");
        mSpecializationCriteria = new StringCriteria(StringCompareType.ANY, "");
        mRelativeLevelCriteria = new IntegerCriteria(NumericCompareType.AT_LEAST, 0);
        mCategoryCriteria = new StringCriteria(StringCompareType.ANY, "");
    }

    public WeaponDamageBonus(DataFile dataFile, JsonMap m) throws IOException {
        this();
        loadSelf(dataFile, m);
    }

    /**
     * Creates a clone of the specified bonus.
     *
     * @param other The bonus to clone.
     */
    public WeaponDamageBonus(WeaponDamageBonus other) {
        super(other);
        mWeaponSelectionType = other.mWeaponSelectionType;
        mNameCriteria = new StringCriteria(other.mNameCriteria);
        mSpecializationCriteria = new StringCriteria(other.mSpecializationCriteria);
        mRelativeLevelCriteria = new IntegerCriteria(other.mRelativeLevelCriteria);
        mCategoryCriteria = new StringCriteria(other.mCategoryCriteria);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WeaponDamageBonus && super.equals(obj)) {
            WeaponDamageBonus wb = (WeaponDamageBonus) obj;
            return mWeaponSelectionType == wb.mWeaponSelectionType &&
                    mNameCriteria.equals(wb.mNameCriteria) &&
                    mSpecializationCriteria.equals(wb.mSpecializationCriteria) &&
                    mRelativeLevelCriteria.equals(wb.mRelativeLevelCriteria) &&
                    mCategoryCriteria.equals(wb.mCategoryCriteria);
        }
        return false;
    }

    @Override
    public Feature cloneFeature() {
        return new WeaponDamageBonus(this);
    }

    @Override
    public String getJSONTypeName() {
        return KEY_ROOT;
    }

    @Override
    public String getKey() {
        return switch (mWeaponSelectionType) {
            case WEAPONS_WITH_NAME -> buildKey(WEAPON_NAMED_ID_PREFIX);
            case WEAPONS_WITH_REQUIRED_SKILL -> buildKey(Skill.ID_NAME);
            default -> THIS_WEAPON_ID;
        };
    }

    private String buildKey(String prefix) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(prefix);
        if (mNameCriteria.isTypeIs() && mSpecializationCriteria.isTypeAnything() && mCategoryCriteria.isTypeAnything()) {
            buffer.append('/');
            buffer.append(mNameCriteria.getQualifier());
        } else {
            buffer.append("*");
        }
        return buffer.toString();
    }

    public boolean matchesCategories(Set<String> categories) {
        return matchesCategories(mCategoryCriteria, categories);
    }

    @Override
    protected void loadSelf(DataFile dataFile, JsonMap m) throws IOException {
        super.loadSelf(dataFile, m);
        mWeaponSelectionType = Enums.extract(m.getString(KEY_SELECTION_TYPE), WeaponSelectionType.values(), WeaponSelectionType.WEAPONS_WITH_REQUIRED_SKILL);
        switch (mWeaponSelectionType) {
        case WEAPONS_WITH_NAME -> {
            mNameCriteria.load(m.getMap(KEY_NAME));
            mSpecializationCriteria.load(m.getMap(KEY_SPECIALIZATION));
            mCategoryCriteria.load(m.getMap(KEY_CATEGORY));
        }
        case WEAPONS_WITH_REQUIRED_SKILL -> {
            mNameCriteria.load(m.getMap(KEY_NAME));
            mSpecializationCriteria.load(m.getMap(KEY_SPECIALIZATION));
            mRelativeLevelCriteria.load(m.getMap(KEY_LEVEL));
            mCategoryCriteria.load(m.getMap(KEY_CATEGORY));
        }
        }
    }

    @Override
    protected void saveSelf(JsonWriter w) throws IOException {
        super.saveSelf(w);
        w.keyValue(KEY_SELECTION_TYPE, Enums.toId(mWeaponSelectionType));
        switch (mWeaponSelectionType) {
        case WEAPONS_WITH_NAME -> {
            mNameCriteria.save(w, KEY_NAME);
            mSpecializationCriteria.save(w, KEY_SPECIALIZATION);
            mCategoryCriteria.save(w, KEY_CATEGORY);
        }
        case WEAPONS_WITH_REQUIRED_SKILL -> {
            mNameCriteria.save(w, KEY_NAME);
            mSpecializationCriteria.save(w, KEY_SPECIALIZATION);
            mRelativeLevelCriteria.save(w, KEY_LEVEL);
            mCategoryCriteria.save(w, KEY_CATEGORY);
        }
        }
    }

    public WeaponSelectionType getWeaponSelectionType() {
        return mWeaponSelectionType;
    }

    public boolean setWeaponSelectionType(WeaponSelectionType type) {
        if (mWeaponSelectionType != type) {
            mWeaponSelectionType = type;
            return true;
        }
        return false;
    }

    /** @return The name criteria. */
    public StringCriteria getNameCriteria() {
        return mNameCriteria;
    }

    /** @return The name criteria. */
    public StringCriteria getSpecializationCriteria() {
        return mSpecializationCriteria;
    }

    /** @return The level criteria. */
    public IntegerCriteria getRelativeLevelCriteria() {
        return mRelativeLevelCriteria;
    }

    /** @return The category criteria. */
    public StringCriteria getCategoryCriteria() {
        return mCategoryCriteria;
    }

    @Override
    public void fillWithNameableKeys(Set<String> set) {
        switch (mWeaponSelectionType) {
        case WEAPONS_WITH_NAME, WEAPONS_WITH_REQUIRED_SKILL -> {
            ListRow.extractNameables(set, mNameCriteria.getQualifier());
            ListRow.extractNameables(set, mSpecializationCriteria.getQualifier());
            ListRow.extractNameables(set, mCategoryCriteria.getQualifier());
        }
        }
    }

    @Override
    public void applyNameableKeys(Map<String, String> map) {
        switch (mWeaponSelectionType) {
        case WEAPONS_WITH_NAME, WEAPONS_WITH_REQUIRED_SKILL -> {
            mNameCriteria.setQualifier(ListRow.nameNameables(map, mNameCriteria.getQualifier()));
            mSpecializationCriteria.setQualifier(ListRow.nameNameables(map, mSpecializationCriteria.getQualifier()));
            mCategoryCriteria.setQualifier(ListRow.nameNameables(map, mCategoryCriteria.getQualifier()));
        }
        }
    }

    @Override
    public String getToolTipAmount() {
        return getAmount().getAmountAsWeaponBonus();
    }
}
