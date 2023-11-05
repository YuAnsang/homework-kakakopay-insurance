# 카카오페이손해보험 과제평가 (계약관리 시스템)

## Application Profile

- test
    - 테스트 코드가 실행되는 profile입니다.
    - 테스트 코드에서 의존 구성요소를(e.g. 외부 통신, 다른 서비스 등) 사용할 수 없을 때는 **테스트 더블**을 사용하여 테스트합니다.
- default
    - 로컬 환경에서 실행되는 profile입니다.
- 과제 프로젝트이므로 test, default Profile 외에는 지원하지 않습니다.

## 실행 가이드

- test
    - JUnit으로 구성된 테스트 케이스를 실행합니다.
    - RDB는 In-Memory DB를 사용합니다.
    - 아래 명령어를 입력합니다.
      ```shell
      ./gradlew test
      ```
- default
    - 로컬 환경에서 Application을 구동합니다.
    - RDB는 In-Memory DB를 사용합니다. (접속 : [Console](http://localhost:8080/h2-console))
    - 아래 명령어를 입력하여 빌드 및 실행합니다. (프로젝트 root path에서 명령어를 입력한다고 가정합니다.)
      ```shell
      ./gradlew build
      java -jar ./build/libs/homework-kakao-insurance-0.0.1-SNAPSHOT.jar
      ```
    - API Spec은 (http://localhost:8080/docs/index.html)를 통해 확인 가능합니다.

## 프로젝트를 진행하며 신경 썼던점

1. 도메인간 연관 관계 및 layer(package) 구조

- 프로젝트를 진행하다보면 도메인간 연관 관계나 의존 관계를 신경쓰지 않고 개발하다가 낭패를 보는 경우가 많았습니다. (e.g. CustomerService가 OrderService를 참조하고 있다거나)
- 도메인간의 무분별한 참조를 금지하여 각 도메인 별로 비즈니스 로직에 집중하였고, 향후 각 도메인들이 커져서 별도의 서비스로 분리되어야 하는 경우까지 고려하였습니다.

2. Validation

- 보험 도메인에서 보험 계약을 체결할 때, 다양한 조건과 요소들을 고려해야 합니다. 이에 따라, 제대로 된 검증 없이 계약을 체결하는 것은 큰 위험을 뜻합니다.
- 예를 들어, 특정 보험 상품에 가입하기 위해서는 담보 조건들이 제대로 검증되지 않으면, 잘못된 보험 상품 계약이 체결될 수 있습니다. (실제로라면 피보험자의 나이, 건강 상태, 직업 등 고려가 더 되어야 할 것으로 보입니다.) 
- 또한 보험료 계산에 있어서도 올바른 정보가 필요하며, 잘못된 정보 입력 시에는 보험료 차이가 발생할 수 있습니다.
- 따라서, 계약관리 시스템에서는 다양한 검증 로직을 도입하여 이러한 오류나 문제점을 최소화하였습니다.

3. 도메인 단어

- 보험 도메인에서는 '계약', '보험금', '보험료', '계약자', '피보험자' 등 특정한 의미를 가진 용어들이 사용됩니다. 이러한 도메인 특화 단어는 개발자뿐만 아니라 도메인 전문가와의 소통에서도 중요한 역할을 합니다.
- 따라서, 프로젝트를 진행하면서 도메인 특화 언어(ubiquitous language)를 정의하고, 이를 코드, 문서, 대화 등 모든 영역에서 일관되게 사용함으로써 개발팀과 비즈니스 팀 간의 커뮤니케이션 효율성을 높였습니다.
- 보험 도메인에서 흔히 사용하는 단어를 정의 하기 위해 해외의 "glossary of insurance terms"등을 최대한 활용하였습니다.
- 정의된 도메인 단어
    - 담보 : Coverage
    - 상품 : Product
    - 계약 : Contract
    - 보험료 : Premium

4. 안내장 발송 선택 문제에 대한 내용

- 안내장 발송의 대상을 조회 하는 곳과 실제로 이메일을 발송하는 부분의 연관 관계를 끊기 위해 EventPublisher, EventListener를 적용하였습니다.
- 실제 환경에서는 EventPublisher, EventListener는 별도 서드파티로 변경도 가능해보입니다.
- 이메일의 contents의 값을 dynamic하게 변경(replace)등을 해야하는 경우는 EmailEvent의 구조가 달라져야 할수도 있어보입니다.

## Technologies

- Java 17
- SpringBoot 3.1.5
- JPA
- H2
- Spring Rest Docs
- ArchUnit
    - 패키지 및 모듈 접근 제한을 테스트를 위해 사용

## Code Convention

- 기본적으로 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) 를 준수합니다.
- 프로젝트 root에 있는 [intellij-java-google-style.xml](./intellij-java-google-style.xml)를 import하여 사용합니다.

## Package Structure

```
└── source_root
 ├── common
 │ ├── config
 │ ├── dto
 │ │ └── response
 │ ├── entity
 │ ├── enums
 │ └── exception
 ├── domain
 │ ├── controller
 │ ├── dto
 │ ├── enums
 │ ├── implement
 │ ├── mapper
 │ ├── persistence
 │ │ ├── dao
 │ │ ├── entity
 │ │ └── repository
 │ ├── service
 │ └── utils
 └── infra

```

- 기본적인 Domain 기반의 package 구조로 구성합니다.
- source_root : 하위에 크게 common, domain 영역으로 구분합니다.
    - common
        - Application 에서 공통으로 사용하는 class들의 패키지입니다.
    - domain : domain 관련 class들의 패키지입니다. 해당 패키지 하위에 서브 도메인으로 세분화 됩니다. (단, 해당 과제는 Contract가 aggregate로 판단되어 별도 서브도메인 별로 패키지를 나누지는 않았습니다.)
        - 서브 도메인
            - controller
                - 사용자의 요청을 받고 처리하는 역할을 담당합니다.
                - 오로지 요청, 데이터 검증, 응답만을 담당합니다.
            - service
                - 비즈니스 로직을 담당하는 패키지입니다.
                - 프로젝트 볼륨이 크지 않아 따로 Command, Query등으로 구분하지 않고 하나의 Service만 사용합니다.
            - implement
                - 비즈니스 로직을 이루기 위해 도구로서 상세 구현 로직을 갖고 있는 클래스들이 있습니다.
                - service에서 오로지 비즈니스로직을 처리하는 경우 코드 가독성 및 유지보수의 어려움이 있어 해당 Layer를 별도로 추가하였습니다.
                - 현재는 Project의 볼륨이 크지않아 해당 영역은 Optional이지만, 프로젝트가 커지는 경우 Required로 변경하여 비즈니스 로직을 상세 구현 로직으로 분리합니다.
            - persistence
                - 데이터를 영구적으로 저장하고 관리하는 데 사용되는 패키지입니다.
                - Repository, DAO로 나뉘어서 관리합니다.
            - dto
                - 흔히 "데이터 전송 객체"라는 개념으로 사용하지만, 해당 프로젝트에서는 그뿐만이 아니라 "1회성으로 사용하는 모든 객체"를 DTO라는 개념으로 사용하고 있습니다.
                - 최대한 immutable하게 사용합니다. (일부 Spring Framework에서 필수적으로 NoArgs, Getter, Setter가 필요한 클래스들은 예외로 간주합니다.)
            - mapper
                - 객체 매핑을 담당하는 클래스들을 관리하는 패키지입니다. (e.g. Entity <-> DTO)
    - infra : http, email, object storage 기타 등 인프라 전반 통신에 필요한 클래스들이 있습니다.

## Package Convention

- 해당 프로젝트는 좋은 Application 구조와 유지보수성을 위해 package(또는 Layer)간의 무분별한 참조를 지양하고 있습니다.
- layer
    - domain -> infra 접근 금지
    - domain -> common 접근 금지
    - common -> infra 접근 금지
- domain package
    - controller -> service -> implement(Optional) -> persistence(DAO -> repository)순으로 참조하며 역방향 참조를 금지합니다.
    - 같은 level의 참조고 금지합니다.
        - ex) service -> service
        - implement layer에서는 일부 예외를 허용합니다.
    - entity class는 외부로 노출을 금지하며 DTO로 변환하여 리턴합니다.
    - 각 서브 도메인 별로 참조를 금지합니다.
        - 서브 도메인은 언제든지 별도 서비스 또는 모듈등으로 분리가 될 수 있기때문에 확장성을 고려하여 참조를 금지합니다.
        - 서로 통신해야 하는 경우가 생기면 eventually하게 호출하여 최대한 커플링을 낮추는걸 권장합니다.
    - Repository를 단독 접근하지 않고, DAO를 통해 접근합니다.
        - Repository를 구현부에서 직접 사용한 경우 일부 반복적인 코드가 들어 갈 수 있으며, 무분별한 참조를 막기 위해 해당 컨벤션을 지정합니다.
- ArchUnit으로 일부 강제화 되어있습니다.
- 테스트 케이스 영역은 간편한 작성을 위해 위 컨벤션에서 제외됩니다.

## Commit Message Convention

- 커밋 메시지를 기본적으로 아래와 같이 구성합니다.

```
CommitType : subject
(한 줄을 띄워 분리)
contents
```

| Commit Type | Description                        |
|-------------|------------------------------------|
| feat        | 새로운 기능 추가                          |
| refactor    | 일반적으로 코드를 수정하는 경우 또는 리팩터링 하는 경우    | 
| fix         | 버그 및 오류 수정                         |
| test        | 테스트 코드를 수정 또는 리팩터링 하는 경우           |
| style       | 코드(로직)의 변경없이 코드 포맷, 컨밴션 등을 수정하는 경우 |
| chore       | 코드의 수정 없이 설정 등을 변경하는 경우            |
| docs        | 문서를 수정하는 경우                        |
| rename      | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우       |
| remove      | 파일을 삭제하는 작업만 수행한 경우                |

- Contents의 경우 subject로만 설명이 가능한 경우 생략이 가능합니다.
- 별도 이슈 티켓을 관리 안하므로 Commit은 유의미한 단위로 하되 개발자의 판단에 맡깁니다.