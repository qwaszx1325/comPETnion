# 電商平台專案

此專案為一個完整的電商平台，包含購物車、訂單管理、支付整合、用戶評價系統、即時聊天等功能。使用了 **ECPay** 支付、**WebSocket** 聊天、**Log4j2** 日誌管理、**Redis** 簽到功能等技術，實現了會員與管理員之間的即時互動和詳盡的訂單流程管理。

---

## 功能特點

### 1. 購物車功能
- 點擊「加入購物車」按鈕會觸發 `onclick` 事件，並使用 **Axios** 將商品加入購物車。
- 畫面右上角的購物車數量會自動刷新，且購物資料會同步存入資料庫。

![加入購物車](https://github.com/user-attachments/assets/57edaace-31d5-458e-85b7-ebf71d294e5e)

- 購物車畫面可以選擇結帳商品。

![購物車頁面](https://github.com/user-attachments/assets/93a2cda1-5f36-4888-8bea-1eba8c658f35)

### 2. 訂單管理
- 在購物車頁面選擇商品結帳後，系統生成訂單及訂單明細。
- 填寫收件人資訊、選擇優惠券、並計算運費，系統會更新訂單總價和收件人資訊。

![訂單確認頁面](https://github.com/user-attachments/assets/4dda947f-e068-49d3-8f47-5368bb0c51d0)

- 管理員可以在管理後台更改訂單狀態至「已完成」，並自動發送確認郵件到用戶的信箱。

![訂單狀態更新](https://github.com/user-attachments/assets/75f4eaf4-6a81-4cb2-af6d-6a4f24cb0636)
![郵件通知](https://github.com/user-attachments/assets/9be19f1c-7f57-4503-bf46-968f14b0e901)

- 訂單狀態為「已付款」或「已完成」的訂單會顯示在月消費圖表及單月消費金額統計中。

![月消費圖表](https://github.com/user-attachments/assets/ee391a83-cd3d-4e6e-9ef5-885ccab5f5d8)

### 3. 評價系統
- 用戶可在訂單完成後對商品進行評價，進一步增進商品反饋。
  
![評價頁面](https://github.com/user-attachments/assets/017d6945-a182-4cbc-b462-c0ac36b2f2f2)
![評價明細](https://github.com/user-attachments/assets/a143c57c-e070-4c59-820b-dec632ef3afd)

### 4. 即時聊天系統
- 會員和管理員可透過 **WebSocket** 進行即時聊天，且聊天記錄會同步儲存在資料庫中。
- 當管理員在與不同用戶對話時，系統會顯示紅色警示燈提醒。

![即時聊天](https://github.com/user-attachments/assets/e36e61e5-6c42-41a7-b000-6b4cc151b93e)
![警示燈](https://github.com/user-attachments/assets/ad2fe7c3-1485-4e45-a6f1-50c7296d404a)

### 5. 日誌紀錄
- 使用 **Log4j2** 紀錄會員登錄資訊，並輸出為日誌文件，以便後續分析使用者行為和系統運作狀況。

![日誌紀錄](https://github.com/user-attachments/assets/ea7f2db2-c463-4e69-b924-a1cf7c64571e)

### 6. 會員簽到功能
- 使用 **Redis** 實現的會員簽到功能，主要功能是驗證當日是否簽到。

![簽到頁面](https://github.com/user-attachments/assets/92c2ea72-34ca-40ad-8d01-f57899104996)

---

## 技術架構

- **後端框架**：Spring Boot
- **模板引擎**：Thymeleaf
- **支付系統**：ECPay
- **即時通訊**：WebSocket
- **前端請求**：Axios
- **日誌管理**：Log4j2
- **快取及簽到**：Redis

---

## 環境設置

1. **配置文件**：在 `src/main/resources` 下新增 `application.properties`，並配置基本的 SQL Server 和 Spring Mail 設定。
2. **資料庫設置**：由於本專案使用了 SQL Server 的視圖，若直接使用 Spring JPA 自動生成會遇到問題，請依照 `model/order` 中的表關係手動建立相應的 Entity 及表結構。
   
---

以上為電商平台的主要功能及技術架構，感謝您的閱讀與參與測試！
