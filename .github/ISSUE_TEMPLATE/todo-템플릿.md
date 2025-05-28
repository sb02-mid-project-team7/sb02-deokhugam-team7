---
name: TODO 템플릿
about: TODO 탬플릿입니다.
title: ''
labels: TODO
assignees: ''

---

### 🧭 작업 이름

- 채널 생성 기능 구현

---

### 🎯 목표

- 서버에 소속된 사용자가 새로운 채널을 생성할 수 있도록 구현
- 기본적인 유효성 검증 및 DB 저장까지 완료

---

### 📌 작업 항목

- [ ]  API 명세 확인 및 URL 설계 (`POST /api/servers/{serverId}/channels`)
- [ ]  Request/Response DTO 생성
- [ ]  Controller 메서드 생성
- [ ]  Service 비즈니스 로직 작성
- [ ]  Channel 엔티티 연관관계 설정 확인
- [ ]  DB 저장 로직 구현
- [ ]  간단한 테스트 진행 (Postman or Swagger)

---

### 📎 참고사항

- title 필드는 필수, 최대 50자
- description은 옵션, 255자 이하
- 인증된 사용자만 생성 가능
- 같은 서버 내에서 title 중복은 허용 X

---

### 🕒 예상 소요 시간

- 2~3시간

---

### 👤 담당자

- 홍길동 (@honggildong)
