/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.plugins.javascript.jshint;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.plugins.javascript.base.JavaScriptExtension;
import org.gradle.plugins.javascript.rhino.RhinoExtension;
import org.gradle.plugins.javascript.rhino.RhinoPlugin;

import java.io.File;
import java.util.concurrent.Callable;

public class JsHintPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(RhinoPlugin.class);
        project.getPluginManager().apply(ReportingBasePlugin.class);

        JavaScriptExtension jsExtension = project.getExtensions().getByType(JavaScriptExtension.class);
        final JsHintExtension jsHintExtension = ((ExtensionAware) jsExtension).getExtensions().create(JsHintExtension.NAME, JsHintExtension.class);
        final Configuration configuration = addConfiguration(project.getConfigurations(), project.getDependencies(), jsHintExtension);

        ConventionMapping conventionMapping = ((IConventionAware) jsHintExtension).getConventionMapping();
        conventionMapping.map("js", (Callable<Configuration>) () -> configuration);
        conventionMapping.map("version", (Callable<String>) () -> JsHintExtension.DEFAULT_DEPENDENCY_VERSION);

        final RhinoExtension rhinoExtension = ((ExtensionAware) jsExtension).getExtensions().getByType(RhinoExtension.class);
        final ReportingExtension reportingExtension = project.getExtensions().getByType(ReportingExtension.class);

        project.getTasks().withType(JsHint.class, task -> {
            task.getConventionMapping().map("rhinoClasspath", (Callable<FileCollection>) () -> rhinoExtension.getClasspath());
            task.getConventionMapping().map("jsHint", (Callable<FileCollection>) () -> jsHintExtension.getJs());
            task.getConventionMapping().map("jsonReport", (Callable<File>) () -> reportingExtension.file(task.getName() + "/report.json"));
        });
    }

    public Configuration addConfiguration(ConfigurationContainer configurations, final DependencyHandler dependencies, final JsHintExtension extension) {
        Configuration configuration = configurations.create(JsHintExtension.CONFIGURATION_NAME);
        configuration.defaultDependencies(configDependencies -> {
            String notation = JsHintExtension.DEFAULT_DEPENDENCY_GROUP + ":" + JsHintExtension.DEFAULT_DEPENDENCY_MODULE + ":" + extension.getVersion() + "@js";
            Dependency dependency = dependencies.create(notation);
            configDependencies.add(dependency);
        });
        return configuration;
    }
}
