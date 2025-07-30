1. Explain the trade-offs of caching. When might caching the findProductById result be a bad idea (e.g., if product prices change very frequently)?

Ưu điểm của caching:
-Giảm số lượng truy vấn DB , tăng hiệu suất, giảm độ trễ cho người dùng.
-Tăng khả năng chịu tải: các request giống nhau không cần tính toán lại.
-Giảm chi phí tài nguyên backend.

Nhược điểm / Trade-offs:
-Dữ liệu có thể khong realtime nếu không cache đúng cách.
-Quản lý cache phức tạp: cần cache invalidation (xóa cache khi có thay đổi).
-Không phù hợp với dữ liệu thay đổi thường xuyên.

Khi nào không nên cache findProductById:
-Khi giá sản phẩm, khuyến mãi, số lượng tồn kho thay đổi thường xuyên.
-Nếu có tính năng flash sale hoặc real-time pricing.

2. What is the difference between a full table scan and an index scan in a database? Why did adding an @Index in Task 2 significantly improve query performance?
Full Table Scan:
-Duyệt qua toàn bộ bảng, từng hàng một, để tìm bản ghi thỏa mãn điều kiện.
-Rất tốn tài nguyên và chậm trên bảng lớn (hàng chục nghìn bản ghi trở lên).

Index Scan:
-DB sử dụng một cấu trúc dữ liệu chỉ mục (index) để tìm bản ghi nhanh hơn nhiều.
-Chỉ truy cập vào các phần cần thiết.

Vì sao @Index trong Task 2 giúp cải thiện rõ rệt:
-Phương thức searchProducts dùng LIKE hoặc WHERE name = ?, truy vấn theo name cột không index sẽ rất chậm.
-Khi thêm @Index(name = "idx_product_name", columnList = "name"), sẽ tạo chỉ mục SQL thì DB thực hiện index scan thay vì full scan.
-Giúp giảm thời gian truy vấn table product có nhiều dữ liệu.

3. Why are default health checks (like disk space, database connection) not always enough? Provide an example of a business-critical dependency for our e-commerce app that would require a custom health indicator.
🔹 Giới hạn của health check mặc định:
Spring Boot Actuator mặc định cung cấp một số health checks như:
-Kiểm tra kết nối DB.
-Kiểm tra dung lượng ổ đĩa còn đủ không.
-Kiểm tra thread pool, memory, etc.

=> Tuy nhiên, những thứ này chỉ phản ánh tình trạng hệ thống nội bộ, không phản ánh toàn bộ hệ sinh thái dịch vụ.

🔹 Vì sao cần thêm custom health check:
Trong ứng dụng thương mại điện tử, ta thường phụ thuộc vào dịch vụ bên ngoài (third-party) để hoàn thành nghiệp vụ.
-Nếu những dịch vụ này không hoạt động, dù server vẫn "UP" theo actuator mặc định nhưng hệ thống thực chất vẫn đang gặp sự cố.

🔹 Ví dụ điển hình: Cổng thanh toán (Payment Gateway)
Ứng dụng phụ thuộc vào cổng thanh toán như Stripe, PayPal, Momo...

Nếu không thể kết nối tới API của PayPal:
-Người dùng không thể thanh toán.
-Đơn hàng không thể hoàn tất.
-Doanh thu bị ảnh hưởng trực tiếp.
