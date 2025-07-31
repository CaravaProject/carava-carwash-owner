# Carava - 디테일링 세차장 & PPF샵 예약 서비스 백엔드 기획서

## 📋 프로젝트 개요

### 서비스 소개
Carava는 디테일링 세차장 및 PPF(Paint Protection Film)샵 예약을 위한 양방향 플랫폼입니다.
- **고객**: 디테일링 서비스 검색, 예약, 리뷰 작성
- **업체**: 서비스 등록, 예약 관리, 매출 관리

### 기술 스택
- **Language**: Kotlin
- **Framework**: Spring Boot 3.5.0
- **Database**: PostgreSQL
- **Authentication**: JWT + Spring Security
- **Build Tool**: Gradle (Kotlin DSL)

---

## 🏗️ 시스템 아키텍처

### 패키지 구조
```
com.carava.carwash/
├── auth/           # 인증/인가 관련
├── user/          # 사용자 관리
├── business/      # 업체 관리
├── booking/       # 예약 관리
├── menu/          # 메뉴/서비스 관리
├── review/        # 리뷰 관리
├── message/       # 메시지 기능
├── notification/  # 알림 기능
├── payment/       # 결제 관리
├── file/          # 파일 업로드
└── global/        # 공통 기능
```

---

## 👤 사용자 관리 (User Management)

### 1. 회원가입 (Registration)

#### 1.1 일반 사용자 회원가입
**엔드포인트**: `POST /api/auth/register/customer`

**요청 데이터**:
```json
{
  "email": "customer@example.com",
  "password": "password123",
  "passwordConfirm": "password123",
  "phoneNumber": "010-1234-5678",
  "nickname": "차주김씨",
  "address": {
    "zipCode": "12345",
    "address": "서울시 강남구 테헤란로 123",
    "detailAddress": "456호"
  },
  "termsAgreed": true,
  "privacyAgreed": true,
  "marketingAgreed": false
}
```

**비즈니스 로직**:
- 이메일 중복 검증
- 비밀번호 복잡성 검증
- 휴대폰 번호 형식 검증
- 필수 약관 동의 확인

#### 1.2 업체 회원가입
**엔드포인트**: `POST /api/auth/register/business`

**요청 데이터**:
```json
{
  "email": "business@example.com",
  "password": "password123",
  "passwordConfirm": "password123",
  "phoneNumber": "010-1234-5678",
  "nickname": "세차왕",
  "businessNumber": "123-45-67890",
  "businessType": "DETAILING", // DETAILING, PPF, COATING
  "address": {
    "zipCode": "12345",
    "address": "서울시 강남구 테헤란로 123",
    "detailAddress": "1층"
  },
  "termsAgreed": true,
  "privacyAgreed": true,
  "marketingAgreed": false
}
```

#### 1.3 소셜 회원가입
**엔드포인트**: `POST /api/auth/register/social`

**요청 데이터**:
```json
{
  "provider": "GOOGLE", // GOOGLE, KAKAO, NAVER
  "socialId": "google_123456789",
  "email": "user@gmail.com",
  "nickname": "소셜유저",
  "phoneNumber": "010-1234-5678",
  "userType": "CUSTOMER", // CUSTOMER, BUSINESS
  "termsAgreed": true,
  "privacyAgreed": true
}
```

### 2. 로그인 (Authentication)

#### 2.1 이메일 로그인
**엔드포인트**: `POST /api/auth/login`

**요청 데이터**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답 데이터**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userType": "CUSTOMER",
  "userId": 1,
  "nickname": "차주김씨"
}
```

#### 2.2 소셜 로그인
**엔드포인트**: `POST /api/auth/social-login`

**요청 데이터**:
```json
{
  "provider": "GOOGLE",
  "accessToken": "google_access_token",
  "socialId": "google_123456789"
}
```

### 3. 비밀번호 관리

#### 3.1 비밀번호 찾기
**엔드포인트**: `POST /api/auth/password/reset-request`

**요청 데이터**:
```json
{
  "email": "user@example.com"
}
```

**비즈니스 로직**:
- 이메일 존재 여부 확인
- 임시 비밀번호 생성 및 이메일 발송
- 또는 비밀번호 재설정 링크 발송

#### 3.2 비밀번호 재설정
**엔드포인트**: `POST /api/auth/password/reset`

### 4. 차량 정보 관리 (고객 전용)

#### 4.1 차량 정보 등록
**엔드포인트**: `POST /api/customer/vehicles`

**요청 데이터**:
```json
{
  "licensePlate": "12가3456",
  "brand": "현대",
  "model": "아반떼",
  "year": 2023,
  "vehicleType": "SEDAN_COMPACT", // SEDAN_COMPACT, SEDAN_MID, SEDAN_LARGE, SUV_COMPACT, SUV_MID, SUV_LARGE, SPORTS_CAR
  "color": "흰색"
}
```

---

## 🏢 업체 관리 (Business Management)

### 1. 업체 정보 관리

#### 1.1 업체 정보 등록/수정
**엔드포인트**: `PUT /api/business/profile`

**요청 데이터**:
```json
{
  "businessName": "프리미엄 디테일링",
  "description": "20년 경력의 디테일링 전문점",
  "address": {
    "zipCode": "12345",
    "address": "서울시 강남구 테헤란로 123",
    "detailAddress": "지하1층"
  },
  "phoneNumber": "02-1234-5678",
  "businessHours": {
    "monday": {"open": "09:00", "close": "18:00", "closed": false},
    "tuesday": {"open": "09:00", "close": "18:00", "closed": false},
    "wednesday": {"open": "09:00", "close": "18:00", "closed": false},
    "thursday": {"open": "09:00", "close": "18:00", "closed": false},
    "friday": {"open": "09:00", "close": "18:00", "closed": false},
    "saturday": {"open": "09:00", "close": "15:00", "closed": false},
    "sunday": {"open": "09:00", "close": "15:00", "closed": true}
  },
  "holidays": ["2024-01-01", "2024-12-25"],
  "promotionLinks": [
    {"platform": "YOUTUBE", "url": "https://youtube.com/channel/123"},
    {"platform": "INSTAGRAM", "url": "https://instagram.com/detailing_pro"},
    {"platform": "BLOG", "url": "https://blog.naver.com/detailing"}
  ],
  "images": ["image1.jpg", "image2.jpg"]
}
```

### 2. 메뉴 관리

#### 2.1 메뉴 등록
**엔드포인트**: `POST /api/business/menus`

**요청 데이터**:
```json
{
  "name": "프리미엄 세차",
  "description": "외부 세차 + 내부 청소 + 왁스 코팅",
  "price": 50000,
  "duration": 120, // 분 단위
  "serviceType": "WASH", // WASH, COATING, WAX, PPF, POLISH
  "vehicleTypes": ["SEDAN_COMPACT", "SEDAN_MID"],
  "images": ["menu1.jpg", "menu2.jpg"]
}
```

### 3. 예약 관리

#### 3.1 예약 요청 조회
**엔드포인트**: `GET /api/business/bookings/requests`

**응답 데이터**:
```json
{
  "bookings": [
    {
      "id": 1,
      "customerName": "김고객",
      "customerPhone": "010-1234-5678",
      "vehicleInfo": "현대 아반떼 (12가3456)",
      "requestedDateTime": "2024-03-15T10:00:00",
      "menus": [
        {"name": "프리미엄 세차", "price": 50000, "duration": 120}
      ],
      "totalPrice": 50000,
      "totalDuration": 120,
      "specialRequests": "차량 하단부 특별 관리 부탁드립니다",
      "attachedImages": ["request1.jpg"],
      "status": "PENDING"
    }
  ]
}
```

#### 3.2 예약 수락/거절
**엔드포인트**: `POST /api/business/bookings/{bookingId}/respond`

**요청 데이터**:
```json
{
  "action": "ACCEPT", // ACCEPT, REJECT
  "rejectionReason": "해당 시간대 예약이 가득 참", // action이 REJECT일 때만
  "conflictWarning": "이미 예약된 시간과 겹칩니다. 정말 수락하시겠습니까?" // 시간 충돌 시
}
```

### 4. 매출 관리

#### 4.1 매출 현황 조회
**엔드포인트**: `GET /api/business/sales/summary`

**쿼리 파라미터**:
- `year`: 연도 (기본값: 현재 연도)
- `month`: 월 (기본값: 현재 월)

**응답 데이터**:
```json
{
  "monthlyRevenue": 2500000,
  "completedBookings": 45,
  "totalReviews": 32,
  "averageRating": 4.8,
  "monthlyTarget": 3000000,
  "achievementRate": 83.3,
  "dailyRevenue": [
    {"date": "2024-03-01", "revenue": 120000, "bookings": 3},
    {"date": "2024-03-02", "revenue": 80000, "bookings": 2}
  ],
  "popularMenus": [
    {"menuName": "프리미엄 세차", "count": 20, "revenue": 1000000},
    {"menuName": "코팅", "count": 15, "revenue": 900000}
  ]
}
```

#### 4.2 매출 목표 설정
**엔드포인트**: `PUT /api/business/sales/target`

**요청 데이터**:
```json
{
  "year": 2024,
  "month": 3,
  "targetAmount": 3000000
}
```

---

## 📱 고객 서비스 (Customer Service)

### 1. 홈 화면

#### 1.1 홈 데이터 조회
**엔드포인트**: `GET /api/customer/home`

**응답 데이터**:
```json
{
  "notifications": [
    {"id": 1, "title": "예약 확정", "message": "3월 15일 예약이 확정되었습니다", "createdAt": "2024-03-10T10:00:00"},
    {"id": 2, "title": "리뷰 요청", "message": "서비스 후기를 남겨주세요", "createdAt": "2024-03-09T15:30:00"}
  ],
  "recommendedBusinesses": [
    {
      "id": 1,
      "name": "프리미엄 디테일링",
      "rating": 4.8,
      "reviewCount": 245,
      "distance": 2.3,
      "images": ["business1.jpg"],
      "isLiked": true
    }
  ],
  "recommendedServices": [
    {
      "menuId": 1,
      "businessId": 1,
      "businessName": "프리미엄 디테일링",
      "menuName": "봄맞이 특가 세차",
      "originalPrice": 50000,
      "discountPrice": 40000,
      "images": ["service1.jpg"]
    }
  ]
}
```

### 2. 업체 검색

#### 2.1 업체 검색
**엔드포인트**: `GET /api/customer/businesses/search`

**쿼리 파라미터**:
- `keyword`: 업체명 검색어
- `region`: 지역 (시/구/동)
- `date`: 예약 희망 날짜 (YYYY-MM-DD)
- `time`: 예약 희망 시간 (HH:mm)
- `serviceType`: 서비스 타입
- `sortBy`: 정렬 기준 (distance, rating, price)
- `page`: 페이지 번호
- `size`: 페이지 크기

**응답 데이터**:
```json
{
  "businesses": [
    {
      "id": 1,
      "name": "프리미엄 디테일링",
      "address": "서울시 강남구 테헤란로 123",
      "rating": 4.8,
      "reviewCount": 245,
      "distance": 2.3,
      "images": ["business1.jpg"],
      "isLiked": true,
      "availableSlots": ["10:00", "14:00", "16:00"], // date, time 파라미터가 있을 때만
      "minPrice": 30000,
      "maxPrice": 150000
    }
  ],
  "totalCount": 25,
  "currentPage": 1,
  "totalPages": 3
}
```

### 3. 업체 상세 정보

#### 3.1 업체 상세 조회
**엔드포인트**: `GET /api/customer/businesses/{businessId}`

**응답 데이터**:
```json
{
  "id": 1,
  "name": "프리미엄 디테일링",
  "description": "20년 경력의 디테일링 전문점",
  "address": "서울시 강남구 테헤란로 123",
  "phoneNumber": "02-1234-5678",
  "rating": 4.8,
  "reviewCount": 245,
  "isLiked": true,
  "images": ["business1.jpg", "business2.jpg"],
  "businessHours": {
    "monday": {"open": "09:00", "close": "18:00", "closed": false}
  },
  "promotionLinks": [
    {"platform": "YOUTUBE", "url": "https://youtube.com/channel/123"}
  ],
  "menus": [
    {
      "id": 1,
      "name": "프리미엄 세차",
      "description": "외부 세차 + 내부 청소 + 왁스 코팅",
      "price": 50000,
      "duration": 120,
      "serviceType": "WASH",
      "vehicleTypes": ["SEDAN_COMPACT", "SEDAN_MID"],
      "images": ["menu1.jpg"]
    }
  ],
  "recentReviews": [
    {
      "id": 1,
      "customerNickname": "차주***",
      "rating": 5,
      "content": "정말 깔끔하게 잘해주세요!",
      "images": ["review1.jpg"],
      "createdAt": "2024-03-10T14:30:00"
    }
  ]
}
```

### 4. 예약 기능

#### 4.1 예약 가능 시간 조회
**엔드포인트**: `GET /api/customer/businesses/{businessId}/available-slots`

**쿼리 파라미터**:
- `date`: 예약 희망 날짜 (YYYY-MM-DD)
- `menuIds`: 선택한 메뉴 ID 배열

**응답 데이터**:
```json
{
  "date": "2024-03-15",
  "totalDuration": 180, // 선택한 메뉴들의 총 소요시간
  "availableSlots": [
    {"time": "09:00", "available": true},
    {"time": "09:30", "available": true},
    {"time": "10:00", "available": false},
    {"time": "10:30", "available": true}
  ]
}
```

#### 4.2 예약 요청
**엔드포인트**: `POST /api/customer/bookings`

**요청 데이터**:
```json
{
  "businessId": 1,
  "menuIds": [1, 2],
  "vehicleId": 1,
  "requestedDateTime": "2024-03-15T10:00:00",
  "specialRequests": "차량 하단부 특별 관리 부탁드립니다",
  "attachedImages": ["request1.jpg"]
}
```

### 5. 예약 관리

#### 5.1 내 예약 조회
**엔드포인트**: `GET /api/customer/bookings`

**쿼리 파라미터**:
- `status`: 예약 상태 (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- `page`: 페이지 번호

**응답 데이터**:
```json
{
  "bookings": [
    {
      "id": 1,
      "businessName": "프리미엄 디테일링",
      "businessPhone": "02-1234-5678",
      "menus": [
        {"name": "프리미엄 세차", "price": 50000}
      ],
      "totalPrice": 50000,
      "scheduledDateTime": "2024-03-15T10:00:00",
      "status": "CONFIRMED",
      "vehicleInfo": "현대 아반떼 (12가3456)",
      "canCancel": true,
      "canReview": false
    }
  ]
}
```

### 6. 좋아요 & 저장

#### 6.1 업체 좋아요
**엔드포인트**: `POST /api/customer/businesses/{businessId}/like`

#### 6.2 좋아요 취소
**엔드포인트**: `DELETE /api/customer/businesses/{businessId}/like`

#### 6.3 저장된 업체 조회
**엔드포인트**: `GET /api/customer/saved-businesses`

---

## 💬 메시지 기능 (Messaging)

### 1. 채팅방 관리

#### 1.1 채팅방 목록 조회
**엔드포인트**: `GET /api/messages/rooms`

**응답 데이터**:
```json
{
  "rooms": [
    {
      "id": 1,
      "businessId": 1,
      "businessName": "프리미엄 디테일링",
      "customerId": 1,
      "customerNickname": "차주김씨",
      "lastMessage": "네, 감사합니다!",
      "lastMessageTime": "2024-03-10T15:30:00",
      "unreadCount": 2
    }
  ]
}
```

#### 1.2 채팅방 생성/입장
**엔드포인트**: `POST /api/messages/rooms`

**요청 데이터**:
```json
{
  "businessId": 1,
  "customerId": 1
}
```

### 2. 메시지 관리

#### 2.1 메시지 조회
**엔드포인트**: `GET /api/messages/rooms/{roomId}/messages`

**응답 데이터**:
```json
{
  "messages": [
    {
      "id": 1,
      "senderId": 1,
      "senderType": "CUSTOMER",
      "content": "안녕하세요, 예약 문의드립니다",
      "messageType": "TEXT",
      "images": [],
      "sentAt": "2024-03-10T14:00:00",
      "isRead": true
    },
    {
      "id": 2,
      "senderId": 2,
      "senderType": "BUSINESS",
      "content": "네, 안녕하세요! 무엇을 도와드릴까요?",
      "messageType": "TEXT",
      "images": [],
      "sentAt": "2024-03-10T14:05:00",
      "isRead": true
    }
  ]
}
```

#### 2.2 메시지 전송
**엔드포인트**: `POST /api/messages/rooms/{roomId}/messages`

**요청 데이터**:
```json
{
  "content": "차량 사진 첨부합니다",
  "messageType": "TEXT_WITH_IMAGE",
  "images": ["message1.jpg", "message2.jpg"]
}
```

### 3. 신고 기능

#### 3.1 사용자 신고
**엔드포인트**: `POST /api/messages/rooms/{roomId}/report`

**요청 데이터**:
```json
{
  "reportedUserId": 2,
  "reason": "SPAM", // SPAM, INAPPROPRIATE, HARASSMENT, OTHER
  "description": "상업적 스팸 메시지를 계속 보냅니다",
  "evidenceImages": ["evidence1.jpg"]
}
```

---

## ⭐ 리뷰 관리 (Review Management)

### 1. 리뷰 작성

#### 1.1 리뷰 작성
**엔드포인트**: `POST /api/reviews`

**요청 데이터**:
```json
{
  "bookingId": 1,
  "businessId": 1,
  "rating": 5,
  "content": "정말 깔끔하게 잘해주세요! 다음에도 이용하겠습니다.",
  "images": ["review1.jpg", "review2.jpg"]
}
```

### 2. 리뷰 조회

#### 2.1 업체 리뷰 조회
**엔드포인트**: `GET /api/businesses/{businessId}/reviews`

#### 2.2 내가 작성한 리뷰 조회
**엔드포인트**: `GET /api/customer/reviews`

---

## 📁 파일 업로드 (File Upload)

### 1. 파일 업로드
**엔드포인트**: `POST /api/files/upload`

**요청**: Multipart Form Data
- `file`: 업로드할 파일
- `type`: 파일 타입 (PROFILE, BUSINESS, MENU, REVIEW, MESSAGE, VEHICLE)

**응답 데이터**:
```json
{
  "fileName": "20240310_143022_image.jpg",
  "fileUrl": "/uploads/20240310_143022_image.jpg",
  "fileSize": 1024000
}
```

---

## 🔔 알림 관리 (Notification)

### 1. 알림 조회
**엔드포인트**: `GET /api/notifications`

### 2. 알림 읽음 처리
**엔드포인트**: `PUT /api/notifications/{notificationId}/read`

---

## 👤 마이페이지 (My Page)

### 1. 고객 마이페이지

#### 1.1 프로필 수정
**엔드포인트**: `PUT /api/customer/profile`

#### 1.2 예약 관리
**엔드포인트**: `GET /api/customer/bookings`

#### 1.3 나의 저장
**엔드포인트**: `GET /api/customer/saved-businesses`

#### 1.4 내 리뷰
**엔드포인트**: `GET /api/customer/reviews`

### 2. 업체 마이페이지

#### 2.1 프로필 수정
**엔드포인트**: `PUT /api/business/profile`

#### 2.2 예약 관리
**엔드포인트**: `GET /api/business/bookings`

#### 2.3 매출 관리
**엔드포인트**: `GET /api/business/sales`

#### 2.4 리뷰 관리
**엔드포인트**: `GET /api/business/reviews`

---

## 🗃️ 데이터베이스 설계

### 주요 엔티티

#### 1. User (사용자)
```kotlin
@Entity
data class User(
    @Id @GeneratedValue
    val id: Long = 0,
    
    @Column(unique = true)
    val email: String,
    
    val password: String?,
    val phoneNumber: String,
    val nickname: String,
    
    @Enumerated(EnumType.STRING)
    val userType: UserType, // CUSTOMER, BUSINESS
    
    @Enumerated(EnumType.STRING)
    val authProvider: AuthProvider, // EMAIL, GOOGLE, KAKAO, NAVER
    
    val socialId: String?,
    
    @Embedded
    val address: Address,
    
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

#### 2. Business (업체)
```kotlin
@Entity
data class Business(
    @Id @GeneratedValue
    val id: Long = 0,
    
    @OneToOne
    val owner: User,
    
    val businessName: String,
    val businessNumber: String,
    val description: String?,
    
    @Enumerated(EnumType.STRING)
    val businessType: BusinessType, // DETAILING, PPF, COATING
    
    @Embedded
    val address: Address,
    
    val phoneNumber: String,
    
    @OneToMany(mappedBy = "business")
    val businessHours: List<BusinessHours> = emptyList(),
    
    @ElementCollection
    val holidays: List<LocalDate> = emptyList(),
    
    @OneToMany(mappedBy = "business")
    val promotionLinks: List<PromotionLink> = emptyList(),
    
    @ElementCollection
    val images: List<String> = emptyList(),
    
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

#### 3. Menu (메뉴)
```kotlin
@Entity
data class Menu(
    @Id @GeneratedValue
    val id: Long = 0,
    
    @ManyToOne
    val business: Business,
    
    val name: String,
    val description: String?,
    val price: Int,
    val duration: Int, // 분 단위
    
    @Enumerated(EnumType.STRING)
    val serviceType: ServiceType, // WASH, COATING, WAX, PPF, POLISH
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    val vehicleTypes: List<VehicleType> = emptyList(),
    
    @ElementCollection
    val images: List<String> = emptyList(),
    
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

#### 4. Booking (예약)
```kotlin
@Entity
data class Booking(
    @Id @GeneratedValue
    val id: Long = 0,
    
    @ManyToOne
    val customer: User,
    
    @ManyToOne
    val business: Business,
    
    @ManyToOne
    val vehicle: Vehicle,
    
    @ManyToMany
    val menus: List<Menu> = emptyList(),
    
    val requestedDateTime: LocalDateTime,
    val totalPrice: Int,
    val totalDuration: Int,
    
    val specialRequests: String?,
    
    @ElementCollection
    val attachedImages: List<String> = emptyList(),
    
    @Enumerated(EnumType.STRING)
    val status: BookingStatus, // PENDING, CONFIRMED, COMPLETED, CANCELLED, REJECTED
    
    val rejectionReason: String?,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
```

---

## 🔒 보안 및 인증

### JWT 토큰 관리
- Access Token: 1시간 유효
- Refresh Token: 2주 유효
- 토큰 갱신 엔드포인트: `POST /api/auth/refresh`

### 권한 관리
- CUSTOMER: 고객 전용 기능
- BUSINESS: 업체 전용 기능
- ADMIN: 관리자 기능 (추후 구현)

---

## 🚀 추가 구현 필요 기능

### 1. 결제 시스템
- 토스페이먼츠/아임포트 연동
- 예약금 결제
- 현장 결제 확인

### 2. 푸시 알림
- FCM 연동
- 예약 상태 변경 알림
- 마케팅 알림

### 3. 관리자 시스템
- 업체 승인 관리
- 신고 처리
- 정산 관리

### 4. 데이터 분석
- 업체별 매출 통계
- 인기 서비스 분석
- 사용자 행동 분석

### 5. 실시간 기능
- WebSocket을 이용한 실시간 채팅
- 실시간 예약 현황

---

## 📋 API 개발 우선순위

### Phase 1 (필수 기능)
1. 사용자 인증 (회원가입, 로그인)
2. 업체 등록 및 메뉴 관리
3. 예약 기본 기능
4. 파일 업로드

### Phase 2 (핵심 기능)
1. 검색 및 필터링
2. 메시지 기능
3. 리뷰 시스템
4. 알림 기능

### Phase 3 (고도화)
1. 결제 연동
2. 실시간 채팅
3. 데이터 분석
4. 관리자 시스템

---

이 기획서를 바탕으로 백엔드 개발을 시작하시면 됩니다! 각 기능별로 상세한 구현이 필요한 부분이 있으면 언제든 문의해 주세요.