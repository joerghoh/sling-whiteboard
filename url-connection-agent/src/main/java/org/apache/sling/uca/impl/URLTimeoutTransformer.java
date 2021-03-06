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

import java.lang.instrument.ClassFileTransformer;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * Transforms well-known HTTP URL connection classes
 * 
 * <p>This implementation adds connect and read timeouts to those connections
 * if none are defined.</p>
 * 
 * @see URLConnection#getConnectTimeout()
 * @see URLConnection#getReadTimeout()
 *
 */
class URLTimeoutTransformer implements ClassFileTransformer {

    private static final Set<String> CLASSES_TO_TRANSFORM = new HashSet<>();

    static {
        CLASSES_TO_TRANSFORM.add(Descriptor.toJvmName("sun.net.www.protocol.http.HttpURLConnection"));
        CLASSES_TO_TRANSFORM.add(Descriptor.toJvmName("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection"));
    }

    private final long readTimeoutMillis;
    private final long connectTimeoutMillis;

    public URLTimeoutTransformer(long connectTimeout, long readTimeout) {
        this.connectTimeoutMillis = connectTimeout;
        this.readTimeoutMillis = readTimeout;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (CLASSES_TO_TRANSFORM.contains(className)) {
                System.out.println("[AGENT] Asked to transform " + className);
                CtMethod connectMethod = findConnectMethod(className);
                connectMethod.insertBefore("if ( getConnectTimeout() == 0 ) { setConnectTimeout(" + connectTimeoutMillis + "); }");
                connectMethod.insertBefore("if ( getReadTimeout() == 0 ) { setReadTimeout(" + readTimeoutMillis + "); }");
                classfileBuffer = connectMethod.getDeclaringClass().toBytecode();
                connectMethod.getDeclaringClass().detach();
                System.out.println("[AGENT] Transformation complete!");
            }
            return classfileBuffer;
        } catch (Exception e) {
            e.printStackTrace(); // ensure _something_ is printed
            throw new RuntimeException("[AGENT] Transformation failed", e);
        }
    }
    
    CtMethod findConnectMethod(String className) throws NotFoundException {
        
        ClassPool defaultPool = ClassPool.getDefault();
        CtClass cc = defaultPool.get(Descriptor.toJavaName(className));
        if (cc == null) {
            System.out.println("[AGENT] no class found with name " + className);
            return null;
        }
        return cc.getDeclaredMethod("connect");

    }

}