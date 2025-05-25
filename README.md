# eFlex - Nền tảng hỗ trợ học tập cá nhân hóa

**eFlex** là một hệ thống web thông minh hỗ trợ học tập được xây dựng với mục tiêu **cá nhân hóa lộ trình học** theo năng lực người dùng. Hệ thống cung cấp các tính năng đánh giá năng lực, đề xuất khóa học, và chatbot học tập thông minh — tất cả trong một nền tảng duy nhất.

> **Tech Stack**: Java, Spring Boot, JavaMail, OAuth 2.0, JWT, WebSocket, MySQL, Redis, Cloudinary, Swagger

---

##  Tính năng chính

###  Xác thực & Phân quyền an toàn
-  Đăng nhập qua **Google OAuth2** và đăng ký bằng **email/password**
-  Xác minh tài khoản qua **email** với **JavaMail**
-  Bảo mật API bằng **JWT**

###  Quản lý người dùng & khóa học
-  CRUD cho **Users, Courses, Lessons, Test Exercises**
-  Phân quyền theo vai trò: **user/admin**

###  WebSocket & Redis
-  Theo dõi trạng thái người dùng **real-time**
-  Cache dữ liệu người dùng và session bằng **Redis** để tối ưu hiệu suất

###  Chatbot học tập sử dụng Gemini API
-  Trợ lý học tập thông minh giúp trả lời câu hỏi và hướng dẫn người học tự động

###  Thuật toán gợi ý khóa học (Recommendation System)
-  Dựa trên **Collaborative Filtering** từ hành vi học tập
-  Gợi ý nội dung học phù hợp với **tiến độ & sở thích cá nhân**

###  Tích hợp Cloudinary
-  Lưu trữ và quản lý ảnh bài học, khóa học một cách tối ưu

###  Tài liệu API tự động với Swagger
-  Dễ dàng khám phá và thử nghiệm API trực tiếp trên giao diện Swagger UI


---

##  Giao diện hệ thống

![eFlex Screenshot](./HomePage.png)

---
##  Chạy ứng dụng 
**BackEnd**
1. Clone dự án: `git clone https://github.com/imthq1/EduFlex`
2. cd Backend
3. mvn spring-boot:run


## API DOCUMENT
### USER
![image](https://github.com/user-attachments/assets/d7166c30-8b97-4d8c-bc35-fddcdccd4240)

### AUTH
![image](https://github.com/user-attachments/assets/c78869ee-dceb-4b68-879b-9b4e5a30cff7)
### TEST EXERCISER AND EXERCISE
![image](https://github.com/user-attachments/assets/e4840d0a-dd15-4f3e-b0fb-a5d8c6604877)
### CATEGORY, FILE UPLOAD AND HISTORY LEARN
![image](https://github.com/user-attachments/assets/32fb178c-fed5-4bfa-9234-31c5d13e2310)
### SECTION, PROGRESS SECTION AND LESSON
![image](https://github.com/user-attachments/assets/6e23262f-a2dd-4ced-9393-2e5ced13c2ee)
### COURSE, CHATBOT
![image](https://github.com/user-attachments/assets/7391c50a-ddea-476c-86dc-72527d5b5b79)

##  Đóng góp
Rất mong nhận được sự đóng góp từ cộng đồng! Hãy mở Pull Request hoặc liên hệ qua email: nguyentoanthang190404@gmail.com.

---

Chúng tôi hy vọng eFlex sẽ trở thành một công cụ hữu ích, mang lại trải nghiệm học tập hiệu quả và thú vị cho bạn!
