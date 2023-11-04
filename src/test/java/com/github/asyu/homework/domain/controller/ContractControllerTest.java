package com.github.asyu.homework.domain.controller;

import static com.github.asyu.homework.common.SpringProfiles.TEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.asyu.homework.common.enums.ErrorCode;
import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.dto.ContractDto.Post;
import com.github.asyu.homework.domain.enums.ContractStatus;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.repository.CoverageRepository;
import com.github.asyu.homework.domain.utils.PremiumCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

@SqlGroup(
    value = {
        @Sql(value = "/sql/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    }
)
@ActiveProfiles(TEST)
@AutoConfigureMockMvc
@SpringBootTest
class ContractControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CoverageRepository coverageRepository;

  @DisplayName("새로운 계약을 생성한다 -> 성공")
  @Test
  void save_success() throws Exception {
    // Given
    ContractDto.Post request = new Post(
        LocalDate.now().plusDays(1),
        3,
        1L,
        List.of(1L, 2L)
    );

    // When & Then
    mockMvc.perform(post("/apis/v1/contracts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.startDate").value(request.startDate().toString()))
        .andExpect(jsonPath("$.endDate").value(request.startDate().plusMonths(request.durationInMonths()).toString()))
        .andExpect(jsonPath("$.durationInMonths").value(request.durationInMonths()))
        .andExpect(jsonPath("$.totalPremium").exists())
        .andExpect(jsonPath("$.status").value(ContractStatus.NORMAL.name()))
        .andExpect(jsonPath("$.product.id").value(request.productId()))
        .andExpect(jsonPath("$.coverages[0].id").value(request.coverageIds().get(0)))
        .andExpect(jsonPath("$.coverages[1].id").value(request.coverageIds().get(1)))
    ;
  }

  @Sql("/sql/dummy.sql")
  @DisplayName("새로운 계약을 생성한다 -> 실패 (잘못된 parameter)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void save_failure_cause_invalid_parameter(String ignoredCause, ContractDto.Post request) throws Exception {
    // Given

    // When & Then
    mockMvc.perform(post("/apis/v1/contracts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getCode()))
    ;
  }

  private static Stream<Arguments> save_failure_cause_invalid_parameter() {
    return Stream.of(
        Arguments.of("StartDate before today", new Post(LocalDate.now().minusDays(1), 3, 1L, List.of(1L, 2L))),
        Arguments.of("Null durationInMonths", new Post(LocalDate.now(), null, 1L, List.of(1L, 2L))),
        Arguments.of("Negative Duration", new Post(LocalDate.now().minusDays(1), -1, 1L, List.of(1L, 2L))),
        Arguments.of("Null productId", new Post(LocalDate.now(), 1, null, List.of(1L, 2L))),
        Arguments.of("Null coverageIds", new Post(LocalDate.now(), 1, 1L, null)),
        Arguments.of("Empty coverageIds", new Post(LocalDate.now(), 1, 1L, Collections.emptyList()))
    );
  }

  @DisplayName("새로운 계약을 생성한다 -> 실패 (잘못된 parameter -> 비즈니스 로직에서 Validation)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void save_failure_cause_invalid_parameter_in_service(String ignoredCause, ContractDto.Post request, ErrorCode expected) throws Exception {
    // Given

    // When & Then
    mockMvc.perform(post("/apis/v1/contracts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.code").value(expected.getCode()))
    ;
  }

  private static Stream<Arguments> save_failure_cause_invalid_parameter_in_service() {
    return Stream.of(
        Arguments.of("Duration out of range", new Post(LocalDate.now(), 4, 1L, List.of(1L, 2L)), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Invalid Coverage Ids -> Not Exists In Product", new Post(LocalDate.now(), 3, 1L, List.of(3L)), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Not Exists Product", new Post(LocalDate.now(), 3, 3L, List.of(5L)), ErrorCode.NOT_FOUND)
    );
  }

  @DisplayName("계약을 단건 조회한다 -> 성공")
  @Test
  void get_contract_success() throws Exception {
    // Given
    Long id = 10_000L;

    // When & Then
    mockMvc.perform(get("/apis/v1/contracts/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.startDate").value("2023-11-03"))
        .andExpect(jsonPath("$.endDate").value("2023-12-03"))
        .andExpect(jsonPath("$.durationInMonths").value(1))
        .andExpect(jsonPath("$.totalPremium").value(15000.00))
        .andExpect(jsonPath("$.status").value(ContractStatus.NORMAL.name()))
        .andExpect(jsonPath("$.product.id").value(1))
        .andExpect(jsonPath("$.coverages[0].id").value(1))
        .andExpect(jsonPath("$.coverages[1].id").value(2))
    ;
  }

  @Sql("/sql/dummy.sql")
  @DisplayName("계약을 단건 조회한다 -> 실패 (존재하지 않는 계약)")
  @Test
  void get_contract_failure_not_exists() throws Exception {
    // Given
    Long id = 10_001L;

    // When & Then
    mockMvc.perform(get("/apis/v1/contracts/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.getCode()))
    ;
  }

  @DisplayName("계약 정보를 변경 한다. -> 성공")
  @Test
  void patch_success() throws Exception {
    // Given
    Long contractId = 10000L;
    LocalDate startDate = LocalDate.of(2023, 11, 3);
    ContractStatus expectedUpdateStatus = ContractStatus.EXPIRED;
    ContractDto.Patch request = new ContractDto.Patch(
        3,
        List.of(2L),
        expectedUpdateStatus
    );

    // When & Then
    mockMvc.perform(patch("/apis/v1/contracts/{id}", contractId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.startDate").value(startDate.toString()))
        .andExpect(jsonPath("$.endDate").value(startDate.plusMonths(request.durationInMonths()).toString()))
        .andExpect(jsonPath("$.durationInMonths").value(request.durationInMonths()))
        .andExpect(jsonPath("$.totalPremium").exists())
        .andExpect(jsonPath("$.status").value(expectedUpdateStatus.name()))
        .andExpect(jsonPath("$.product.id").value(1L))
        .andExpect(jsonPath("$.coverages.size()").value(1))
        .andExpect(jsonPath("$.coverages[0].id").value(request.coverageIds().get(0)))
    ;
  }

  @Sql("/sql/dummy.sql")
  @DisplayName("계약 정보를 변경 한다. -> 실패 (잘못된 parameter)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void patch_failure_cause_invalid_parameter(String ignoredCause, ContractDto.Patch request) throws Exception {
    // Given
    Long contractId = 10000L;

    // When & Then
    mockMvc.perform(patch("/apis/v1/contracts/{id}", contractId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getCode()))
    ;
  }

  private static Stream<Arguments> patch_failure_cause_invalid_parameter() {
    return Stream.of(
        Arguments.of("Null durationInMonths", new ContractDto.Patch(null, List.of(1L, 2L), ContractStatus.NORMAL)),
        Arguments.of("Negative Duration", new ContractDto.Patch(-1, List.of(1L, 2L), ContractStatus.NORMAL)),
        Arguments.of("Zero Duration", new ContractDto.Patch(0, List.of(1L, 2L), ContractStatus.NORMAL)),
        Arguments.of("Null coverageIds", new ContractDto.Patch(1, null, ContractStatus.NORMAL)),
        Arguments.of("Empty coverageIds", new ContractDto.Patch(1, Collections.emptyList(), ContractStatus.NORMAL)),
        Arguments.of("Null status", new ContractDto.Patch(1, List.of(1L, 2L), null))
    );
  }

  @DisplayName("계약 정보를 변경 한다. -> 실패 (잘못된 parameter -> 비즈니스 로직에서 Validation)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void patch_failure_cause_invalid_parameter_in_service(String ignoredCause, Long contractId, ContractDto.Patch request, ErrorCode expected) throws Exception {
    // Given

    // When & Then
    mockMvc.perform(patch("/apis/v1/contracts/{id}", contractId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.code").value(expected.getCode()))
    ;
  }

  private static Stream<Arguments> patch_failure_cause_invalid_parameter_in_service() {
    return Stream.of(
        Arguments.of("Expired Contract cannot be modifed.", 10001L, new ContractDto.Patch(3, List.of(2L), ContractStatus.NORMAL), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Duration out of range", 10000L, new ContractDto.Patch(12, List.of(2L), ContractStatus.NORMAL), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Invalid Coverage Ids", 10000L, new ContractDto.Patch(3, List.of(99L, 100L), ContractStatus.NORMAL), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Not Exists Product", 10002L, new ContractDto.Patch(3, List.of(1L), ContractStatus.NORMAL), ErrorCode.NOT_FOUND)
    );
  }

  @DisplayName("예상 총 보험료를 조회한다. -> 성공")
  @Test
  void get_expected_total_premium_success() throws Exception {
    // Given
    List<Long> coverageIds = List.of(1L, 2L);
    Integer contractDuration = 3;
    ContractDto.TotalPremiumCriteria request = new ContractDto.TotalPremiumCriteria(
        contractDuration,
        1L,
        coverageIds
    );

    List<Coverage> coverages = coverageRepository.findByIdIn(coverageIds);
    BigDecimal expectedTotalPremium = PremiumCalculator.calculateTotalPremium(coverages, contractDuration);

    // When & Then
    mockMvc.perform(get("/apis/v1/contracts/expected-total-premium")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPremium").exists())
        .andExpect(jsonPath("$.totalPremium").value(expectedTotalPremium.doubleValue()))
    ;
  }

  @Sql("/sql/dummy.sql")
  @DisplayName("예상 총 보험료를 조회한다. -> 실패 (잘못된 parameter)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void get_expected_total_premium_failure_cause_invalid_parameter(String ignoredCause, ContractDto.TotalPremiumCriteria criteria) throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/apis/v1/contracts/expected-total-premium")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(criteria))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getCode()))
    ;
  }

  private static Stream<Arguments> get_expected_total_premium_failure_cause_invalid_parameter() {
    return Stream.of(
        Arguments.of("Null durationInMonths", new ContractDto.TotalPremiumCriteria(null, 1L, List.of(1L, 2L))),
        Arguments.of("Negative Duration", new ContractDto.TotalPremiumCriteria(-1, 1L, List.of(1L, 2L))),
        Arguments.of("Zero Duration", new ContractDto.TotalPremiumCriteria(0, 1L, List.of(1L, 2L))),
        Arguments.of("Null productId", new ContractDto.TotalPremiumCriteria(3, null, List.of(1L, 2L))),
        Arguments.of("Null coverageIds", new ContractDto.TotalPremiumCriteria(3, 1L, null)),
        Arguments.of("Empty coverageIds", new ContractDto.TotalPremiumCriteria(3, 1L, Collections.emptyList()))
    );
  }

  @DisplayName("예상 총 보험료를 조회한다. -> 실패 (잘못된 parameter -> 비즈니스 로직에서 Validation)")
  @MethodSource
  @ParameterizedTest(name = "Cause : {0}")
  void get_expected_total_premium_failure_cause_invalid_parameter_in_service(String ignoredCause, ContractDto.TotalPremiumCriteria criteria, ErrorCode expected) throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/apis/v1/contracts/expected-total-premium")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(criteria))
        )
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.code").value(expected.getCode()))
    ;
  }

  private static Stream<Arguments> get_expected_total_premium_failure_cause_invalid_parameter_in_service() {
    return Stream.of(
        Arguments.of("Duration out of range", new ContractDto.TotalPremiumCriteria(4, 1L, List.of(1L, 2L)), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Invalid Coverage Ids -> Not Exists In Product", new ContractDto.TotalPremiumCriteria(3, 1L, List.of(3L)), ErrorCode.INVALID_PARAMETER_BUSINESS),
        Arguments.of("Not Exists Product", new ContractDto.TotalPremiumCriteria(3, 99L, List.of(1L)), ErrorCode.NOT_FOUND)
    );
  }

}
