此專案為電商平台，包含購物車、訂單管理、支付整合、用戶評價系統、管理員與會員的即時聊天等功能，並使用 ECPay 支付、WebSocket 聊天、Log4j2 日誌管理等技術。
介紹一下我做的部分
1.購物車功能
點擊「加入購物車」按鈕觸發 onclick 事件，使用 Axios 將商品添加到購物車，並自動刷新右上角的數量，將購物資料存入資料庫。
![image](https://github.com/user-attachments/assets/57edaace-31d5-458e-85b7-ebf71d294e5e)
購物車畫面可以選擇需要的商品結帳
![image](https://github.com/user-attachments/assets/93a2cda1-5f36-4888-8bea-1eba8c658f35)
2.訂單管理
在購物車頁面選擇結帳商品，進入訂單確認頁面生成訂單及訂單明細。
填寫收件人資訊、選擇優惠券，並計算運費，系統更新訂單總價和收件人資訊。
![image](https://github.com/user-attachments/assets/4dda947f-e068-49d3-8f47-5368bb0c51d0)
在管理員頁面把訂單狀態改成已完成會送出mail到信箱
![image](https://github.com/user-attachments/assets/75f4eaf4-6a81-4cb2-af6d-6a4f24cb0636)
![image](https://github.com/user-attachments/assets/9be19f1c-7f57-4503-bf46-968f14b0e901)
訂單狀態為已付款和已完成的訂單都會顯示在月消費的圖表和單月消費的金額上
![image](https://github.com/user-attachments/assets/ee391a83-cd3d-4e6e-9ef5-885ccab5f5d8)
3.評價系統
選擇評價可以到評價頁面,訂單已完成後才已訂單明細進行評價
![image](https://github.com/user-attachments/assets/017d6945-a182-4cbc-b462-c0ac36b2f2f2)
![image](https://github.com/user-attachments/assets/a143c57c-e070-4c59-820b-dec632ef3afd)
![image](https://github.com/user-attachments/assets/4ee0ee57-6dcf-44db-866d-335abfca3f64)
4.即時聊天系統
會員和管理員可透過 WebSocket 進行即時聊天，並將聊天記錄儲存在資料庫中。當管理員在與不同用戶對話時，會顯示紅色警示燈。
![image](https://github.com/user-attachments/assets/e36e61e5-6c42-41a7-b000-6b4cc151b93e)
![image](https://github.com/user-attachments/assets/ad2fe7c3-1485-4e45-a6f1-50c7296d404a)
5.日誌紀錄
使用 Log4j2 紀錄會員登錄資訊，並輸出為日誌文件，便於日後分析。
![image](https://github.com/user-attachments/assets/ea7f2db2-c463-4e69-b924-a1cf7c64571e)
6.會員簽到功能
基於redis使用簽到,只有單單驗證是否簽到並無其他功能
![image](https://github.com/user-attachments/assets/92c2ea72-34ca-40ad-8d01-f57899104996)
