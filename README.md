# sb02-deokhugam-team7

[![codecov](https://codecov.io/gh/sb02-mid-project-team7/sb02-deokhugam-team7/graph/badge.svg?token=XLA9WNJZL3)](https://codecov.io/gh/sb02-mid-project-team7/sb02-deokhugam-team7)   
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

# **7팀**

[🪧팀 회의 노션 페이지 바로가기](https://spot-blizzard-f33.notion.site/7-1fc49229cb1480e8a716c601a9388fc0?source=copy_link)   
[🪧깃허브 레포지토리 바로가기](https://github.com/sb02-mid-project-team7/sb02-deokhugam-team7)

## **팀원 구성**

[✅김태우](https://github.com/kimtaewoo9)<br>
[✅선혜린](https://github.com/seonseon933)<br>
[✅이종원](https://github.com/BrotherMountain)<br>
[✅최규원](https://github.com/GYUWON-CHOI)<br>
[✅한성태](https://github.com/Seong-taeHan)

---

## **프로젝트 소개**

- 책 읽는 즐거움을 공유하고, 지식과 감상을 나누는 책 덕후들의 커뮤니티 서비스
- 프로젝트 기간: 2025.5.28 ~ 2025.06.18

---

## **기술 스택**

- Backend: Spring Boot, Spring Security, Spring Data JPA
- Database: PostgreSQL
- 공통 Tool: Git & Github, Discord

---

## 팀원별 구현 기능 상세
### **김태우**

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **댓글 관리 API**
    - 댓글 정보의 CRUD 처리

### **선혜린**

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **리뷰 관리 API**
    - Query DSL을 활용한 리뷰 정보의 CRUD 처리(Spring Data JPA 사용).
- **인기 리뷰 API**
    - Spring Batch, Schedule을 활용한 정기적인 배치 시스템 구현. 
    - 리뷰의 좋아요 수, 댓글 수에 따른 기간별(일간, 주간, 월간, 역대) 리뷰 점수를 구하여 인기 리뷰 구현.

### **이종원**

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **도서 관리 API**
    - 도서 정보의 CRUD 처리(Spring Data JPA 사용)
    - ISBN을 바탕으로 네이버 API 요청 서비스 구현
    - ZXing을 통해 바코드로 ISBN을 추출하는 서비스 구현
    - S3버킷에 도서 이미지를 업로드
- **인기 도서 API**
    - 기간별(일별, 주간, 월간, 역대) 리뷰 수와 평점에 따른 인기 도서 순위 구현

### **최규원**

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **사용자 관리 API**
    - 사용자 정보의 CRUD 처리(Spring Data JPA 사용)
- **파워 유저 API**
    - Spring Batch, Schedule을 활용한 정기적인 배치 시스템 구현. 
    - 기간별(일간, 주간, 월간, 역대) 활동 점수에 따른 파워 유저 순위 구현.

### **한성태**

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **알림 관리 API**
    - 사용자의 리뷰에 좋아요, 댓글이 추가되면 사용자에게 알림을 생성
    - 일주일이 지난 알람 자동 삭제 처리 기능 구현
- **공용 에러 응답 관리**
    - 예외 처리에 대한 공통 응답 처리 구현
- **로그 관리**
    - Aspect를 이용한 controller, service, repository의 일관된 로그 처리
    - 하루에 한번 에러 로그 및 정상 로그 S3 업로드
    - MDC를 이용해 요청별 사용자 및 요청 시간 식별 가능한 로그 구현

---

## **파일 구조**
<details>
<summary>📁 프로젝트 파일 구조</summary>
<div markdown="1">

```
.
├─.github
│  ├─ISSUE_TEMPLATE
│  └─workflows
├─.gradle
├─build
├─gradle
│  └─wrapper
├─src
│  ├─main
│  │  ├─generated
│  │  │  └─com
│  │  │      └─sprint
│  │  │          └─deokhugamteam7
│  │  │              └─domain
│  │  │                  ├─book
│  │  │                  │  └─entity
│  │  │                  ├─comment
│  │  │                  │  └─entity
│  │  │                  ├─notification
│  │  │                  │  └─entity
│  │  │                  ├─review
│  │  │                  │  └─entity
│  │  │                  └─user
│  │  │                      └─entity
│  │  ├─java
│  │  │  └─com
│  │  │      └─sprint
│  │  │          └─deokhugamteam7
│  │  │              ├─aspect
│  │  │              ├─config
│  │  │              ├─constant
│  │  │              ├─domain
│  │  │              │  ├─book
│  │  │              │  │  ├─batch
│  │  │              │  │  │  ├─schedule
│  │  │              │  │  │  └─step
│  │  │              │  │  ├─controller
│  │  │              │  │  ├─dto
│  │  │              │  │  │  ├─condition
│  │  │              │  │  │  ├─request
│  │  │              │  │  │  └─response
│  │  │              │  │  ├─entity
│  │  │              │  │  ├─repository
│  │  │              │  │  │  └─custom
│  │  │              │  │  └─service
│  │  │              │  ├─comment
│  │  │              │  │  ├─controller
│  │  │              │  │  ├─dto
│  │  │              │  │  │  ├─request
│  │  │              │  │  │  └─response
│  │  │              │  │  ├─entity
│  │  │              │  │  ├─repository
│  │  │              │  │  └─service
│  │  │              │  ├─log
│  │  │              │  │  └─service
│  │  │              │  ├─notification
│  │  │              │  │  ├─controller
│  │  │              │  │  ├─dto
│  │  │              │  │  ├─entity
│  │  │              │  │  ├─repository
│  │  │              │  │  │  └─custom
│  │  │              │  │  └─service
│  │  │              │  ├─review
│  │  │              │  │  ├─batch
│  │  │              │  │  │  ├─schedule
│  │  │              │  │  │  └─step
│  │  │              │  │  ├─controller
│  │  │              │  │  ├─dto
│  │  │              │  │  │  ├─request
│  │  │              │  │  │  └─response
│  │  │              │  │  ├─entity
│  │  │              │  │  ├─repository
│  │  │              │  │  │  └─custom
│  │  │              │  │  └─service
│  │  │              │  └─user
│  │  │              │      ├─batch
│  │  │              │      │  ├─schedule
│  │  │              │      │  ├─step
│  │  │              │      │  └─tasklet
│  │  │              │      ├─controller
│  │  │              │      ├─dto
│  │  │              │      │  ├─request
│  │  │              │      │  └─response
│  │  │              │      ├─entity
│  │  │              │      ├─repository
│  │  │              │      │  └─custom
│  │  │              │      └─service
│  │  │              ├─exception
│  │  │              │  ├─book
│  │  │              │  ├─comment
│  │  │              │  ├─notification
│  │  │              │  ├─review
│  │  │              │  └─user
│  │  │              └─swagger
│  │  └─resources
│  │      └─static
│  │          ├─assets
│  │          └─images
│  └─test
│      ├─java
│      │  └─com
│      │      └─sprint
│      │          └─deokhugamteam7
│      │              ├─config
│      │              └─domain
│      │                  ├─book
│      │                  │  ├─entity
│      │                  │  └─service
│      │                  ├─comment
│      │                  │  ├─controller
│      │                  │  ├─data
│      │                  │  ├─repository
│      │                  │  └─service
│      │                  ├─notification
│      │                  │  ├─config
│      │                  │  ├─intergration
│      │                  │  └─unit
│      │                  │      ├─controller
│      │                  │      ├─entity
│      │                  │      ├─repository
│      │                  │      └─service
│      │                  │          └─impl
│      │                  ├─review
│      │                  │  ├─batch
│      │                  │  │  └─schedule
│      │                  │  ├─controller
│      │                  │  ├─entity
│      │                  │  ├─integration
│      │                  │  ├─repository
│      │                  │  └─service
│      │                  │      └─basic
│      │                  └─user
│      │                      ├─config
│      │                      ├─controller
│      │                      ├─entity
│      │                      ├─integration
│      │                      ├─repository
│      │                      └─service
│      └─resources
│          └─file
└─storage

```

</div>
</details>

---

## **구현 홈페이지**

(개발한 홈페이지에 대한 링크 게시)

https://www.codeit.kr/

---

## **프로젝트 회고록**

(제작한 발표자료 링크 혹은 첨부파일 첨부)
