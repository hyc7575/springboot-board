# Spring Boot Board

Spring Boot · Spring Security JWT · Spring Data JPA · H2로 만든 게시글/댓글 REST API입니다.
이 문서는 현재 구현과 2026-09-27 실제 HTTP 호출 결과를 기준으로 작성했습니다.

## 1. 실행 방법

### 준비 사항

- **JDK 25**: `build.gradle.kts`의 Java toolchain 기준입니다.
- Spring Boot **4.1.1**, Gradle Wrapper **9.7.1**을 사용합니다. Gradle 별도 설치는 필요 없습니다.
- 최초 실행에는 Gradle 및 Maven 의존성을 다운로드할 네트워크가 필요합니다.
- 아래 실행 예제는 macOS/Linux 셸 기준입니다. Windows는 `gradlew.bat`를 사용하세요.
- API 재현 예제에는 `curl`, `jq`가 필요합니다.

```bash
java -version
./gradlew --version
```

### DB 준비

별도 DB 서버 설치 없이 H2 파일 DB가 자동 생성됩니다. 기본 프로필은 `local`입니다.

| 항목 | 값 |
|---|---|
| JDBC URL | `jdbc:h2:file:./db_dev` |
| 사용자 / 비밀번호 | `sa` / 빈 문자열 |
| DB 파일 | 프로젝트 실행 디렉터리의 `db_dev.mv.db` |
| 스키마 설정 | `spring.jpa.hibernate.ddl-auto=update` |
| H2 콘솔 | `http://localhost:8080/h2-console/` |

`./gradlew bootRun`에는 개발용 H2 콘솔 모듈이 포함됩니다. 일반 배포 JAR에는 `developmentOnly` 모듈이 포함되지 않습니다.

시작 시 `user1`, `ueer2`, `user3` 샘플 회원을 생성합니다. 이메일은 각 닉네임에 `@mail.com`을 붙이고, 비밀번호는 `1234`를 BCrypt로 저장합니다. 같은 이메일 또는 닉네임이 있으면 건너뜁니다. `ueer2`는 현재 코드의 실제 철자입니다.

기존 DB에서 엔티티 필드명을 변경한 경우 `ddl-auto=update`가 예전 컬럼을 제거하거나 데이터까지 이동해 주지는 않습니다. 예를 들어 과거 `COMMENT NOT NULL` 컬럼이 남고 새 `CONTENT` 컬럼을 쓰면 INSERT가 실패할 수 있습니다. 기존 데이터를 보존하는 스키마 마이그레이션이 필요합니다.

### 실행

JWT 서명 키는 실행 환경에서 덮어쓰는 것을 권장합니다. 현재 코드는 `jwt.secret`의 문자열 바이트를 사용합니다. 아래 명령은 충분한 길이의 임의 키를 만듭니다.

```bash
export JWT_SECRET="$(openssl rand -hex 32)"
./gradlew bootRun
```

재시작 후에도 기존 토큰을 사용하려면 같은 키를 유지하세요. 다른 키로 재시작하면 기존 토큰은 검증되지 않습니다.

- 기본 주소: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

테스트/빌드:

```bash
./gradlew test
./gradlew bootJar
java -jar build/libs/springboot-board-0.0.1-SNAPSHOT.jar
```

테스트를 실행할 때도 기본 프로필을 사용하는 전체 컨텍스트 테스트는 파일 DB에 접근할 수 있습니다. 독립된 DB로 실행하려면:

```bash
SPRING_DATASOURCE_URL='jdbc:h2:mem:board-tests' \
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop ./gradlew test
```

## 2. API 명세

### 공통 규칙

- 요청/응답: `Content-Type: application/json`
- 인증 필요 API: `Authorization: Bearer <accessToken>`
- 공개 API는 토큰을 생략할 수 있습니다. 잘못된 Bearer 토큰을 보내면 공개 경로도 JWT 필터에서 401이 발생할 수 있습니다.
- 모든 현재 성공 응답은 **200 OK**입니다. 생성은 201, 삭제는 204를 사용하지 않습니다.
- 경로는 단수 `post`가 아닌 **`posts`**입니다.
- `{id}`, `{postId}`, `{commentId}`는 DB의 정수 ID입니다.

성공 응답은 `ApiResponse<T>`입니다.

```json
{"success":true,"message":null,"data":{}}
```

아래 표의 응답 열은 `data`에 들어가는 값입니다. 각 엔드포인트의 전체 응답은 위 공통 구조로 감싸집니다.

| 메서드 | 주소 | 인증 | 요청 본문 | 성공 응답 `data` | 주요 상태 코드 |
|---|---|---|---|---|---|
| POST | `/api/v1/members/join` | 불필요 | `{"email":"a@mail.com","password":"1234","nickname":"a"}` | JWT 문자열. `message`는 `회원가입이 완료되었습니다.` | 200, 409 중복 |
| POST | `/api/v1/members/login` | 불필요 | `{"email":"a@mail.com","password":"1234"}` | `{"accessToken":"..."}` | 200, **500 로그인 실패(현재 구현)** |
| GET | `/api/v1/posts` | 불필요 | 없음 | `PostDto[]`, 없으면 `[]` | 200 |
| GET | `/api/v1/posts/{id}` | 불필요 | 없음 | `PostDto` | 200, 404 없음/삭제됨 |
| POST | `/api/v1/posts` | 필요 | `{"title":"제목","content":"내용"}` | `PostDto` | 200, 401, 404 회원 없음 |
| PATCH | `/api/v1/posts/{id}` | 필요·작성자 | `{"title":"새 제목","content":"새 내용"}` | `PostDto` | 200, 400, 401, 403, 404 |
| DELETE | `/api/v1/posts/{id}` | 필요·작성자 | 없음 | `null` | 200, 401, 403, 404 |
| GET | `/api/v1/posts/{postId}/comments` | 불필요 | 없음 | `CommentDto[]`, 없으면 `[]` | 200 |
| POST | `/api/v1/posts/{postId}/comments` | 필요 | `{"content":"댓글","parentId":null}` | `CommentDto` | 200, 400, 401, 404 |
| PATCH | `/api/v1/posts/{postId}/comments/{commentId}` | 필요·작성자 | `{"content":"새 댓글"}` | `CommentDto` | 200, 400, 401, 403, 404 |
| DELETE | `/api/v1/posts/{postId}/comments/{commentId}` | 필요·작성자 | 없음 | `null` | 200, 401, 403, 404 |

공통으로 잘못된 JSON·경로 변수는 400, 내부 처리 오류는 500이 발생할 수 있습니다. 토큰 누락·유효하지 않은 토큰은 보호 API에서 401입니다.

**요청 처리 세부사항**

- 게시글 PATCH는 제목과 내용을 **둘 다 필수**로 받습니다. 생략한 필드를 유지하는 부분 업데이트는 구현하지 않았습니다.
- 댓글 작성/수정의 `content`, 게시글 수정의 `title`/`content`는 `@NotBlank`로 검증합니다.
- 댓글의 `parentId`는 **부모 댓글 ID**입니다. 생략하거나 `null`이면 일반 댓글입니다. 부모 댓글은 같은 게시글의 삭제되지 않은 댓글이어야 하며, 아니면 404입니다.
- 작성자 ID는 요청 본문에서 받지 않습니다. JWT의 `sub`에서 가져옵니다.
- 댓글 작성 시 없거나 삭제된 회원은 401, 없거나 삭제된 게시글은 404입니다.
- 댓글 수정/삭제는 URL의 게시글과 댓글이 일치해야 합니다. 다른 게시글의 댓글, 삭제된 댓글/게시글이면 404, 다른 작성자이면 403입니다.
- 댓글 목록은 없는/삭제된 게시글에도 빈 배열을 반환합니다. 게시글 존재를 별도로 검증하지 않습니다.
- 회원 가입/로그인은 DTO에 검증 애너테이션이 있어도 컨트롤러에 `@Valid`가 없어 입력 검증이 적용되지 않습니다. 게시글 생성도 별도 Bean Validation을 적용하지 않았습니다.

**PostDto**

```json
{"id":1,"memberId":4,"title":"제목","content":"내용","createdAt":"2026-09-27T03:40:40.544109","deletedAt":null}
```

**CommentDto**

```json
{"id":1,"memberId":4,"content":"댓글","parentId":null}
```

게시글 생성 시간은 JPA Auditing으로 채웁니다. 댓글에도 생성 시간을 저장하지만 현재 `CommentDto`에는 노출하지 않습니다.

삭제 성공 응답:

```json
{"success":true,"message":null,"data":null}
```

### 오류 응답의 실제 모양

현재 `ApiResponse.fail()`은 정의만 되어 있으며, 공통 예외 처리기에 연결되어 있지 않습니다. **성공과 오류의 응답 형식이 다릅니다.**

1. JWT 인증 필터의 401: 본문이 없고 `WWW-Authenticate: Bearer ...` 헤더가 반환됩니다.
2. 서비스의 403·404·409, 요청 검증의 400: Spring Boot 기본 오류 JSON을 반환합니다.
3. 로그인에서 이메일 없음/비밀번호 불일치는 `IllegalArgumentException`으로 처리되어 현재 **500**입니다. 401 매핑은 개선할 부분입니다.

로컬 `bootRun`의 DevTools 설정에서는 `message`, `trace`, 경우에 따라 `errors`도 포함됩니다. 아래는 실제 403에서 긴 `trace`만 생략한 본문입니다.

```json
{
  "timestamp": "2026-09-26T18:40:40.682Z",
  "status": 403,
  "error": "Forbidden",
  "message": "작성자만 수정하거나 삭제할 수 있습니다.",
  "path": "/api/v1/posts/1"
}
```

## 3. 설계 설명

### 로그인 방식

API 호출마다 Bearer JWT로 인증하는 방식을 사용합니다. 서버가 로그인 세션을 저장하지 않고, 클라이언트가 받은 토큰을 다음 요청에 붙이는 REST API 흐름에 맞췄습니다.

- 가입 시 BCrypt로 비밀번호를 해시해 저장합니다.
- 로그인 시 이메일로 회원을 조회하고 BCrypt로 비밀번호를 검증합니다.
- 토큰은 HMAC 공유 키로 서명하고 Spring Security Resource Server가 검증합니다.
- `sub`: 회원 ID, `email`: 회원 이메일, `iat`: 발급 시각, `exp`: 만료 시각입니다.
- 유효기간은 **1시간**입니다. `SessionCreationPolicy.STATELESS`를 사용합니다.
- Refresh Token, 로그아웃 시 토큰 폐기, `issuer`/`audience` 검증은 현재 구현하지 않았습니다.
- CSRF는 현재 비활성화되어 있습니다. 이 API는 인증 쿠키 대신 명시적인 Authorization 헤더를 사용하는 전제입니다.
- `DispatcherType.ERROR`는 허용하여 내부 오류 응답이 다시 인증에 막혀 401로 바뀌지 않도록 합니다.

JWT 인증 후에도 서비스에서 작성자 ID를 비교하므로 다른 회원의 게시글·댓글을 수정/삭제할 수 없습니다. 단, 현재 로그인은 삭제된 회원을 별도로 거부하지 않고, 모든 API가 회원 활성 상태를 재확인하는 것도 아닙니다. 계정 삭제 시 토큰 무효화 정책은 추가 구현이 필요합니다.

### 엔티티 관계

```text
Member.id ── Post.memberId       (정수 ID, JPA 연관관계 없음)
Member.id ── Comment.memberId    (정수 ID, JPA 연관관계 없음)
Post      1 ── N Comment.post   (@ManyToOne LAZY, post_id)
Comment   1 ── N 자식 댓글      (자식의 parentComment, @ManyToOne LAZY)
```

- 댓글은 게시글 `Post`를 참조하고 DB에는 `post_id` 외래 키로 저장합니다.
- 일반 댓글의 `parentComment`는 `null`, 대댓글은 부모 댓글을 참조합니다.
- 부모 댓글 컬럼명은 현재 코드 그대로 `parent_coment_id`입니다.
- `Post`에 댓글 컬렉션을 두지 않은 단방향 관계입니다. cascade remove는 설정하지 않았습니다.
- API에는 엔티티 대신 DTO를 반환하여 연관 엔티티 전체나 순환 참조가 직렬화되지 않도록 했습니다.

### N+1 대응과 현재 범위

현재는 fetch join, `@EntityGraph`, batch fetch 설정을 사용하지 않습니다. 대신 목록 응답이 연관 엔티티의 상세 데이터를 순회하지 않게 구성했습니다.

- 게시글 목록: `memberId`가 단순 값이므로 회원을 글마다 추가 조회하지 않습니다.
- 댓글 목록: `post`, `parentComment`는 LAZY이며 DTO에는 댓글 자체의 값과 부모의 **ID만** 담습니다. 현재 Hibernate 매핑에서는 프록시의 ID 조회가 부모 엔티티 초기화를 요구하지 않습니다.
- 부모 내용이나 작성자 닉네임 등 연관 엔티티의 필드를 DTO에 추가하면 N+1이 생길 수 있으므로 그때 fetch join/DTO projection 등의 조회 설계가 필요합니다. **LAZY 자체가 N+1 해결책은 아닙니다.**

검증 시 H2에서 영속성 컨텍스트를 `flush/clear`한 뒤 Hibernate 통계를 측정했습니다. 서로 다른 부모 댓글 3개와 대댓글 3개를 만들고, 부모는 소프트 삭제하여 목록에 포함되지 않게 했습니다. 댓글 목록 3개 반환에 SELECT **1회**, 게시글 목록에도 SELECT **1회**였습니다. 이는 현재 DTO 필드 기준의 측정이며 모든 향후 조회에 대한 보장은 아닙니다.

### 글 삭제 시 댓글 처리

- 게시글 삭제는 실제 DELETE가 아닌 `deletedAt` 기록입니다.
- 연결된 댓글 행과 댓글의 `deletedAt`은 함께 변경하지 않습니다.
- 댓글 목록 쿼리가 `comment.deletedAt IS NULL`과 `post.deletedAt IS NULL`을 함께 확인하므로, 삭제된 글의 댓글은 목록에서 보이지 않습니다.
- 삭제된 글에는 댓글 작성·수정·삭제를 할 수 없습니다.
- 댓글 자체를 삭제할 때도 `deletedAt`만 기록합니다. 부모 댓글을 삭제해도 기존 대댓글은 남고 조회될 수 있으며, `parentId`는 유지됩니다. 삭제된 부모에 새 대댓글을 작성하는 것은 막습니다.

## 4. 실제 실행 결과

2026-09-27, 임시 메모리 DB와 포트 18083에서 실제 HTTP 요청으로 검증했습니다. 기존 파일 DB는 사용하지 않았습니다. 아래 curl은 동일 요청을 재현하기 위한 명령입니다.

```bash
# 터미널 1: 독립된 테스트 서버 실행
export JWT_SECRET="$(openssl rand -hex 32)"
./gradlew bootRun --args='--server.port=18083 --server.address=127.0.0.1 --spring.datasource.url=jdbc:h2:mem:readme-verification --spring.jpa.hibernate.ddl-auto=create-drop'

# 터미널 2
BASE=http://127.0.0.1:18083
```

토큰 문자열만 `<ACCESS_TOKEN>`으로 가렸습니다. ID·시각은 실제 실행값이며 실행할 때마다 달라집니다. 성공 결과의 작성자 ID가 4인 이유는 샘플 회원 3개가 먼저 생성되었기 때문입니다. 오류 응답의 긴 `trace`는 가독성을 위해 생략 표시합니다.

### 1. 가입

```bash
JOIN=$(curl -sS "$BASE/api/v1/members/join" -H 'Content-Type: application/json' \
  -d '{"email":"readme-owner@mail.com","password":"1234","nickname":"readme-owner"}')
printf '%s\n' "$JOIN" | jq
```

HTTP **200**

```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": "<ACCESS_TOKEN>"
}
```

### 2. 로그인

```bash
LOGIN=$(curl -sS "$BASE/api/v1/members/login" -H 'Content-Type: application/json' \
  -d '{"email":"readme-owner@mail.com","password":"1234"}')
printf '%s\n' "$LOGIN" | jq
TOKEN=$(printf '%s' "$LOGIN" | jq -r '.data.accessToken')
```

HTTP **200**

```json
{
  "success": true,
  "message": null,
  "data": {
    "accessToken": "<ACCESS_TOKEN>"
  }
}
```

### 3. 글 쓰기

```bash
POST=$(curl -sS "$BASE/api/v1/posts" \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"README 게시글","content":"API 실행 확인"}')
printf '%s\n' "$POST" | jq
POST_ID=$(printf '%s' "$POST" | jq -r '.data.id')
```

HTTP **200**

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "memberId": 4,
    "title": "README 게시글",
    "content": "API 실행 확인",
    "createdAt": "2026-09-27T03:40:40.544109",
    "deletedAt": null
  }
}
```

### 4. 댓글 쓰기

```bash
COMMENT=$(curl -sS "$BASE/api/v1/posts/$POST_ID/comments" \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"content":"첫 댓글입니다","parentId":null}')
printf '%s\n' "$COMMENT" | jq
COMMENT_ID=$(printf '%s' "$COMMENT" | jq -r '.data.id')
```

HTTP **200**

```json
{
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "memberId": 4,
    "content": "첫 댓글입니다",
    "parentId": null
  }
}
```

### 5. 게시글 목록 조회

```bash
curl -sS "$BASE/api/v1/posts" | jq
```

HTTP **200**

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "memberId": 4,
      "title": "README 게시글",
      "content": "API 실행 확인",
      "createdAt": "2026-09-27T03:40:40.544109",
      "deletedAt": null
    }
  ]
}
```

### 6. 댓글 목록 조회

```bash
curl -sS "$BASE/api/v1/posts/$POST_ID/comments" | jq
```

HTTP **200**

```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "id": 1,
      "memberId": 4,
      "content": "첫 댓글입니다",
      "parentId": null
    }
  ]
}
```

### 7. 인증 없이 글 쓰기 → 401

```bash
curl -i "$BASE/api/v1/posts" -H 'Content-Type: application/json' \
  -d '{"title":"인증 없음","content":"실패 확인"}'
```

```http
HTTP/1.1 401
WWW-Authenticate: Bearer resource_metadata="http://127.0.0.1:18083/.well-known/oauth-protected-resource"
Content-Length: 0
```

응답 본문 없음.

### 8. 다른 회원 가입

```bash
OTHER_JOIN=$(curl -sS "$BASE/api/v1/members/join" -H 'Content-Type: application/json' \
  -d '{"email":"readme-other@mail.com","password":"1234","nickname":"readme-other"}')
printf '%s\n' "$OTHER_JOIN" | jq
OTHER_TOKEN=$(printf '%s' "$OTHER_JOIN" | jq -r '.data')
```

HTTP **200**

```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": "<ACCESS_TOKEN>"
}
```

### 9. 다른 작성자의 글 수정 → 403

```bash
curl -i -X PATCH "$BASE/api/v1/posts/$POST_ID" \
  -H "Authorization: Bearer $OTHER_TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"다른 사람 수정","content":"실패 확인"}'
```

HTTP **403**

```json
{
  "timestamp": "2026-09-26T18:40:40.682Z",
  "status": 403,
  "error": "Forbidden",
  "trace": "<실제 스택 트레이스 생략>",
  "message": "작성자만 수정하거나 삭제할 수 있습니다.",
  "path": "/api/v1/posts/1"
}
```

### 추가 검증 결과

동일 서버에서 다음 동작도 실제 호출했습니다.

| 호출 | 결과 |
|---|---|
| 작성자 게시글 PATCH | 200, 수정된 제목·내용 반환 |
| 작성자 댓글 PATCH | 200, 수정된 내용 반환 |
| `parentId`를 지정한 댓글 POST | 200, 부모 댓글 ID 반환 |
| 작성자 댓글 DELETE | 200, `data: null` |
| 작성자 게시글 DELETE | 200, `data: null` |
| 글 삭제 후 댓글 GET | 200, `data: []` |
| 없는 게시글 GET | 404 |
| 빈 제목으로 게시글 PATCH | 400 |
| 중복 회원 가입 | 409 |
| 잘못된 비밀번호로 로그인 | 500 — 현재 예외 처리의 미완성 부분 |

목록 정렬·페이지네이션은 현재 구현하지 않았습니다. 응답 순서에 의존하지 마세요.

위 독립 DB 명령으로 전체 테스트 **18개 통과**를 확인했습니다.
