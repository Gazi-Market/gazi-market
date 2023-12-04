<a name="readme-top"></a>

<br />
<div align="center">
  <a href="https://github.com/Gazi-Market/gazi-market">
    <img src="app/src/main/res/drawable/logo.png" alt="Logo" width="100" height="100">
  </a>

<h3 align="center">🍆 2023 고급 모바일 프로그래밍 텀프로젝트 [ 가지마켓 ] 🍆</h3>

  <p align="center">
    당신을 위한 중고거래 플랫폼, 가지마켓

</div>

# Gazi-Market
> 한성대학교 고급 모바일 프로그래밍 프로젝트 : 가지마켓<br>개발 기간 : 2023.10 ~ 2023.12

## Contributors
@MinseoKangQ @jjinueng @xogk1128 @gyeongminn 

## Used Tech Stack
<div align="left">

  <img src="https://img.shields.io/badge/android%20studio-%233DDC84.svg?&style=for-the-badge&logo=android%20studio&logoColor=white" />
  &nbsp;
  <img src="https://img.shields.io/badge/kotlin-%230095D5.svg?&style=for-the-badge&logo=kotlin&logoColor=white" />
  &nbsp;
  <img src="https://img.shields.io/badge/firebase-%23FFCA28.svg?&style=for-the-badge&logo=firebase&logoColor=black" />

</div>

## 테스트 가이드
[여기](https://github.com/Gazi-Market/gazi-market/releases/download/v1.0/gazi-market.apk) 혹은 아래 첨부된 apk 파일 다운로드 후, 테스트 계정으로 로그인
> ID : android@hansung.ac.kr<br>PW : hansungandroid

## 유의 사항
- 자신이 작성한 게시물에는 체팅하기 버튼이 생성되지 않습니다.
- 판매완료로 변경한 경우 가격이 표시되는 부분이 판매 완료로 변경됩니다.

## 화면구성

| 첫 화면 | 회원가입 | 로그인 |
| --- | --- | --- |
| ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/4ad7bbd2-c6da-459c-9683-aae173c61374) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/add60232-46e2-4403-a8a9-d1acd78cdc5f) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/ceba9edb-99ce-418f-a7db-70a04fd43727) |


| 메인 화면 | 채팅 목록 | 마이페이지 |
| --- | --- | --- |
| ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/80820721-6fdd-4bcd-9554-94a33ff149c2) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/6a0e2704-23d3-4e5a-9af5-e299d800c16c) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/25320362-c472-473f-acc3-a6faac873385) |

|판매글 등록|상세 페이지|채팅|
| --- | --- | --- |
|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/7e81b09d-2f63-4ac1-be93-abfb9169410b)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/cb8ce252-cd0a-465c-823b-fcb721c37f49)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/8da65baf-cb23-481f-a476-3cbc412b8ce5)|

## 주요 기능
### 💡 사용자 계정 생성과 로그인 기능
- 회원가입 버튼 클릭 시, 사용자 계정 생성이 가능하며 이메일과 비밀번호, 생년월일, 이름 입력 후 가입이 가능합니다.
- 회원가입 후에는 자동으로 로그인이 됩니다.
- 회원가입 조건을 충족했을 때, 로그인 조건을 충족했을 때 각 버튼이 활성화됩니다.

### 💡 판매 글 목록 보기 기능
- Home 화면에서 판매 글 목록을 확인할 수 있습니다.
- 판매 글은 Grid 형태로 3개 씩 확인이 가능합니다.
- 판매 글 목록에서는 물건 가격이 10,000원 이상일 시, 단위가 만원으로 바뀝니다.
   ex) 20,200원 -> 2만원 / 10,020,000원 -> 100만원

| 판매 글 목록 | 판매 글 목록 가격 | 상세 페이지 가격 |
| --- | --- | --- |
|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/a706c3bb-8e5c-47a1-99dd-6814a35b68ac)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/484ab0b0-c7b3-4d8a-b7a1-8b5518d16efc)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/333502bf-c3d7-4974-a863-2f38e146dba9)|


### 💡 판매 글 목록 필터 기능
- 판매 글 목록의 좌측 상단 버튼을 누르면 필터링이 가능합니다.
- 판매중 상품 (판매된 상품 제외) / 판매 완료 상품 / 최소 가격 ~ 최대 가격 입력 후 필터링을 할 수 있습니다.


### 💡 글 등록 기능
- Home 화면의 우측 하단 버튼을 이용하여 판매글을 등록할 수 있습니다.
- 판매글에는 사진과 제목, 가격, 내용을 입력할 수 있고 3가지 모두 입력해야 작성완료 버튼이 활성화됩니다.

### 💡 상세페이지 기능
- 등록된 판매 글을 누르면 상세페이지를 확인할 수 있습니다.
- 상세페이지에는 판매자의 닉네임과 글의 제목, 내용, 가격이 나와있으며 판매중인 상품은 가격이, 판매완료된 상품은 판매완료가 표시됩니다.
- 우측 상단에는 더보기 버튼(글 수정, 판매완료/판매중 전환)이 있으며, 우측 하단에는 채팅하기 버튼이 있습니다.
- 판매글의 작성자가 본인일 시에는 더보기 버튼이 나타나고 채팅하기 버튼이 나타나지 않습니다.
- 판매글의 작성자가 본인이 아닐 시에는 더보기 버튼이 나타나지 않고 채팅하기 버튼이 나타납니다.

| 상세 페이지 (판매자) | 상세페이지 (구매자) | 구매 완료 전환 | 
| --- | --- | --- |
| ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/976e0707-e7cc-42db-818e-bd18a2482502) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/5d571f12-3a9f-4e98-9ba5-9ae0eb55b564) | ![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/6626fac5-a640-4e49-99c1-c5948054ba1a) |


### 💡 채팅 기능
- 상세페이지의 채팅하기 버튼을 누르면 판매자와 채팅이 시작됩니다.
- 실시간으로 메시지를 주고 받을 수 있으며 말풍선 옆 '1' 표시로 상대방이 메시지를 읽었는지, 읽지 않았는지 알 수 있습니다.
- 채팅목록에서는 상대방에게 온 메시지의 개수를 확인할 수 있습니다.
- 보낼 내용이 공백일 경우에는 메시지를 보낼 수 없습니다.
- 내용을 입력하면 전송 버튼이 파란색으로 활성화가 됩니다.

| 채팅 목록 | 채팅 시작 | 전송 버튼 활성화 | 안 읽음 | 읽음 |
| --- | --- | --- | --- | --- |
|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/6b821ee5-8336-49af-9cb2-6ff99e4a8820)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/33ec487e-3ba7-4973-bf35-5ed92f1ddbc0)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/98125813-79f9-45be-bbd3-569e357510fa)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/1ad52da4-f4d7-49f0-9e02-29110b786850)|![image](https://github.com/Gazi-Market/gazi-market/assets/97784561/09a4d32f-febb-4633-af2b-fc46460e3669)|
