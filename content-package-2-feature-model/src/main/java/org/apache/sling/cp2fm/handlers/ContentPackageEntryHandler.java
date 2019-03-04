/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.cp2fm.handlers;

import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.fs.io.Archive.Entry;
import org.apache.jackrabbit.vault.fs.io.ZipStreamArchive;
import org.apache.sling.cp2fm.ContentPackage2FeatureModelConverter;

public final class ContentPackageEntryHandler extends AbstractRegexEntryHandler {

    public ContentPackageEntryHandler() {
        super("jcr_root/etc/packages/.+\\.zip");
    }

    @Override
    public void handle(Archive archive, Entry entry, ContentPackage2FeatureModelConverter converter) throws Exception {
        logger.info("Processing sub-content package '{}'...", entry.getName());

        Archive subArchive = new ZipStreamArchive(archive.openInputStream(entry));
        converter.process(subArchive);
    }

}
