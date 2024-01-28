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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(TestRenameHandler.class, renamed);
    }

    @Test
    void test_noMatchType() throws ClassNotFoundException {
        assertThrows(ClassNotFoundException.class, () -> {
            var test = RenameHandler.create();
            test.lookupType("com.foo.Foo");
        });
    }

    @Test
    void test_renameBlockedType1() {
        assertThrows(IllegalArgumentException.class, () -> {
            var test = RenameHandler.create();
            test.renamedType("java.lang.String", TestRenameHandler.class);
        });
    }

    @Test
    void test_renameBlockedType2() {
        assertThrows(IllegalArgumentException.class, () -> {
            var test = RenameHandler.create();
            test.renamedType("javax.foo.Bar", TestRenameHandler.class);
        });
    }

    @Test
    void test_renameBlockedType3() {
        assertThrows(IllegalArgumentException.class, () -> {
            var test = RenameHandler.create();
            test.renamedType("org.joda.foo.Bar", TestRenameHandler.class);
        });
    }

    @Test
    void test_locked() {
        assertThrows(IllegalStateException.class, () -> {
            var test = RenameHandler.create();
            test.renamedType("com.foo.Bar", TestRenameHandler.class);
            test.lock();
            test.renamedType("com.foo.Foo", TestRenameHandler.class);
        });
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
            assertEquals(Status.class, test.lookupType("com.foo.Bar"));
            assertEquals(Status.VALID, test.lookupEnum(Status.class, "YES"));
            assertEquals(DistanceMethodMethod.class, test.lookupType("com.foo.Foo"));
            assertEquals(Status.INVALID, test.lookupEnum(Status.class, "NO"));
            var logged = baos.toString("UTF-8");
            assertTrue(logged.startsWith("ERROR: Invalid Renamed.ini: "));
            assertTrue(logged.contains("org.joda.convert.ClassDoesNotExist"));
            // ensure that the bad init class is loaded, and that it did not see a null RenameHandler
            assertTrue(test.getTypeRenames().containsKey("com.foo.convert.TestRenameHandlerBadInit"));
            assertTrue(Status.STRING_CONVERTIBLE);
            assertFalse(BAD_INIT.get());

        } finally {
            System.setErr(originalErr);
        }
    }

}
