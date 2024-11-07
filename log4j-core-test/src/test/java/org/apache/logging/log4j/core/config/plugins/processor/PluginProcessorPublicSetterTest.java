package org.apache.logging.log4j.core.config.plugins.processor;


import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class PluginProcessorPublicSetterTest {

    private static final String FAKE_PLUGIN_CLASS_PATH =
            "src/test/java/org/apache/logging/log4j/core/config/plugins/processor/" + FakePlugin.class.getSimpleName() + ".java";

    @Test
    public void warnWhenPluginBuilderAttributeLacksPublicSetter() throws Exception {
        // Instantiate the tooling
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.ROOT, UTF_8);


        // Get the source files
        Path sourceFile = Paths.get(FAKE_PLUGIN_CLASS_PATH);

        assertThat(Files.exists(sourceFile)).isTrue();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());

        // Compile the sources with the plugin processor
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(
                        "-proc:only",
                        "-processor",
                        PluginProcessor.class.getName()),
                null,
                compilationUnits);
        task.call();

        // Check for warnings about missing public setter
        List<Diagnostic<? extends JavaFileObject>> warningDiagnostics = diagnosticCollector.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.WARNING)
                .collect(Collectors.toList());

        assertThat(warningDiagnostics).anyMatch(warningMessage -> warningMessage.getMessage(Locale.ROOT).contains("The field `attribute` does not have a public setter"));
    }

    @Test
    public void IgnoreWarningWhenSuppressWarningsIsPresent() throws Exception {
        // Instantiate the tooling
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.ROOT, UTF_8);


        // Get the source files
        Path sourceFile = Paths.get(FAKE_PLUGIN_CLASS_PATH);
        System.out.println(FAKE_PLUGIN_CLASS_PATH);
        assertThat(Files.exists(sourceFile)).isTrue();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());

        // Compile the sources with the plugin processor
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(
                        "-proc:only",
                        "-processor",
                        PluginProcessor.class.getName()),
                null,
                compilationUnits);
        task.call();

        // Check for warnings about missing public setter
        List<Diagnostic<? extends JavaFileObject>> warningDiagnostics = diagnosticCollector.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.WARNING)
                .collect(Collectors.toList());

        assertThat(warningDiagnostics).allMatch(warningMessage -> !warningMessage.getMessage(Locale.ROOT).contains("The field `attributeWithoutPublicSetterButWithSuppressAnnotation` does not have a public setter"));
    }


}
