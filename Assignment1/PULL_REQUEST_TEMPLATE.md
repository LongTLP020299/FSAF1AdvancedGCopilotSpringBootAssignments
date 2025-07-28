Task 1: Refactoring placeOrder

\- Phương thức placeOrder gốc đang để quá nhiều logic trong 1 method, 1 method làm quá nhiều chức năng

\- Phương thức placeOrder mới tách ra nhiều method, mỗi method có 1 nhiệm vụ cụ thể, code dễ đọc, có thẻ test riêng từng method

&nbsp;

Task 2: Debugging

Bug C về missing @Transactional là khó nhất vì:

\- Không có compile error mà code vẫn chạy bình thường

\- Chỉ fail trong điều kiện đặc biệt và ảnh hưởng nghiêm trọng đến tính nhất quán của dữ liệu

\- Khó tái hiện trong môi trường test

&nbsp;

What makes a good prompt for debugging?

\- Cần hiểu sâu về transaction management

\- Mô tả chính xác symptom và expected behavior

\- Cung cấp stack trace, error messages

\- Có steps để reproduce bug

\- Environment: Database, Spring Boot version, etc.



Task3: 

Cross-Site Scripting (XSS)

