/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.tasks.scala;

import org.gradle.api.file.FileTree;
import org.gradle.api.internal.tasks.compile.JavaCompiler;
import org.gradle.api.internal.tasks.compile.JavaCompilerSupport;
import org.gradle.api.internal.tasks.compile.JvmCompileSpec;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.scala.ScalaCompileOptions;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;

public class DefaultScalaJavaJointCompiler extends JavaCompilerSupport implements ScalaJavaJointCompiler {
    private final ScalaCompiler<? extends JvmCompileSpec> scalaCompiler;
    private final JavaCompiler javaCompiler;

    public DefaultScalaJavaJointCompiler(ScalaCompiler<? extends JvmCompileSpec> scalaCompiler, JavaCompiler javaCompiler) {
        this.scalaCompiler = scalaCompiler;
        this.javaCompiler = javaCompiler;
    }

    public ScalaCompileOptions getScalaCompileOptions() {
        return scalaCompiler.getScalaCompileOptions();
    }

    public void setScalaClasspath(Iterable<File> classpath) {
        scalaCompiler.setScalaClasspath(classpath);
    }

    public WorkResult execute() {
        configureScalaCompiler();
        scalaCompiler.execute();

        PatternFilterable patternSet = new PatternSet();
        patternSet.include("**/*.java");
        FileTree javaSource = getSpec().getSource().getAsFileTree().matching(patternSet);
        if (!javaSource.isEmpty()) {
            javaCompiler.setSpec(spec);
            javaCompiler.setSource(javaSource);
            javaCompiler.execute();
        }

        return new WorkResult() {
            public boolean getDidWork() {
                return true;
            }
        };
    }
    
    private void configureScalaCompiler() {
        scalaCompiler.setSource(spec.getSource());
        scalaCompiler.setDestinationDir(spec.getDestinationDir());
        scalaCompiler.setClasspath(spec.getClasspath());
    }
}
