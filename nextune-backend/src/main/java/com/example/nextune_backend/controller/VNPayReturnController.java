package com.example.nextune_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment/vnpay")
@RequiredArgsConstructor
public class VNPayReturnController {

    // Tuỳ bạn: có thể chỉ hiển thị, hoặc redirect về trang FE kèm query
    @GetMapping("/return")
    public ResponseEntity<Map<String, String>> handleReturn(@RequestParam Map<String, String> params) {
        // KHÔNG cập nhật DB ở đây. IPN mới là nguồn sự thật.
        // Ở đây chỉ trả kết quả để FE hiển thị cho user.
        // Bạn có thể lọc những key cần thiết:
        Map<String, String> result = new LinkedHashMap<>();
        result.put("vnp_ResponseCode", params.get("vnp_ResponseCode"));     // "00" nếu user thao tác OK
        result.put("vnp_TransactionStatus", params.get("vnp_TransactionStatus")); // "00" nếu giao dịch thành công
        result.put("vnp_TxnRef", params.get("vnp_TxnRef"));
        result.put("vnp_Amount", params.get("vnp_Amount"));
        result.put("vnp_BankCode", params.get("vnp_BankCode"));
        result.put("message", "Đây là trang return. Kết quả chính thức sẽ được xác nhận qua IPN.");

        return ResponseEntity.ok(result);
    }

    // Nếu muốn redirect FE:
    // @GetMapping("/return")
    // public void handleReturn(@RequestParam Map<String,String> params, HttpServletResponse resp) throws IOException {
    //     String fe = "https://your-frontend/payment-result?" + URLEncoder.encode(...);
    //     resp.sendRedirect(fe);
    // }
}
