/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.uca.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import javassist.NotFoundException;

public class UrlTimeoutTransformerTest {
    
    private URLTimeoutTransformer transformer;

    @Before
    public void initFields() {
        transformer = new URLTimeoutTransformer(1, 1);
    }

    @Test
    public void findDeclaredConnectMethod() throws NotFoundException {
        assertNotNull(transformer.findConnectMethod("sun/net/www/protocol/http/HttpURLConnection"));
    }

    @Test(expected = NotFoundException.class)
    public void findInheritedConnectMethod() throws NotFoundException {
        // do NOT look for inherited methods, as we can only rewrite the precise classes the
        // retransform was triggered for
        transformer.findConnectMethod("sun/net/www/protocol/https/DelegateHttpsURLConnection");
    }
    
}
