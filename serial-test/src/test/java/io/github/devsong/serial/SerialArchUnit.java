package io.github.devsong.serial;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import io.github.devsong.base.test.ArchUnit;

@AnalyzeClasses(packages = SerialConstants.SYSTEM_PREFIX, importOptions = {ImportOption.OnlyIncludeTests.class})
class SerialArchUnit extends ArchUnit {

//    @ArchTest
//    private final ArchRule test_class_should_be_package_private =
//            classes().that()
//                    .haveSimpleNameEndingWith("Test")
//                    .should()
//                    .bePackagePrivate();
//
//    @ArchTest
//    private final ArchRule test_method_should_be_package_private =
//            methods()
//                    .that()
//                    .haveNameNotStartingWith("validate_")
//                    .and()
//                    .areDeclaredInClassesThat()
//                    .haveSimpleNameEndingWith("Test")
//                    .and()
//                    .areAnnotatedWith(Test.class)
//                    .or()
//                    .areAnnotatedWith(ParameterizedTest.class)
//                    .should()
//                    .bePackagePrivate();

}
