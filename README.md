# SmartBloodDonation
```
SmartBloodDonation/
├── build.gradle.kts                 // File build Gradle của project
├── settings.gradle.kts              // Khai báo các module của project
├── gradle/
│
├── app/                             // Module chính, nơi ghép nối các module feature
│   ├── build.gradle.kts
│   └── src/main/
│       ├── java/com/smartblood/
│       │   ├── MainApplication.kt   // Lớp Application, khởi tạo Hilt
│       │   ├── MainActivity.kt      // Activity duy nhất, host của NavHost
│       │   ├── di/                  // DI cho module app
│       │   │   └── AppModule.kt
│       │   └── navigation/          // Quản lý điều hướng toàn ứng dụng
│       │       ├── AppNavHost.kt    // Cấu hình NavController và các graph
│       │       └── Screen.kt        // Định nghĩa các route
│       └── res/
│
├── core/                            // Module lõi chứa code dùng chung
│   ├── build.gradle.kts
│   └── src/main/java/com/smartblood/core/
│       ├── data/
│       │   ├── local/
│       │   │   └── AppDatabase.kt   // Lớp trừu tượng của Room DB
│       │   └── network/
│       │       ├── ApiClient.kt     // Cấu hình Retrofit, OkHttp
│       │       └── AuthInterceptor.kt
│       ├── domain/
│       │   └── model/
│       │       └── Result.kt        // Lớp Result wrapper chung (Success, Error)
│       ├── ui/
│       │   ├── components/          // Các Composable dùng chung toàn app
│       │   │   ├── LoadingDialog.kt
│       │   │   ├── ErrorMessage.kt
│       │   │   └── PrimaryButton.kt
│       │   └── theme/               // Theme, Color, Typography, Shape
│       │       ├── Color.kt
│       │       ├── Shape.kt
│       │       ├── Theme.kt
│       │       └── Type.kt
│       └── util/                    // Các lớp tiện ích, extensions
│           ├── Constants.kt
│           └── extensions/
│               └── StringExt.kt
│
├── feature_auth/                    // Module tính năng: Xác thực
│   ├── build.gradle.kts
│   └── src/main/java/com/smartblood/auth/
│       ├── data/
│       │   ├── local/               // Dữ liệu cục bộ (ví dụ: lưu session token)
│       │   │   └── AuthLocalDataSource.kt
│       │   ├── mapper/              // Ánh xạ giữa DTO -> Domain Model
│       │   │   └── UserMapper.kt
│       │   ├── remote/
│       │   │   ├── AuthApiService.kt // Interface Retrofit/Firebase function
│       │   │   └── dto/             // Data Transfer Objects
│       │   │       ├── LoginRequestDto.kt
│       │   │       └── UserDto.kt
│       │   └── repository/
│       │       └── AuthRepositoryImpl.kt // Implement interface từ Domain
│       ├── domain/
│       │   ├── model/               // Model sạch, chỉ chứa logic nghiệp vụ
│       │   │   └── User.kt
│       │   ├── repository/
│       │   │   └── AuthRepository.kt  // Interface (Hợp đồng) cho repository
│       │   └── usecase/             // Các trường hợp sử dụng cụ thể
│       │       ├── LoginUseCase.kt
│       │       ├── RegisterUseCase.kt
│       │       └── PerformFaceAuthUseCase.kt
│       ├── di/                      // DI cho module auth
│       │   └── AuthModule.kt
│       └── ui/
│           ├── navigation/          // Điều hướng trong feature
│           │   └── AuthNavigation.kt
│           ├── login/
│           │   ├── LoginScreen.kt
│           │   ├── LoginViewModel.kt
│           │   └── LoginContract.kt // Định nghĩa State, Event, Effect
│           └── register/
│               ├── RegisterScreen.kt
│               ├── RegisterViewModel.kt
│               └── RegisterContract.kt
│
├── feature_profile/                 // Module tính năng: Hồ sơ
│   ├── build.gradle.kts
│   └── src/main/java/com/smartblood/profile/
│       ├── data/
│       │   ├── mapper/
│       │   │   └── DonationHistoryMapper.kt
│       │   ├── remote/...
│       │   └── repository/
│       │       └── ProfileRepositoryImpl.kt
│       ├── domain/
│       │   ├── model/
│       │   │   ├── UserProfile.kt
│       │   │   └── DonationRecord.kt
│       │   ├── repository/
│       │   │   └── ProfileRepository.kt
│       │   └── usecase/
│       │       ├── GetUserProfileUseCase.kt
│       │       └── GetDonationHistoryUseCase.kt
│       ├── di/
│       │   └── ProfileModule.kt
│       └── ui/
│           ├── navigation/
│           │   └── ProfileNavigation.kt
│           ├── profile_detail/
│           │   ├── ProfileScreen.kt
│           │   └── ProfileViewModel.kt
│           └── donation_history/
│               ├── DonationHistoryScreen.kt
│               └── DonationHistoryViewModel.kt
│
feature_map_booking/
└── src/main/java/com/smartblood/mapbooking/
    ├── data/
    │   ├── local/
    │   │   ├── dao/
    │   │   │   └── HospitalDao.kt           // Interface Room DAO cho Hospital
    │   │   └── entity/
    │   │       └── HospitalEntity.kt        // Bảng Hospital trong DB cục bộ để cache
    │   ├── mapper/
    │   │   ├── HospitalMapper.kt            // Chuyển đổi HospitalEntity/Dto -> Hospital
    │   │   └── AppointmentMapper.kt         // Chuyển đổi AppointmentDto -> Appointment
    │   ├── remote/
    │   │   ├── MapBookingApiService.kt      // Interface Retrofit/Firebase cho API bản đồ
    │   │   └── dto/
    │   │       ├── HospitalDto.kt           // DTO cho thông tin bệnh viện
    │   │       ├── AvailableSlotsDto.kt     // DTO cho các khung giờ còn trống
    │   │       └── BookingRequestDto.kt     // DTO để gửi yêu cầu đặt lịch
    │   └── repository/
    │       └── MapBookingRepositoryImpl.kt  // Triển khai repository, quyết định lấy dữ liệu từ local/remote
    │
    ├── domain/
    │   ├── model/
    │   │   ├── Hospital.kt                  // Model sạch của Bệnh viện
    │   │   ├── Appointment.kt               // Model sạch của Lịch hẹn
    │   │   └── TimeSlot.kt                  // Model sạch của Khung giờ
    │   ├── repository/
    │   │   └── MapBookingRepository.kt      // Interface định nghĩa các hàm cần thiết (getHospitals, bookAppointment,...)
    │   └── usecase/
    │       ├── GetNearbyHospitalsUseCase.kt // Use case lấy danh sách bệnh viện gần đây
    │       ├── GetHospitalDetailsUseCase.kt // Use case lấy chi tiết một bệnh viện
    │       ├── GetAvailableSlotsUseCase.kt  // Use case lấy các khung giờ trống
    │       └── BookAppointmentUseCase.kt    // Use case thực hiện đặt lịch hẹn
    │
    ├── di/
    │   └── MapBookingModule.kt              // Hilt module cung cấp Repository và Use Cases
    │
    └── ui/
        ├── navigation/
        │   └── MapBookingNavigation.kt      // Định nghĩa các route và hàm điều hướng cho module
        ├── map/
        │   ├── components/
        │   │   ├── HospitalMarker.kt        // Composable cho marker trên bản đồ
        │   │   └── FilterBottomSheet.kt     // Composable cho bộ lọc
        │   ├── MapScreen.kt                 // Màn hình chính hiển thị bản đồ
        │   ├── MapViewModel.kt              // ViewModel quản lý state bản đồ, danh sách bệnh viện
        │   └── MapContract.kt               // Định nghĩa State, Event, Effect cho MapScreen
        ├── location_detail/
        │   ├── LocationDetailScreen.kt      // Màn hình hiển thị chi tiết một địa điểm
        │   └── LocationDetailViewModel.kt   // ViewModel lấy dữ liệu chi tiết
        └── booking/
            ├── components/
            │   ├── CalendarView.kt          // Composable cho giao diện lịch
            │   └── TimeSlotGrid.kt          // Composable cho lưới chọn giờ
            ├── BookingScreen.kt             // Màn hình đặt lịch
            └── BookingViewModel.kt          // ViewModel xử lý logic chọn ngày/giờ và đặt lịch
feature_emergency/
└── src/main/java/com/smartblood/emergency/
    ├── data/
    │   ├── mapper/
    │   │   └── BloodRequestMapper.kt        // Chuyển đổi BloodRequestDto -> BloodRequest
    │   ├── remote/
    │   │   ├── EmergencyApiService.kt       // Interface cho các API liên quan đến yêu cầu khẩn cấp
    │   │   └── dto/
    │   │       ├── BloodRequestDto.kt       // DTO cho yêu cầu máu
    │   │       └── CreateRequestDto.kt      // DTO để tạo yêu cầu mới
    │   └── repository/
    │       └── EmergencyRepositoryImpl.kt   // Triển khai repository
    │
    ├── domain/
    │   ├── model/
    │   │   ├── BloodRequest.kt              // Model sạch cho yêu cầu máu
    │   │   └── RequestStatus.kt             // Enum cho trạng thái yêu cầu (PENDING, ACTIVE, COMPLETED)
    │   ├── repository/
    │   │   └── EmergencyRepository.kt       // Interface repository
    │   └── usecase/
    │       ├── CreateEmergencyRequestUseCase.kt // Use case tạo yêu cầu khẩn cấp
    │       └── GetMyRequestsUseCase.kt      // Use case lấy danh sách các yêu cầu đã tạo
    │
    ├── di/
    │   └── EmergencyModule.kt               // Hilt module
    │
    └── ui/
        ├── navigation/
        │   └── EmergencyNavigation.kt       // Điều hướng trong module
        ├── create_request/
        │   ├── CreateRequestScreen.kt       // Màn hình form tạo yêu cầu
        │   ├── CreateRequestViewModel.kt    // ViewModel xử lý validation và gửi form
        │   └── CreateRequestContract.kt     // Định nghĩa State, Event, Effect
        └── manage_requests/
            ├── components/
            │   └── RequestListItem.kt       // Composable hiển thị một yêu cầu trong danh sách
            ├── ManageRequestsScreen.kt      // Màn hình danh sách các yêu cầu đã tạo
            └── ManageRequestsViewModel.kt   // ViewModel lấy và quản lý danh sách yêu cầu
feature_chatbot/
└── src/main/java/com/smartblood/chatbot/
    ├── data/
    │   ├── local/
    │   │   ├── dao/
    │   │   │   └── ChatMessageDao.kt        // Room DAO để lưu lịch sử chat
    │   │   └── entity/
    │   │       └── ChatMessageEntity.kt     // Bảng ChatMessage trong DB
    │   ├── mapper/
    │   │   └── ChatMessageMapper.kt         // Chuyển đổi giữa Entity/Dto và Model
    │   ├── remote/
    │   │   ├── ChatbotApiService.kt         // Interface API để giao tiếp với Dialogflow/Gemini
    │   │   └── dto/
    │   │       ├── ChatRequestDto.kt        // DTO gửi tin nhắn lên server
    │   │       └── ChatResponseDto.kt       // DTO nhận tin nhắn trả về
    │   └── repository/
    │       └── ChatbotRepositoryImpl.kt     // Triển khai repository, gửi tin nhắn và lưu lịch sử
    │
    ├── domain/
    │   ├── model/
    │   │   ├── ChatMessage.kt               // Model sạch cho một tin nhắn
    │   │   └── SenderType.kt                // Enum người gửi (USER, BOT)
    │   ├── repository/
    │   │   └── ChatbotRepository.kt         // Interface repository
    │   └── usecase/
    │       ├── SendMessageUseCase.kt        // Use case gửi một tin nhắn
    │       └── GetChatHistoryUseCase.kt     // Use case lấy lịch sử cuộc trò chuyện
    │
    ├── di/
    │   └── ChatbotModule.kt                 // Hilt module
    │
    └── ui/
        ├── navigation/
        │   └── ChatbotNavigation.kt         // Điều hướng cho màn hình chat
        └── chat/
            ├── components/
            │   ├── ChatBubble.kt            // Composable cho bong bóng chat (gửi và nhận)
            │   ├── MessageInputField.kt     // Composable cho ô nhập tin nhắn
            │   └── TypingIndicator.kt       // Composable cho hiệu ứng "Bot is typing..."
            ├── ChatbotScreen.kt             // Màn hình chat chính
            ├── ChatbotViewModel.kt          // ViewModel quản lý danh sách tin nhắn, trạng thái đang gõ
            └── ChatbotContract.kt           // Định nghĩa State, Event, Effect
  ```    

---

### **HƯỚNG DẪN CÀI ĐẶT VÀ CHẠY DỰ ÁN (PROJECT SETUP GUIDE)**

Quy trình này sẽ hướng dẫn bạn cách clone, cài đặt và chạy dự án **Smart Blood Donation** trên máy tính của bạn.

#### **Giai đoạn 0: Yêu Cầu Cần Có (Prerequisites)**

Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt các công cụ sau:

1.  **Git:** Hệ thống quản lý phiên bản. Nếu chưa có, bạn có thể tải tại [git-scm.com](https://git-scm.com/).
2.  **Android Studio:** Môi trường phát triển chính. Khuyến nghị sử dụng phiên bản mới nhất (Iguana 2023.2.1 hoặc mới hơn).
    *   Tải tại: [developer.android.com/studio](https://developer.android.com/studio)
    *   Trong quá trình cài đặt, hãy đảm bảo bạn đã chọn cài đặt **Android SDK**. Android Studio thường sẽ tự động cài đặt JDK (Java Development Kit) đi kèm, vì vậy bạn không cần cài đặt Java riêng.

#### **Giai đoạn 1: Lấy Mã Nguồn Dự Án (Cloning the Repository)**

Bạn cần sao chép (clone) mã nguồn từ GitHub về máy tính của mình.

1.  **Lấy URL của Repository:**
    *   Truy cập trang repository của dự án trên GitHub.
    *   Nhấn vào nút màu xanh lá **"<> Code"**.
    *   Chọn tab **HTTPS** và sao chép URL. (Ví dụ: `https://github.com/TenNguoiDung/SmartBloodDonation-Android.git`)

2.  **Thực hiện Clone:**
    Bạn có thể dùng một trong hai cách sau:

    *   **Cách A: Dùng Terminal (Command Line)**
        ```bash
        # Mở Terminal (hoặc Git Bash trên Windows)
        # Di chuyển đến thư mục bạn muốn lưu dự án (ví dụ: D:\Projects)
        cd D:\Projects

        # Chạy lệnh clone với URL bạn đã sao chép
        git clone https://github.com/TenNguoiDung/SmartBloodDonation-Android.git

        # Di chuyển vào thư mục dự án vừa được tạo
        cd SmartBloodDonation-Android
        ```

    *   **Cách B: Dùng Android Studio (Khuyến khích)**
        *   Mở Android Studio.
        *   Trên màn hình chào mừng, chọn **"Get from VCS"** (Lấy từ Hệ thống quản lý phiên bản).
        *   Dán URL bạn đã sao chép vào ô **URL**.
        *   Chọn thư mục trên máy tính của bạn ở ô **Directory**.
        *   Nhấn **"Clone"**. Android Studio sẽ tự động tải dự án về và mở nó ra.

#### **Giai đoạn 2: Lần Mở Đầu Tiên và Đồng Bộ Hóa Gradle (First Open & Sync)**

Đây là bước tự động nhưng quan trọng nhất. Hãy kiên nhẫn.

1.  **Mở Dự Án:**
    *   Nếu bạn dùng cách B, dự án sẽ được mở tự động.
    *   Nếu bạn dùng cách A, trong Android Studio, chọn **File -> Open** và trỏ đến thư mục `SmartBloodDonation-Android` bạn vừa clone về.

2.  **Chờ Đợi Quá Trình Đồng Bộ Hóa Tự Động:**
    *   Ngay khi dự án được mở, Android Studio sẽ bắt đầu một loạt các tác vụ nền. Bạn có thể theo dõi tiến trình ở thanh trạng thái dưới cùng bên phải.
    *   **Điều gì đang xảy ra?**
        *   Android Studio đọc file `gradle/wrapper/gradle-wrapper.properties` và thấy dự án yêu cầu **Gradle phiên bản 8.6**.
        *   Nó sẽ **tự động tải về Gradle 8.6** (việc này có thể mất vài phút nếu đây là lần đầu bạn dùng phiên bản này).
        *   Sau đó, Gradle sẽ đọc tất cả các file `build.gradle.kts`, `settings.gradle.kts`, và `gradle/libs.versions.toml`.
        *   Nó sẽ **tải về tất cả các thư viện (dependencies)** và **plugins** được định nghĩa trong dự án.
        *   Cuối cùng, nó sẽ lập chỉ mục (indexing) toàn bộ file trong dự án.

    **LƯU Ý QUAN TRỌNG:** **KHÔNG LÀM GÌ CẢ** cho đến khi tất cả các thanh tiến trình ở góc dưới bên phải biến mất và bạn không còn thấy thông báo "Syncing project..." hay "Gradle build running...". Việc can thiệp có thể làm hỏng quá trình cài đặt ban đầu.

#### **Giai đoạn 3: Build và Chạy Ứng Dụng**

Sau khi quá trình đồng bộ hoàn tất, bạn đã sẵn sàng để chạy ứng dụng.

1.  **Chọn Thiết Bị Chạy:**
    *   Ở thanh công cụ trên cùng, bạn sẽ thấy một danh sách thả xuống các thiết bị (thường có chữ 'app' bên cạnh).
    *   **Nếu dùng máy thật:** Kết nối điện thoại của bạn với máy tính và bật chế độ **"USB Debugging"** (Gỡ lỗi qua USB) trong Tùy chọn nhà phát triển.
    *   **Nếu dùng máy ảo:** Chọn một máy ảo có sẵn. Nếu chưa có, hãy vào **Tools -> Device Manager** để tạo một máy ảo mới (khuyến nghị API 34).

2.  **Chạy Ứng Dụng:**
    *   Nhấn vào nút **Run 'app'** (biểu tượng hình tam giác màu xanh lá cây) ở thanh công cụ trên cùng.
    *   Gradle sẽ biên dịch toàn bộ dự án. Lần build đầu tiên có thể mất vài phút.
    *   Nếu không có lỗi, ứng dụng sẽ được cài đặt và tự động mở trên thiết bị bạn đã chọn.

#### **Giai đoạn 4: Xử Lý Các Vấn Đề Thường Gặp (Troubleshooting)**

Nếu bạn gặp lỗi trong quá trình build, hãy thử các bước sau theo thứ tự:

1.  **Clean and Rebuild Project:**
    *   Vào **Build -> Clean Project**.
    *   Sau khi hoàn tất, vào **Build -> Rebuild Project**.

2.  **Invalidate Caches / Restart (Giải pháp hiệu quả nhất):**
    *   Đây là cách giải quyết hầu hết các lỗi "kỳ lạ" của Gradle hoặc Android Studio.
    *   Vào **File -> Invalidate Caches...**
    *   Trong hộp thoại hiện ra, tick vào ô đầu tiên và nhấn **"Invalidate and Restart"**. Android Studio sẽ khởi động lại và dọn dẹp toàn bộ cache.

3.  **Kiểm Tra Lại SDK Location:**
    *   Vào **File -> Project Structure... -> SDK Location**.
    *   Đảm bảo đường dẫn Android SDK là chính xác. Nếu không, hãy chọn lại.

