일정관리 파트
=============


C create
------------
> 일정 등록
> - (done) 기본등록 기능
> ```
>   {
>        "taskId": 일정 key,
>        "teamId": 팀 key,
>        "title": "일정제목",
>        "description": "일정상세",
>        "status": "진행상황",
>        "startDate": "시작일자",
>        "dueDate": "마감일자",
>        "parentTask": 상위일정
>    }
> ```
>> - (ongoing) 요청에 대한 리스폰스 리턴하기 (성공, 실패, 잘못된요청 등)
>> - (ongoing) 완료일자 추가 ( "endDate": "완료일자" )
>> - (ongoing) 작성자 추가 ( "userId": "작성자" )
> - (todo) 컬럼 추가 후 더미데이터 재생성
> - (todo) teamId, userId 등 테이블 조인 어떻게 해야하는가?


R read
------------
> 일정 조회
> - (done) 일정 전체목록 조회
>> - (ongoing) 요청에 대한 리스폰스 리턴하기 (성공, 실패, 잘못된요청 등)
>> - (ongoing) 메인화면-캘린더 부분
>>> - 조회요청 -> 1일~말일 “dueDate” 별 task title list
>>> - {
      “date” : “2024-08-01”,
      “dayTaskList” : [“할일 제목”,...]
      }
      {
      “date” : “2024-08-02”,
      “dayTaskList” : [“할일 제목”,...]
      }
      ...
>> - (ongoing) 메인화면-세부일정 부분
>>> - 조회요청 -> 캘린더에서 선택한 “dueDate” 의 task list
>>> - {
      “taskId” : “1”,
      “date” : “2024-08-01”,
      “title” : “일정 제목”,
      “description” : “일정 내용”,
      “status” : “todo”
      }
      ...
>> - (ongoing) 메인화면-진행상황 부분
>>> - 조회요청 -> 조회요청 -> 1일~말일 “status” 의 각각 항목별 갯수
>>> - {
      “todo” : “10”,
      “ongoing” : “5”,
      “done” : “3”,
      “delay” : “2”
      }
> - (todo) 일정 상/하위 관계에 따라 연계된 조회값 리턴
> - (todo) 조회량이 많아질 경우 속도개선을 어떻게 할 것인가?


U update
------------
> 일정 수정
> - (done) 일정수정 가능
>> - (ongoing) 요청에 대한 리스폰스 리턴하기 (성공, 실패, 잘못된요청 등)
> - (todo) 수정 요청 시 권한 확인 후 수정


D delete
------------
> 일정 삭제
> - (done) 선택/요청한 일정 삭제 가능
>> - (ongoing) 요청에 대한 리스폰스 리턴하기 (성공, 실패, 잘못된요청 등)
>- (todo) 삭제 요청 시 권한 확인 후 삭제


Etc.
------------
- task.html
- task-list.html
- 주석 조금 더 달기