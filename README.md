# 원티드 프리온보딩 사전과제

## 지원자의 성명

- 안창범

## 개발 환경

- Language: JAVA 11
- Framework: Springboot 2.7.14
- DB: MySQL 8.0
- Build: Gradle 8.2.1
- Server: AWS EC2
- Deploy: Docker

## 애플리케이션의 실행 방법 (엔드포인트 호출 방법 포함)

### Swagger Docs 배포 주소

- http://ec2-52-79-213-143.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

### 배포 스크립트

```bash
docker build -t wanted-pre-onboarding-backend .

docker run -p 8080:8080 \
-e SPRING_DATASOURCE_PASSWORD=<DB비밀번호> \
-e JWT_TOKEN_SECRET=<JWT Token Secret Key> \
-d wanted-pre-onboarding-backend
```

## 데이터베이스 테이블 구조

![erd](./src/main/resources/static/erd.png)

## 구현한 API의 동작을 촬영한 데모 영상 링크

### 로그인 API 동작 데모 영상

![로그인](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/b4e57659-67ac-459d-a1f0-6b0f02618b73)

### 회원가입 API 동작 데모 영상

![회원가입](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/9e99f48f-b44e-4ebb-afff-642b38b038db)

### 게시글 등록 API 동작 데모 영상

![게시글등록](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/c98bcd2d-4d84-4a8c-b432-495b272d3daf)

### 게시글 조회 API 동작 데모 영상

![게시글조회](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/95a34820-78aa-4893-9fa9-845fedc75815)

### 게시글 수정 API 동작 데모 영상

![게시글수정](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/7474bb74-1243-4fd6-b2ad-63b6851b2610)

### 게시글 삭제 API 동작 데모 영상

![게시글삭제](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/56e450c3-f59c-45f2-9ff7-2861f13de677)

### 게시글 리스트 조회 API 동작 데모 영

![리스트조회](https://github.com/Changbum97/wanted-pre-onboarding-backend/assets/87286719/77a03fa4-20f5-4c28-996b-329fc34711d0)

## 구현 방법 및 이유에 대한 간략한 설명

- RestController, Service, Repository 구조로 구현
- Spring Security를 사용하여 로그인 기능 및 비밀번호 암호화 기능 구현
- JPA를 사용하여 MySQL Database와 연결
- Swagger 3.0을 사용하여 Swagger Docs 생성
- 유저와 게시글은 1:N 관계로 설정
- JPA Auditing 기능을 사용하여 유저, 게시글의 createdAt, lastModifiedAt 자동 갱신
- JUnit5와 Mockito를 사용하여 RestController, Service 관련 Test Code 작성
- Response, Request 시에는 DTO 활용
- Response, ErrorResponse, CustomException 객체를 활용하여 API 통신 시 일정한 format으로 return하도록 구현
- ErrorCode 객체를 활용하여 발생할 수 있는 에러 코드 명시
- Exception Manager을 활용하여 CustomException 발생 시 ErrorResponse로 변환 후 return

## API 명세(request/response 포함)

### 회원가입 API

- URI : POST /api/users/join
- 설명
  - email과 password를 입력하여 회원가입 할 수 있습니다.
  - email은 중복 불가합니다.
  - email은 Null일 수 없으며, @가 포함되어야 합니다.
  - password는 Null일 수 없으며, 8자 이상이여야 합니다.
- Request 예시
```json
{
  "email": "test@naver.com",
  "password": "12345678"
}
```
- 성공 Response 예시
```json
HTTP Status Code: 200
{
    "resultCode": "SUCCESS",
    "result": {
        "id": 1,
        "email": "aaa@aaa"
    }
}
```
- 실패 Response 예시
```json
HTTP Status Code: 400
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "BAD_REQUEST",
        "message": "이메일 형식이 아닙니다."
    }
}
```

### 로그인 API

- URI : POST /api/users/login
- 설명
  - email과 password를 입력하여 로그인 할 수 있습니다.
  - email은 Null일 수 없으며, @가 포함되어야 합니다.
  - password는 Null일 수 없으며, 8자 이상이여야 합니다.
  - email과 password가 올바르면 JWT Token을 return 합니다.
  - return 받은 Token을 Header의 Authorization에 'Bearer ' + Token으로 넣어주거나, Swagger 문서 우측 상단에 넣어주면 로그인이 완료됩니다.
- Request 예시
```json
{
    "email": "test@naver.com",
    "password": "12345678"
}
```
- 성공 Response 예시
```json
HTTP Status Code: 200
{
    "resultCode": "SUCCESS",
    "result": {
        "email": "aaa@aaa",
        "jwtToken": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFhYUBhYWEiLCJpYXQiOjE2OTE2NjAyMzIsImV4cCI6MTY5MTY2MjAzMn0.e9uQzw--XudpKFv2pSV3Jw3i8mVkfkRXAVgJkAKHtMs"
    }
}
```
- 실패 Response 예시
```json
HTTP Status Code: 400
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "BAD_REQUEST",
        "message": "비밀번호가 일치하지 않습니다."
    }
}
```

### 게시글 등록 API

- URI : POST /api/boards
- 설명
  - 로그인한 유저만 게시글을 작성 할 수 있습니다.
  - title과 body를 입력하여 게시글을 작성 할 수 있습니다.
  - title과 body는 Null일 수 없습니다.
- Request 예시
```json
{
    "title": "제목2",
    "body": "내용2"
}
```
- 성공 Response 예시
```json
HTTP Status Code: 200
{
    "resultCode": "SUCCESS",
    "result": {
        "id": 2,
        "userEmail": "aaa@aaa",
        "title": "제목2",
        "body": "내용2",
        "createdAt": "2023-08-10T18:39:36.158112",
        "lastModifiedAt": "2023-08-10T18:39:36.158112"
    }
}
```

- 실패 Response 예시
```json
HTTP Status Code: 401
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "INVALID_PERMISSION",
        "message": "로그인이 필요한 기능입니다."
    }
}
```

### 게시글 상세 조회 API

- URI : GET /api/boards/{게시글 Id}
- 설명
  - 게시글의 Id를 통해 해당 게시글을 조회 할 수 있습니다.
  - 게시글의 title, body와 작성자의 Email, 생성일자, 수정일자가 출력됩니다.
- Request 예시
```
GET /api/boards/2
```

- 성공 Response 예시
```json
HTTP Status Code: 200
{
    "resultCode": "SUCCESS",
    "result": {
        "id": 2,
        "userEmail": "aaa@aaa",
        "title": "제목2",
        "body": "내용2",
        "createdAt": "2023-08-10T18:39:36.158112",
        "lastModifiedAt": "2023-08-10T18:39:36.158112"
    }
}
```

- 실패 Response 예시
```json
HTTP Status Code: 400
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "BAD_REQUEST",
        "message": "게시글이 존재하지 않습니다."
    }
}
```

### 게시글 수정 API

- URI : PUT /api/boards/{게시글 ID}
- 설명
  - 본인이 작성한 게시글만 수정 할 수 있습니다.
  - 수정하고자 하는 게시글의 Id가 필요합니다.
  - title과 body를 입력하여 게시글을 수정 할 수 있습니다.
  - title과 body는 Null일 수 없습니다.
- Request 예시
```json
PUT /api/boards/2
{
  "title": "제목 수정 2",
  "body": "내용 수정 2"
}
```

- 성공 Response 예시
```json
HTTP Status Code : 200
{
    "resultCode": "SUCCESS",
    "result": {
        "id": 2,
        "userEmail": "aaa@aaa",
        "title": "제목 수정 2",
        "body": "내용 수정 2",
        "createdAt": "2023-08-10T18:39:36.158112",
        "lastModifiedAt": "2023-08-10T18:49:32.048125"
    }
}
```

- 실패 Response 예시
```json
HTTP Status Code : 401
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "INVALID_PERMISSION",
        "message": "본인이 작성한 게시글만 수정 가능합니다."
  }
}
```

### 게시글 삭제 API

- URI : DELETE /api/boards/{게시글 ID}
- 설명
  - 본인이 작성한 게시글만 삭제 할 수 있습니다.
  - 삭제하고자 하는 게시글의 Id가 필요합니다.
- Request 예시
```json
DELETE /api/boards/2
```

- 성공 Response 예시
```json
HTTP Status Code : 200
{
    "resultCode": "SUCCESS",
    "result": "2번 글이 삭제되었습니다."
}
```

- 실패 Response 예시
```json
HTTP Status Code : 401
{
    "resultCode": "ERROR",
    "result": {
        "errorCode": "INVALID_PERMISSION",
        "message": "본인이 작성한 게시글만 삭제 가능합니다."
  }
}
```

### 게시글 리스트 조회 API

- URI : GET /api/boards?page={페이지 번호}
- 설명
  - 게시글 리스트를 조회할 수 있습니다.
  - 1페이지부터 시작하며, 별도의 입력이 없다면 default는 1페이지입니다.
  - 한 페이지당 10개의 게시글이 출력됩니다.
  - 게시글은 최신순으로 정렬됩니다.
- Request 예시
```json
GET /api/boards?page=1
```

- 성공 Response 예시
```json
HTTP Status Code : 200
{
    "resultCode": "SUCCESS",
    "result": {
        "content": [
            {
                "id": 4,
                "userEmail": "aaa@aaa",
                "title": "제목4",
                "body": "내용4",
                "createdAt": "2023-08-11T13:45:49.675707",
                "lastModifiedAt": "2023-08-11T13:45:49.675707"
            }, 
            {
                "id": 3,
                "userEmail": "aaa@aaa",
                "title": "제목3",
                "body": "내용3",
                "createdAt": "2023-08-11T13:45:34.371286",
                "lastModifiedAt": "2023-08-11T13:45:34.371286"
            }, 
            {
                "id": 1,
                "userEmail": "chb20050@gmail.com",
                "title": "제목1",
                "body": "내용1",
                "createdAt": "2023-08-10T08:40:46.66829",
                "lastModifiedAt": "2023-08-10T08:40:46.66829"
            }
        ],
        "pageable": {
            "sort": {
                "empty": false,
                "sorted": true,
                "unsorted": false
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 10,
            "paged": true,
            "unpaged": false
        },
        "last": true,
        "totalPages": 1,
        "totalElements": 3,
        "size": 10,
        "number": 0,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "first": true,
        "numberOfElements": 3,
        "empty": false
    }
}
```