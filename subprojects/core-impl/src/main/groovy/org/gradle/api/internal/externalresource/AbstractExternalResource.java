/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.externalresource;

import org.apache.commons.io.IOUtils;

import java.io.*;

public abstract class AbstractExternalResource implements ExternalResource {

    public void writeTo(File destination) throws IOException {
        FileOutputStream output = new FileOutputStream(destination);
        try {
            writeTo(output);
        } finally {
            output.close();
        }
    }

    public void writeTo(OutputStream output) throws IOException {
        InputStream input = openStream();
        try {
            IOUtils.copy(input, output);
        } finally {
            input.close();
        }
    }

    public void close() throws IOException {
    }
}
