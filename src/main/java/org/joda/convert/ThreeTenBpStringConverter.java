/*
 *  Copyright 2010-present Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.convert;

import java.util.function.Function;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.MonthDay;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.Period;
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

/**
 * Conversion between ThreeTen-Backport classes and a {@code String}.
 * <p>
 * This is loaded in a try-catch block, as this is an optional dependency.
 * Loading this class will throw a ClassNotFoundError if ThreeTen-Backport is not present.
 */
enum ThreeTenBpStringConverter implements TypedStringConverter<Object> {

    /**
     * Instant converter.
     */
    INSTANT(Instant.class, Instant::parse),
    /**
     * Duration converter.
     */
    DURATION(Duration.class, Duration::parse),
    /**
     * LocalDate converter.
     */
    LOCAL_DATE(LocalDate.class, LocalDate::parse),
    /**
     * LocalTime converter.
     */
    LOCAL_TIME(LocalTime.class, LocalTime::parse),
    /**
     * LocalDateTime converter.
     */
    LOCAL_DATE_TIME(LocalDateTime.class, LocalDateTime::parse),
    /**
     * OffsetTime converter.
     */
    OFFSET_TIME(OffsetTime.class, OffsetTime::parse),
    /**
     * OffsetDateTime converter.
     */
    OFFSET_DATE_TIME(OffsetDateTime.class, OffsetDateTime::parse),
    /**
     * ZonedDateTime converter.
     */
    ZONED_DATE_TIME(ZonedDateTime.class, ZonedDateTime::parse),
    /**
     * Year converter.
     */
    YEAR(Year.class, Year::parse),
    /**
     * YearMonth converter.
     */
    YEAR_MONTH(YearMonth.class, YearMonth::parse),
    /**
     * MonthDay converter.
     */
    MONTH_DAY(MonthDay.class, MonthDay::parse),
    /**
     * Period converter.
     */
    PERIOD(Period.class, Period::parse),
    /**
     * ZoneOffset converter.
     */
    ZONE_OFFSET(ZoneOffset.class, ZoneOffset::of),
    /**
     * ZoneId converter.
     */
    ZONE_ID(ZoneId.class, ZoneId::of),
    ;

    /** The type. */
    private Class<?> type;
    private Function<String, Object> fromStringFn;

    /**
     * Creates an enum.
     * @param type  the type, not null
     */
    ThreeTenBpStringConverter(Class<?> type, Function<String, Object> fromStringFn) {
        this.type = type;
        this.fromStringFn = fromStringFn;
    }

    /**
     * Gets the type of the converter.
     * @return the type, not null
     */
    @Override
    public Class<?> getEffectiveType() {
        return type;
    }

    //-----------------------------------------------------------------------
    @Override
    public String convertToString(Object object) {
        return object.toString();
    }

    @Override
    public Object convertFromString(Class<? extends Object> cls, String str) {
        return fromStringFn.apply(str);
    }

}
