## 1. 영상에서 정리한 내용

### 1-1. HTTP 요청과 응답

- 요청: 주소(`localhost`) + 포트(`8080`) + 경로(`/api/v4/items`) + 메서드(GET/POST/PUT/DELETE), 필요하면 헤더와 본문(body)
- 응답: 상태 코드(예: 200 OK) + 헤더 + 본문(body)

### 1-2. 서버로 데이터를 전달하는 방법 4가지

- 경로: `@PathVariable` (예: `/api/v4/items/1`)
- 쿼리: `@RequestParam` (예: `/api/v4/items/search?keyword=연필`)
- 본문: `@RequestBody` (예: `{"name":"연필","price":500}`)
- 헤더: `@RequestHeader` (예: `X-USER-ID: 7`)

### 1-3. 사용한 어노테이션

- `@RestController`: 웹 요청을 처리하고 JSON으로 응답하는 클래스
- `@RequestMapping("/api/v4/items")`: 클래스 공통 주소
- `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`: 해당 경로에 그 **메서드 방식**으로 접근하면 실행됨
- `ResponseEntity`: 상태 코드와 본문을 직접 정해서 응답

## 2. 직접 추가·수정한 API

기본 주소: `/api/v4/items`

예제(v3)에 있던 API(`POST /with-header`, `GET /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `GET /search`)는
`ItemController4`에 그대로 포함했고, 아래는 **새로 추가하거나 고친 것**입니다.
(전체: POST 2, GET 2, PUT 2, DELETE 2)

수정
- POST `/` : 예제는 성공 시 200이었는데 **201**로 바꾸고, name/price가 없으면 **400** 추가

추가
- PUT `/{id}/price` : 가격만 수정 (성공 200, price 없음/음수 400, 없는 id 404)
- DELETE `/` : 전체 삭제 (성공 204)
- GET `/debug/error` : 500 응답 확인용 (테스트용)
- GET `/debug/unavailable` : 503 응답 확인용 (테스트용)

## 3. 사용한 응답 코드

- 2xx: 200(조회/수정), 201(생성), 204(삭제, 본문 없음)
- 4xx: 400(name/price 누락, price 음수), 404(없는 id)
- 5xx: 500, 503 (테스트용 API)

## 4. 미들웨어 (요청 로그)

- `LoggingInterceptor`: 요청이 들어오면 `메서드 + 주소`를, 처리가 끝나면 응답 `status`를 콘솔에 출력
- `WebConfig`: 인터셉터를 `/api/**`에 등록