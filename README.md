# PAAS-TA-PORTAL-STORAGE-API

## Storage Api Service
Storage Api Service? Object Storage에 파일 업로드 및 다운로드를 제공하는 기능을 제공하기 위해
별도의 API 서비스로 분리하여, 개발된 서비스이다.
현재는 Swift Object Storage를 지원하고 있다.

## 사용방법
### Swift Object Storage
- Base Request URL : http://[STORAGE-API-HOST]:[STORAGE-API-PORT]/v2/swift
 - Request Method
   - POST (File upload, multipart)
     - URI : http://[STORAGE-API-HOST]:[STORAGE-API-PORT]/v2/swift
	 - Response : Stored file name
   - GET (File download)
     - URI : http://[STORAGE-API-HOST]:[STORAGE-API-PORT]/v2/swift/[STORED-FILE-NAME]
     - Response : Binary (raw file)
   - DELETE (File delete/remove)
     - URI : http://[STORAGE-API-HOST]:[STORAGE-API-PORT]/v2/swift/[FILE-NAME]
	 - Response : Result status (SUCCESS, FAIL)
   - PUT (File update) : Unsupported operation


## 유의사항
- Java 1.8 버전
- Spring Cloud Edgware.RELEASE 
- Tomcat Embedded 8.5.14
- Gradle 4.4.1
- Spring-boot 1.5.9
