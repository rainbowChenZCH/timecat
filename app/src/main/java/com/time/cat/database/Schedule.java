/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.database.typeSerializers.BooleanArrayPersister;
import com.time.cat.database.typeSerializers.LocalDatePersister;
import com.time.cat.database.typeSerializers.LocalTimePersister;
import com.time.cat.mvp.model.User;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

/**
 * Created by joseangel.pineiro
 */
@DatabaseTable(tableName = "Schedules")
public class Schedule {

    public static final int SCHEDULE_TYPE_EVERYDAY = 0; // DEFAULT
    public static final int SCHEDULE_TYPE_SOMEDAYS = 1;
    public static final int SCHEDULE_TYPE_INTERVAL = 2;

    public static final int SCHEDULE_TYPE_HOURLY = 4;
    public static final int SCHEDULE_TYPE_CYCLE = 5;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DAYS = "Days";
    public static final String COLUMN_START = "Start";
    public static final String COLUMN_START_TIME = "Starttime";
    public static final String COLUMN_DOSE = "Dose";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_CYCLE = "Cycle";

    public static final String COLUMN_SCANNED = "Scanned";

    public static final String COLUMN_USER = "User";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_DAYS, persisterClass = BooleanArrayPersister.class)
    private boolean[] days = noWeekDays();

    @DatabaseField(columnName = COLUMN_START, persisterClass = LocalDatePersister.class)
    private LocalDate start;

    @DatabaseField(columnName = COLUMN_START_TIME, persisterClass = LocalTimePersister.class)
    private LocalTime startTime;

    @DatabaseField(columnName = COLUMN_DOSE)
    private float dose = 1f;

    @DatabaseField(columnName = COLUMN_TYPE)
    private int type = SCHEDULE_TYPE_EVERYDAY;

    @DatabaseField(columnName = COLUMN_CYCLE)
    private String cycle;

    @DatabaseField(columnName = COLUMN_SCANNED)
    private boolean scanned;

    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    private User user;


    public int type() {
        return type;
    }

    public void setType(int type) {
        if (type < 0 || type > 5) {
            throw new RuntimeException("Invalid schedule type");
        }
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ScheduleItem> items() {
        return DB.scheduleItems().findBySchedule(this);
    }

    public LocalDate start() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public float dose() {
        return dose;
    }

    public User user() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // *************************************
    // DB queries
    // *************************************

    public static List<Schedule> findAll() {
        return DB.schedules().findAll();
    }

    public static Schedule findById(long id) {
        return DB.schedules().findById(id);
    }


    public void setDose(float dose) {
        this.dose = dose;
    }

    public void save() {
        DB.schedules().save(this);
    }

    public void deleteCascade() {
        DB.schedules().deleteCascade(this, false);
    }

    public boolean[] getLegacyDays() {
        return days;
    }

    public boolean repeatsHourly() {
        return type == SCHEDULE_TYPE_HOURLY;
    }


    public LocalTime startTime() {
        return startTime;
    }

    public void setStartTime(LocalTime t) {
        startTime = t;
    }

    @Override
    public String toString() {
        return "Schedule{" + "id=" + id + ", start=" + start + ", dose=" + dose + ", type=" + type + '}';
    }

    public static final boolean[] noWeekDays() {
        return new boolean[]{false, false, false, false, false, false, false};
    }

    public static final boolean[] allWeekDays() {
        return new boolean[]{true, true, true, true, true, true, true};
    }

    public String displayDose() {
        int integerPart = (int) dose;
        double fraction = dose - integerPart;

        String fractionRational;
        if (fraction == 0.125) {
            fractionRational = "1/8";
        } else if (fraction == 0.25) {
            fractionRational = "1/4";
        } else if (fraction == 0.5) {
            fractionRational = "1/2";
        } else if (fraction == 0.75) {
            fractionRational = "3/4";
        } else if (fraction == 0) {
            return "" + ((int) dose);
        } else {
            return "" + dose;
        }
        return integerPart + "+" + fractionRational;
    }



    public void setCycle(int days, int rest) {
        this.cycle = days + "," + rest;
    }

    public int getCycleDays() {
        if (cycle == null) {
            return 0;
        }
        String[] parts = cycle.split(",");
        return Integer.valueOf(parts[0]);
    }

    public int getCycleRest() {
        if (cycle == null) {
            return 0;
        }
        String[] parts = cycle.split(",");
        return Integer.valueOf(parts[1]);
    }

    public boolean scanned() {
        return scanned;
    }

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }
}

