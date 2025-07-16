# 📡 BADATA: BADATA 소개 

## 🛠 기술적 특징

### - ERD 🔗[ERDCloud에서 열기](https://www.erdcloud.com/d/NnvfEkHaQgXSXHWCm)
<img src="https://github.com/user-attachments/assets/0a8563e0-43b0-4050-aba6-bbd1b884ee54" style="width:100%; height:auto;" />

<br>

### - 아키텍처 

<img src="https://github.com/user-attachments/assets/27973cb4-3853-4115-ae58-9a0f82114734" style="width:100%; height:auto;" />

<br>

### - 기술 스택

| 사용 기술        | 로고 | 역할               | 사용 이유                                                 |
|------------------|------|--------------------|------------------------------------------------------------|
| JAVA (ver. 17)   | <img src="https://github.com/user-attachments/assets/ff2b6cbf-079d-47aa-8492-990e31cc1fbe" width="40"/> | Backend 언어       | 안정성과 대규모 서비스에 적합                                |
| Spring           | <img src="https://github.com/user-attachments/assets/4f5dd33b-79bf-4bfb-baa7-c5068deedac1" width="40"/> | Backend Framework | 유연한 모듈화와 의존성 관리                                 |
| Spring Security  | <img src="https://github.com/user-attachments/assets/2ccaf734-5366-453e-a5d6-566dbb6316ab" width="40"/> | 보안 관리          | 인증 및 권한 관리를 위한 강력한 솔루션                        |
| Gradle           | <img src="https://github.com/user-attachments/assets/662efb39-2c4d-4382-aa9a-182e996933e9" width="40"/> | 프로젝트 빌드 관리 | 빌드 자동화 및 의존성 관리에 용이                            |
| JPA              | <img src="https://github.com/user-attachments/assets/94c03ac7-660b-44f0-b49f-8169431b43d7" width="40"/> | ORM Framework     | 객체-관계 매핑 및 데이터베이스 관리                          |
| JUnit5           | <img src="https://github.com/user-attachments/assets/bac5d705-a038-41a6-9d4d-911b30c5e65c" width="40"/> | 단위/통합 테스트   | 애플리케이션 테스트 용이성 제공                              |
| PostgreSQL       | <img src="https://github.com/user-attachments/assets/8abd30c4-14fb-4f18-9d1e-b8ca1f06175a" width="40"/> | 관계형 DB          | 공간 DB로서 우수한 성능                                 |
| Redis            | <img src="https://github.com/user-attachments/assets/b1b7fa19-8454-448d-8f47-e52ba78297f3" width="40"/> | 인메모리 저장소     | 빠른 응답과 성능 향상을 위한 인메모리 기반 캐시 저장소         |
| AWS S3           | <img src="https://github.com/user-attachments/assets/dcd0111d-e598-4b05-9f76-0d7f033f85c3" width="40"/> | 객체 스토리지      | 이미지 파일 저장 및 관리                                     |
| AWS EC2          | <img src="https://github.com/user-attachments/assets/0db85e5d-7497-4874-8ecb-9fa711ac72f3" width="40"/> | 컴퓨팅 서비스      | 유연한 서버 인프라 제공                                     |
| AWS RDS          | <img src="https://github.com/user-attachments/assets/3b6db2b5-10ae-4196-81b8-3450f7c3005b" width="40"/> | 관리형 DB          | 간편한 DB 관리 및 자동 백업과 확장성 제공                    |

<br>

### - 백앤드 기능 목표
- SOS 시 중복 응답이 발생하지 않도록 동시성 제어를 적용한 시스템
- 조작 가능성이 있는 이미지를 ELA를 통해 검증하여 신뢰도 높은 게시글 제공
- 유저 행동 및 찜, 구매 내역을 기반으로 개인 맞춤형 추천 게시글 제공
- OCR 분석을 통해 필요한 정보만 선별/정제하여 게시글 등록 시 사용자 편의성 확보
- 포트원 API 연동, 결제 요청 및 검증 과정 구현으로 안정적인 결제 흐름 제공

---

## 📚 Github Wiki 🔗[Wiki에서 열기](https://github.com/Ureca-Final-Project-Team2/be_badata/wiki)


<ul>
<li><a href="https://github.com/Ureca-Final-Project-Team2/be_badata/wiki/%F0%9F%93%A2-%EC%BD%94%EB%93%9C-%EC%BB%A8%EB%B2%A4%EC%85%98"> 🛠️ 코드 컨벤션</a></li>
<li><a href="https://github.com/Ureca-Final-Project-Team2/be_badata/wiki/%F0%9F%97%A3%EF%B8%8F-Git-%EC%BB%A8%EB%B2%A4%EC%85%98-%EA%B0%80%EC%9D%B4%EB%93%9C-(COMMIT,-PR,-ISSUE)"> 📑 깃 컨벤션</a></li>
<li><a href="https://github.com/Ureca-Final-Project-Team2/be_badata/wiki/%F0%9F%A7%AD-Jira-%EC%82%AC%EC%9A%A9%EB%B2%95-%EA%B0%80%EC%9D%B4%EB%93%9C"> 🏗️ 지라 컨벤션 </a></li>
</ul>

---


## 📑 API 명세서 🔗[Notion에서 열기](https://www.notion.so/API-225672106a2081389214daa0b7ed286d?source=copy_link)


<details>
  <summary>사용자 API </summary> 

  <img src="https://github.com/user-attachments/assets/959c6e39-29cc-4d4b-a533-a4c520b8389a" style="width:100%; height:auto;" />
</details>

<details>
  <summary>대여 API</summary>

  <img src="https://github.com/user-attachments/assets/3b286d5b-872c-4292-a145-1ec3aa8a2414" style="width:100%; height:auto;" />
  <img src="https://github.com/user-attachments/assets/b1b9a8ff-f442-4bbb-8c88-26ed40428adb" style="width:100%; height:auto;" />
</details>

<details>
  <summary>렌탈 API</summary>
  <img src="https://github.com/user-attachments/assets/a5025c38-ca18-42c5-8236-8f6f4cc38017" style="width:100%; height:auto;" />
  <img src="https://github.com/user-attachments/assets/b5bb88b9-3e0d-47af-9e43-60c045dc570b" style="width:100%; height:auto;" />
</details>

<details>
  <summary>SOS API</summary>
  <img src="https://github.com/user-attachments/assets/4102bb05-5330-44e6-9523-aaa197734500" style="width:100%; height:auto;" />
</details>

---

## 🏗️ 폴더 구조 
```
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂TwoSeaU
 ┃ ┃ ┃ ┃ ┗ 📂BaData
 ┃ ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂rental
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂sos
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂store
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂trade
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┃ ┣ 📂global
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂redis
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┗ 📜BaDataApplication.java
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📂static
 ┃ ┃ ┣ 📂templates
 ┃ ┃ ┣ 📜application-dev.properties
 ┃ ┃ ┣ 📜application-local.properties
 ┃ ┃ ┗ 📜application.yml
```
---

## 📆 개발 일정
| 기간   | 내용         |
| ---- | ---------- |
| 7/15 ~ 7/18 | 2차 스프린트 시작 (남은 15% API 개발) |
| 7/19 ~ 7/20 |  테스트 코드 작성   |
| 7/21 ~ 8/1 | 성능 최적화 / 고도화 (주요 기능 최적화 및 고도화)  |


---

## 🧑🏻‍💻 역할 분담

<table align="center">
  <tr>
    <td align="center" width="250">
      <img src="https://avatars.githubusercontent.com/u/96781019?v=4" width="80"><br>
      <a href="https://github.com/dionisos198">👑 이진우</a>
    </td>
    <td align="center" width="250">
      <img src="https://avatars.githubusercontent.com/u/102001809?s=96&v=4" width="80"><br>
      <a href="https://github.com/marineAqu">김도연</a>
    </td>
    <td align="center" width="250">
      <img src="https://avatars.githubusercontent.com/u/99892677?v=4" width="80"><br>
      <a href="https://github.com/choyunju">조윤주</a>
    </td>
  </tr>
  <tr>
    <td align="center" valign="top">
      <ul align="left">
        <li>소셜 로그인 및 인증/인가</li>
        <li>가맹점 지도 시스템</li>
        <li>렌탈 시스템</li>
      </ul>
    </td>
    <td align="center" valign="top">
      <ul align="left">
        <li>거래 커뮤니티, 결제 시스템</li>
        <li>실시간 검색어 시스템</li>
        <li>게시글 추천 시스템</li>
        <li>CI/CD</li>
      </ul>
    </td>
    <td align="center" valign="top">
      <ul align="left">
        <li>인프라</li>
        <li>사용자 시스템</li>
        <li>SOS 시스템</li>
        <li>OCR</li>
      </ul>
    </td>
  </tr>
</table>
