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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

/**
 * Test {@link RenameHandler}.
 */
public class TestRenameHandler {

    static final AtomicBoolean BAD_INIT = new AtomicBoolean();

    @Test
    public void test_matchRenamedType() throws ClassNotFoundException {
        RenameHandler test = RenameHandler.create();
        test.renamedType("com.foo.Bar", TestRenameHandler.class);
        Class<?> renamed = test.lookupType("com.foo.Bar");
        assertEquals(TestRenameHandler.class, renamed);
    }

    @Test(expected = ClassNotFoundException.class)
    public void test_noMatchType() throws ClassNotFoundException {
        RenameHandler test = RenameHandler.create();
        test.lookupType("com.foo.Foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_renameBlockedType1() {
        RenameHandler test = RenameHandler.create();
        test.renamedType("java.lang.String", TestRenameHandler.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_renameBlockedType2() {
        RenameHandler test = RenameHandler.create();
        test.renamedType("javax.foo.Bar", TestRenameHandler.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_renameBlockedType3() {
        RenameHandler test = RenameHandler.create();
        test.renamedType("org.joda.foo.Bar", TestRenameHandler.class);
    }

    @Test(expected = IllegalStateException.class)
    public void test_locked() {
        RenameHandler test = RenameHandler.create();
        test.renamedType("com.foo.Bar", TestRenameHandler.class);
        test.lock();
        test.renamedType("com.foo.Foo", TestRenameHandler.class);
    }

    @Test
    public void test_matchUsingConfigFile() throws Exception {
        PrintStream originalErr = System.err;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, false, "UTF-8");
            System.setErr(ps);
            RenameHandler test = RenameHandler.create(true);
            System.err.flush();
            assertEquals(Status.class, test.lookupType("com.foo.Bar"));
            assertEquals(Status.VALID, test.lookupEnum(Status.class, "YES"));
            assertEquals(DistanceMethodMethod.class, test.lookupType("com.foo.Foo"));
            assertEquals(Status.INVALID, test.lookupEnum(Status.class, "NO"));
            String logged = baos.toString("UTF-8");
            assertTrue(logged.startsWith("ERROR: Invalid Renamed.ini: "));
            assertTrue(logged.contains("org.joda.convert.ClassDoesNotExist"));
            // ensure that the bad init class is loaded, and that it did not see a null RenameHandler
            assertTrue(test.getTypeRenames().containsKey("com.foo.convert.TestRenameHandlerBadInit"));
            assertFalse(BAD_INIT.get());

        } finally {
            System.setErr(originalErr);
        }
    }

}
