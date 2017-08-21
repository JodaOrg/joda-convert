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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link RenameHandler}.
 */
public class TestRenameHandler {

    @Test
    public void test_matchRenamedType() throws ClassNotFoundException {
        RenameHandler test = RenameHandler.create();
        test.renamedType("com.foo.Bar", TestRenameHandler.class);
        Class<?> renamed = test.lookupType("com.foo.Bar");
        Assert.assertEquals(TestRenameHandler.class, renamed);
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

}
