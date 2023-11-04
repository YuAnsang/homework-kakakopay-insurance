package com.github.asyu.homework.arch;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.github.asyu.homework.domain", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

  @ArchTest
  static final ArchRule layer_dependencies_are_respected = layeredArchitecture().consideringAllDependencies()
      .layer("Controller").definedBy("..controller..")
      .layer("Service").definedBy("..service..")
      .layer("Implement").definedBy("..implement..")
      .layer("Dao").definedBy("..dao..")
      .layer("Repository").definedBy("..repository..")
      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Implement").mayOnlyBeAccessedByLayers("Service", "Implement")
      .whereLayer("Dao").mayOnlyBeAccessedByLayers("Service", "Implement")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Dao")
      ;

}
