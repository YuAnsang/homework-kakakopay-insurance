package com.github.asyu.homework.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.github.asyu.homework", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredDependencyRulesTest {

  @ArchTest
  static final ArchRule controller_should_not_depend_on_entity =
      noClasses().that()
          .resideInAPackage("..domain..controller..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..domain..persistence..entity..");

  @ArchTest
  static final ArchRule common_should_not_access_domain =
      noClasses().that()
          .resideInAPackage("..common..")
          .should()
          .accessClassesThat()
          .resideInAPackage("..domain..");

  @ArchTest
  static final ArchRule common_should_not_access_infra =
      noClasses().that()
          .resideInAPackage("..common..")
          .should()
          .accessClassesThat()
          .resideInAPackage("..infra..");

}
