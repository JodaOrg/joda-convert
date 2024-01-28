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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

/**
 * Test {@link RenameHandler}.
 */
class TestRenameHandler {

    static final AtomicBoolean BAD_INIT = new AtomicBoolean();

    @Test
    void test_matchRenamedType() throws ClassNotFoundException {
        var test = RenameHandler.create();
        test.renamedType("com.foo.Bar", TestRenameHandler.class);
        var renamed = test.lookupType("com.foo.Bar");
        assertThat(renamed).isEqualTo(TestRenameHandler.class);
    }

    @Test
    void test_noMatchType() throws ClassNotFoundException {
        var test = RenameHandler.create();
        assertThatExceptionOfType(ClassNotFoundException.class)
                .isThrownBy(() -> test.lookupType("com.foo.Foo"));
    }

    @Test
    void test_renameBlockedType1() {
        var test = RenameHandler.create();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.renamedType("java.lang.String", TestRenameHandler.class));
    }

    @Test
    void test_renameBlockedType2() {
        var test = RenameHandler.create();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.renamedType("javax.foo.Bar", TestRenameHandler.class));
    }

    @Test
    void test_renameBlockedType3() {
        var test = RenameHandler.create();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> test.renamedType("org.joda.foo.Bar", TestRenameHandler.class));
    }

    @Test
    void test_locked() {
        var test = RenameHandler.create();
        test.renamedType("com.foo.Bar", TestRenameHandler.class);
        test.lock();
        assertThatIllegalStateException()
                .isThrownBy(() -> test.renamedType("com.foo.Foo", TestRenameHandler.class));
    }

    @Test
    void test_matchUsingConfigFile() throws Exception {
        var originalErr = System.err;
        try {
            var baos = new ByteArrayOutputStream();
            var ps = new PrintStream(baos, false, "UTF-8");
            System.setErr(ps);
            var test = RenameHandler.create(true);
            System.err.flush();
            assertThat(test.lookupType("com.foo.Bar")).isEqualTo(Status.class);
            assertThat(test.lookupEnum(Status.class, "YES")).isEqualTo(Status.VALID);
            assertThat(test.lookupType("com.foo.Foo")).isEqualTo(DistanceMethodMethod.class);
            assertThat(test.lookupEnum(Status.class, "NO")).isEqualTo(Status.INVALID);
            var logged = baos.toString("UTF-8");
            assertThat(logged).startsWith("ERROR: Invalid Renamed.ini: ");
            assertThat(logged).contains("org.joda.convert.ClassDoesNotExist");
            // ensure that the bad init class is loaded, and that it did not see a null RenameHandler
            assertThat(test.getTypeRenames()).containsKey("com.foo.convert.TestRenameHandlerBadInit");
            assertThat(Status.STRING_CONVERTIBLE).isTrue();
            assertThat(BAD_INIT.get()).isFalse();

        } finally {
            System.setErr(originalErr);
        }
    }

}
