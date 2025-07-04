# echoLog

## 소개
echoLog는 명령 프롬프트(cmd) 창처럼 텍스트가 실시간으로 쌓이는 재미를 위한 웹사이트입니다.  
Java(Spring Boot)와 MySQL, JavaScript를 사용하여 구현되었습니다.

## 기술 스택
- Java 1.8
- Spring Boot 2.7.5
- Gradle
- MySQL 8.0
- JavaScript (프론트엔드)
- MyBatis

## 주요 기능
- 웹에서 명령어 입력 및 결과 출력(cmd 스타일 UI)
- 입력 내역 및 결과 로그처럼 실시간 표시
- 명령어 기록 및 관리

## 프로젝트 구조
- `src/main/java/home/example/echoLog/` : 백엔드 코드(컨트롤러, 서비스, 모델, 매퍼 등)
- `src/main/resources/` : 설정, SQL, 정적 리소스, 템플릿
- `sql/` : DB 초기화 스크립트

## DB 설정
src/main/resources/application.properties 또는 환경별 파일에서 MySQL 접속 정보를 설정하세요.
필요시 sql/create.sql로 DB 및 테이블을 생성하세요.
애플리케이션 실행

## 애플리케이션 실행
```bash
    ./gradlew bootRun
```
또는
```bash
java -jar build/libs/echoLog-*.jar
```

### 웹사이트 접속
http://localhost:8080/