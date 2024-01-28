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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

/**
 * Test java.time.*.
 */
class TestJavaTime {

    @Test
    void test_basics() throws ClassNotFoundException {
        var test = StringConvert.INSTANCE;
        assertThat(test.convertToString(LocalDate.of(2019, 6, 30))).isEqualTo("2019-06-30");
        assertThat(test.convertToString(LocalTime.of(11, 45))).isEqualTo("11:45");
        assertThat(test.convertToString(LocalDateTime.of(2019, 6, 30, 11, 45))).isEqualTo("2019-06-30T11:45");
        assertThat(test.convertToString(OffsetDateTime.of(2019, 6, 30, 11, 45, 0, 0, ZoneOffset.ofHours(2)))).isEqualTo("2019-06-30T11:45+02:00");
        assertThat(test.convertToString(ZonedDateTime.of(2019, 6, 30, 11, 45, 0, 0, ZoneId.of("Europe/Paris")))).isEqualTo("2019-06-30T11:45+02:00[Europe/Paris]");
        assertThat(test.convertToString(ZoneOffset.ofHours(2))).isEqualTo("+02:00");
        assertThat(test.convertToString(ZoneId.of("Europe/Paris"))).isEqualTo("Europe/Paris");
        assertThat(test.convertFromString(ZoneId.class, "Europe/Paris")).isEqualTo(ZoneId.of("Europe/Paris"));

        assertThat(test.convertFromString(ZoneId.class, "Europe/Paris").toString()).isEqualTo("Europe/Paris");

        var zoneRegionClass = ZoneId.of("Europe/Paris").getClass();  // ZoneRegion
        assertThat(test.convertFromString(zoneRegionClass, "Europe/Paris").toString()).isEqualTo("Europe/Paris");
    }

}
