# Pedestrian_Navigation_Practice1의 학습을 위한 프로젝트 파일입니다.

## 실행화면
#### v1.0.0
<img width="168" alt="image" src="https://github.com/junni01kim/Pedestrian_Navigation_Practice1/assets/127941871/17dbcc95-300d-45a4-a088-54e3ea3ce778">

#### v1.4
<img width="1218" alt="image" src="https://github.com/junni01kim/Pedestrian_Navigation_Practice/assets/127941871/5bb85887-a25a-4ad0-9d6a-1ea0efe7c2f0">

 
## Tmap API를 이용한 도보 네비게이션 만들기

## 개요
 해당 프로그램은 추후 제작할 보행자 네비게이션 프로그램에 사용할 Tmap API의 사용 방식을 이해하기 위한 연습용 프로젝트이다.

# 2024_03_28 (프로보노 0주차)
## 구현 기능
  1) 관련 POI 찍기 기능: 키워드와 관련된 POI들을 화면에 찍을 수 있다. (소스코드에서 변경 가능)
  2) 보행자 도로 안내 기능: 출발지와 목적지의 가장 연관성 높은 POI값을 통해 안내를 한다.
       - 현재 설정은 한성대 -> 한성대입구 (소스코드에서 변경가능)
    
## 작동 방식
  - 시작과 함께 바로 위치가 출력된다.

## 미구현 요소
  - 해당 프로젝트는 api를 적용해보는 것이 목적이기에 해당 부분을 만들지 않았다.
      1. 화면 안에서 목적지를 재설정하지 못한다.
      2. 프로그램의 POI를 findAllPOI()를 통해 하나만 찍고 싶었지만 하지 못했다.

# 2024_04_04 (프로보노 1주차)
## 구현 기능
  1) Tmap 대중교통 경로 api를 이용한 json값 받기
       - OkHttp를 이용하여 경로를 string을 수신

## 작동 방식
  - Log.d를 통해 스트링 값이 들어오는 것을 확인할 수 있다.

## 미구현 요소
  - 코드 파싱 및 경로 그리기

# 2024_04_09 (프로보노 2주차)
## 구현 기능
  1) 보행자 도로 안내 기능: 출발지와 목적지의 가장 연관성 높은 POI값을 통해 안내를 한다.
      - 처음 사용하는 코드이기에 코드를 정리하여 새롭게 디자인
  2) 보행자 경로를 기존의 여러 장소가 아닌 가장 유사한 장소를 안내한다.
      - 보행자 경로를 위치 값(string)만 함수에 전달하면 바로 그려진다.
  3) 스레드 이용
      - 기존의 함수에서 delay를 걸어 시스템이 일정 시간 씩 멈추던 방식에서 스레드를 이용해 경로 그리기를 지도 이용과 병렬적으로 처리
 ## 미구현 요소
   - 기존의 코드가 너무 난잡하여 코드를 정리하였고, 기존의 수행 목표였던 파싱을 하지 못하였다.
       - 원인은 json과 클래스화에 대한 학습 미흡이므로 추가 학습을 할 예정이다.

# 2024_04_27 (프로보노 4주차)
## 구현 기능
  1) 보행자 대중교통 경로 파싱: json string 값을 실제 객체로 만드는 방법을 학습.
       - 객체화를 하기 위한 data class를 만드는 방식을 숙지하고 필요한 data class를 제작해보았다.
  2) data class를 이용하여 클래스를 gson으로 파싱
       - gson 파싱을 진행 반복적으로 Null값이 반환이 되었는데, 이유는 네트워크 전송시간 동안 프로그램이 미리 진행되는 상황이었기 때문.
       - 플래스를 이용한 loop함수를 통해 문제 해결
  \* 회의 결과 그림 그리기는 반복 작업이기에 실 프로젝트에서 진행하기로 계획
