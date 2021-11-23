package me.peruma.plugins.poc.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

public class MyCustomSmellInspectionProvider implements InspectionToolProvider {
    /**
     * Query method for inspection tools provided by a plugin.
     *
     * @return classes that extend {@link InspectionProfileEntry}
     */
    @Override
    public Class<? extends LocalInspectionTool> @NotNull [] getInspectionClasses() {
        return new Class[]{
                PrintInspection.class
        };
    }
}
