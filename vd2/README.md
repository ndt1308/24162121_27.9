# springboot1-10 – IOTSTAR SHOP (Spring Boot 4.1.1 + Spring Security + MapStruct + Thymeleaf + SQL Server)

## Chạy
1. Tạo database rỗng trong SQL Server (vd: `webst10`).
2. Copy `.env.example` thành `.env` (cùng cấp `pom.xml`), điền DB, Gmail App Password, Cloudinary, ADMIN_EMAIL/ADMIN_PASSWORD.
3. `mvnw spring-boot:run` (hoặc Run As > Spring Boot App trong Eclipse). Hibernate tự tạo bảng, DataInitializer tạo role USER/ADMIN và 1 tài khoản admin.

## Chức năng và URL
| Chức năng | URL |
|---|---|
| Đăng ký (username, email, họ tên, mật khẩu) -> OTP mail -> kích hoạt | /register, /verify-otp, /register/resend-otp |
| Đăng nhập bằng **username hoặc email** (session) / đăng xuất; header hiển thị avatar + họ tên | /login, /logout |
| Quên mật khẩu -> OTP mail -> đặt lại | /forgot-password, /reset-password |
| Dashboard (đếm user, product, product của tôi) | /dashboard |
| CRUD user + tìm kiếm + phân trang + số product (ADMIN) | /users |
| CRUD product + upload Cloudinary + tìm kiếm + phân trang | /products |

## Kiểm thử nhanh
1. Đăng nhập admin -> /users, /products, /dashboard.
2. Đăng ký email thật -> nhận OTP -> nhập -> đăng nhập.
3. Đăng nhập USER -> thử vào /users (bị chuyển sang /access-denied).
4. Thêm product có ảnh -> kiểm tra ảnh trong Cloudinary folder `iotstar/products`.

## Lưu ý khi nâng cấp từ bản cũ (bảng `users` thêm cột `username`, `images`)
Cột `username` là NOT NULL nên Hibernate `ddl-auto=update` **không thêm được** vào bảng đã có dữ liệu.
Hãy xóa các bảng cũ (thứ tự: `otp_tokens`, `products`, `users`, `roles`) hoặc tạo lại database rỗng rồi chạy lại.
`.env` cần thêm `ADMIN_USERNAME` (mặc định `admin`).

## Cách hoạt động của custom login
- `CustomUserDetailsService` tìm user theo `username` **hoặc** `email` (không phân biệt hoa thường).
- `CustomUserDetails` (principal) mang thêm `id, email, fullName, images, role`.
- `fragments/header.html` đọc `#authentication.principal` để hiện avatar (`images`, rỗng -> `/images/avatar-default.svg`), họ tên, username, email, role.
- `auth.getName()` bây giờ trả về **username**, nên code cần email dùng `SecurityUtils.email(auth)`.
