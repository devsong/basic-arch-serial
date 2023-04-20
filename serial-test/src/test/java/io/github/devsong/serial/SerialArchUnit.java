package io.github.devsong.serial;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import io.github.devsong.base.test.ArchUnit;

@AnalyzeClasses(packages = SerialConstants.SYSTEM_PREFIX, importOptions = {ImportOption.OnlyIncludeTests.class})
class SerialArchUnit extends ArchUnit {
    static String CONTROLLER_PACKAGE_LAYER = SerialConstants.SYSTEM_PREFIX + ".api.controller";
    static String SERVICE_PACKAGE_LAYER = SerialConstants.SYSTEM_PREFIX + ".service..";
    static String MAPPER_PACKAGE_LAYER = SerialConstants.SYSTEM_PREFIX + ".mapper";

    static String CONTROLLER = "controller", SERVICE = "service", MAPPER = "mapper";

//    @ArchTest
//    protected final ArchRule layer_dependencies_are_respected = layeredArchitecture()
//            .layer(CONTROLLER).definedBy(CONTROLLER_PACKAGE_LAYER)
//            .layer(SERVICE).definedBy(SERVICE_PACKAGE_LAYER)
//            .layer(MAPPER).definedBy(MAPPER_PACKAGE_LAYER)
//            .whereLayer(CONTROLLER).mayNotBeAccessedByAnyLayer()
//            .whereLayer(SERVICE).mayOnlyBeAccessedByLayers(SERVICE, CONTROLLER)
//            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Representation")
//    ;
}
