# BackEnd
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/83e80f15-4d20-48fe-ba1a-b99988c718b2/image.png)

# 📢 서비스 소개

- 팀을 위한 효율적인 일정 관리 웹 서비스입니다!
- 프로젝트와 작업을 간편하게 관리하고, 팀원들과의 협업을 원활하게 도와줍니다!
- 시각화된 캘린더와 목록 기능을 통해 업무 진행 상황을 한눈에 파악하고, 보다 체계적인 업무 
수행이 가능해집니다!

---

# ⚙️ 서비스 아키텍처

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/83c75a39-3aba-4ba4-a792-7aefe4b07895/99c3148c-204e-46d3-9eee-7c4cb3b49cba/image.png)

---

## 📃 의사 결정 과정

## FE

| 사용 기술 | 결정 이유 |
| --- | --- |
| redux | 전역 객체 관리 프레임워크 
복잡한 상태를 효과적으로 관리할 수 있어 대규모 어플리케이션에 적합하여 채택. |
| React Query | 자동으로 JSON 데이터 변환으로 개발 효율 증가
로딩, 에러 등의 상태를 쉽게 처리 할 수 있음
데이터를 자동을 갱신해 항상 최신 데이터를 볼 수 있고 같은 데이터에 대하여 여러 번 요청이 있을 시 중복 요청을 제거함. |
| axios | 다양한 환경에서 일관된 API 사용 가능하며, JSON 데이터를 자동으로 처리하여 개발 효율성이 증가함. |
| react-Router | JSX를 사용하여 쉽게 라우트 정의가 가능하고, 복잡한 어플리케이션 구조를 효과적으로 구현 가능 하기에 사용. |
| pnpm | 설치 속도가 빠르고 패키지를 효율적으로 저장하여 중복 설치를 방지함. |
| styled-components | CSS를 컴포넌트로 정의하여 재사용성이 뛰어남. 
props를 기반으로 스타일을 동적으로 변경 가능하여 유연한 UI 구현이 가능하기에 채택. |

## BE

[data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==](data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==)

| 사용 기술 | 결정 이유 |
| --- | --- |
| docker | 컨테이너 기반의 가상화 기술로 개발, 테스트, 배포 환경 간 일관성을 유지할 수 있으며, 쉽게 배포와 확장이 가능함. |
| jenkins | Github Action보다 더 확장성이 훨씬 용이함. |
| redis | RefreshToken을 저장하는데 사용. 인 메모리 데이터 베이스로 읽고 쓰는 시간이 DBMS에 비해 월등히 빠르다. 인 메모리라 휘발성의 단점이 있지만 RefreshToken은 지워져도 서비스에 큰 문제가 없음. |
| Amazon Ec2 / S3 | 대규모  데이터를 안전하게 저장하고 관리할 수 있으며, 비용 효율성을 제공하여 사용량에 따라 유연하게 비용을 지불할 수 있으며, 관리 도구를 통해 데이터 관리를 편리하게 할 수 있어 채택. |
| swagger |  API 문서화를 자동화하고, 설계 및 테스트 지원을 통해 API 개발과 유지 보수를 보다
 효율적으로 관리할 수 있기에 채택. |
| jwt/spring security | 인증 및 권한 관리를 안전하게 구현할 수 있으며, JWT 토큰을 사용해서 서버가 별도의 세션을 저장하거나 관리할 필요 없이 각 요청에서 토큰을 검사해 사용자를 인증할 수 있습니다.  또한 Spring Security와 함께 사용하면 보안을 쉽게 설정하고 유지할 수 있어 채택. |
| websocket/stomp | 클라이언트와 서버 간에 양방향 실시간 통신을 위해 WebSocket을 사용했고,
STOMP는 WebSocket을 통한 실시간 통신을 보다 쉽게 하고,  메세지 전송을 관리하기에
채택. |
| mysql | 데이터베이스가 빠르고 안정적으로 작동하며, 많은 사람들이 사용하고 있어서 문제 해결을 위한 자료를 쉽게 찾을 수 있고, 여러 개발 도구와 잘 연결되어 사용하기 편리함. |

---

## 🚀 트러블 슈팅

### Frontend

- form-data
    - multipart-form-data로 이미지와 data를  보내야 하는 상황에 data가 전송 제대로 안 되는 걸 확인
    
    → data를 new Blob객체로 만들어서 전송하여 해결
    

### Backend

- spring security 403 에러
    - Filter에서 코드 오류가 생겨 모든 api 접근에서 403 에러가 발생함.
    - spring security는 정의되지 않은 오류를 403 에러로 반환 함.
        
        → 403 에러 시 spring security를 제일 먼저 의심해 볼 것.
        
- wss 연결
    - nginx 설정에서 인증서 설정, 443→8080으로 포트포워딩만 되면 wss사용이 될 줄 알았음.
    - wss 사용을 위해 nginx config에서 웹 소켓 업그레이드를 해주어야 함.
    
- 배포 시 서버에 코드가 반영이 안되는 문제
    - jenkins 콘솔에서 빌드 후 push까지 다 되었지만 서버에 코드가 반영이 안됨.
    - 서버의 root disk 저장 공간이 다 차서 업데이트가 되지 않았음.
    - 주기적으로 캐시 삭제를 해주거나 ci/cd 파이프라인에서 disk 정리를 추가해야 할 듯.

---

## 🌟 Team Members

[](https://www.notion.so/7f64e05557c044358f6dd7f49f63ae89?pvs=21)
