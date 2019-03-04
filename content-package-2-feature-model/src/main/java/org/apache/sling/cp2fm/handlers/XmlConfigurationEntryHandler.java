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

import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.felix.utils.properties.ConfigurationHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlConfigurationEntryHandler extends AbstractSingleConfigurationEntryHandler {

    private static final String JCR_ROOT = "jcr:root";

    private static final String JCR_PREFIX = "jcr:";

    private static final String XMLNS_PREFIX = "xmlns:";

    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    public XmlConfigurationEntryHandler() {
        super("[^.][^/]+\\.xml");
    }

    @Override
    protected Dictionary<String, Object> parseConfiguration(InputStream input) throws Exception {
        SAXParser saxParser = saxParserFactory.newSAXParser();
        JcrConfigurationHandler configurationHandler = new JcrConfigurationHandler();
        saxParser.parse(input, configurationHandler);
        return configurationHandler.getConfiguration();
    }

    private static final class JcrConfigurationHandler extends DefaultHandler {

        private final Dictionary<String, Object> configuration = new Hashtable<>();

        public Dictionary<String, Object> getConfiguration() {
            return configuration;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            String primaryType = attributes.getValue(JCR_PRIMARYTYPE);

            if (JCR_ROOT.equals(qName) && primaryType != null && !primaryType.isEmpty()) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    String attributeQName = attributes.getQName(i);

                    if (!attributeQName.startsWith(JCR_PREFIX) && !attributeQName.startsWith(XMLNS_PREFIX)) {
                        String attributeValue = attributes.getValue(i);

                        configuration.put(attributeQName, attributeValue);
                    }
                }
            }
        }

    }

}
