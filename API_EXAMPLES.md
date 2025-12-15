# API Examples & Test Cases

This document provides curl examples and test cases for all the implemented APIs.

## Prerequisites
- Base URL: `http://localhost:8080`
- Replace `<token>` with actual JWT token

---

## CART MANAGEMENT EXAMPLES

### 1. Add Product to Cart
```bash
curl -X POST http://localhost:8080/api/products/1/quantity/2 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Product added to cart successfully",
  "timestamp": "2025-12-10T14:30:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 1000.0,
    "products": [
      {
        "productId": 1,
        "productName": "Laptop",
        "price": 500.0,
        "specialPrice": 450.0,
        "quantity": 2,
        "discount": 10
      }
    ]
  },
  "addedProduct": {
    "productId": 1,
    "productName": "Laptop",
    "price": 500.0,
    "specialPrice": 450.0,
    "quantity": 2,
    "discount": 10
  },
  "quantityAdded": 2,
  "priceAdded": 900.0,
  "status": "201 CREATED"
}
```

### 2. Get Cart Details
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart details retrieved successfully",
  "timestamp": "2025-12-10T14:31:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 1500.0,
    "products": [
      {
        "productId": 1,
        "productName": "Laptop",
        "price": 500.0,
        "specialPrice": 450.0,
        "quantity": 2,
        "discount": 10
      },
      {
        "productId": 2,
        "productName": "Mouse",
        "price": 50.0,
        "specialPrice": 45.0,
        "quantity": 1,
        "discount": 10
      }
    ]
  },
  "status": "200 OK"
}
```

### 3. Update Product Quantity
```bash
curl -X PUT http://localhost:8080/api/cart/products/1/quantity/5 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Product quantity updated successfully",
  "timestamp": "2025-12-10T14:32:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 2295.0,
    "products": [
      {
        "productId": 1,
        "productName": "Laptop",
        "quantity": 5
      }
    ]
  },
  "quantityAdded": 5,
  "priceAdded": 2250.0,
  "status": "200 OK"
}
```

### 4. Delete Product from Cart
```bash
curl -X DELETE http://localhost:8080/api/cart/products/2 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Product deleted from cart successfully",
  "timestamp": "2025-12-10T14:33:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 2250.0,
    "products": [
      {
        "productId": 1,
        "productName": "Laptop",
        "quantity": 5
      }
    ]
  },
  "priceAdded": 45.0,
  "status": "200 OK"
}
```

### 5. Clear Cart
```bash
curl -X DELETE http://localhost:8080/api/cart \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart cleared successfully",
  "timestamp": "2025-12-10T14:34:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 0.0,
    "products": []
  },
  "status": "200 OK"
}
```

---

## ADDRESS MANAGEMENT EXAMPLES

### 1. Create Address
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "country": "United States",
    "zipCode": "10001",
    "phoneNumber": "+1-212-555-1234",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": true
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Address created successfully",
  "timestamp": "2025-12-10T14:35:00",
  "address": {
    "addressId": 1,
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "country": "United States",
    "zipCode": "10001",
    "phoneNumber": "+1-212-555-1234",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": true
  },
  "status": "201 CREATED"
}
```

### 2. Get User Addresses
```bash
curl -X GET http://localhost:8080/api/addresses \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
  {
    "addressId": 1,
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "country": "United States",
    "zipCode": "10001",
    "phoneNumber": "+1-212-555-1234",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": true
  },
  {
    "addressId": 2,
    "street": "456 Oak Avenue",
    "city": "Los Angeles",
    "state": "CA",
    "country": "United States",
    "zipCode": "90001",
    "phoneNumber": "+1-213-555-5678",
    "recipientName": "Jane Smith",
    "addressType": "BILLING",
    "isDefault": false
  }
]
```

### 3. Get Default Address
```bash
curl -X GET http://localhost:8080/api/addresses/default \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "addressId": 1,
  "street": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "country": "United States",
  "zipCode": "10001",
  "phoneNumber": "+1-212-555-1234",
  "recipientName": "John Doe",
  "addressType": "SHIPPING",
  "isDefault": true
}
```

### 4. Update Address
```bash
curl -X PUT http://localhost:8080/api/addresses/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "789 Pine Road",
    "city": "Boston",
    "state": "MA",
    "country": "United States",
    "zipCode": "02101",
    "phoneNumber": "+1-617-555-9999",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": false
  }'
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Address updated successfully",
  "timestamp": "2025-12-10T14:36:00",
  "address": {
    "addressId": 1,
    "street": "789 Pine Road",
    "city": "Boston",
    "state": "MA",
    "country": "United States",
    "zipCode": "02101",
    "phoneNumber": "+1-617-555-9999",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": false
  },
  "status": "200 OK"
}
```

### 5. Set Default Address
```bash
curl -X PUT http://localhost:8080/api/addresses/2/set-default \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Address set as default successfully",
  "timestamp": "2025-12-10T14:37:00",
  "address": {
    "addressId": 2,
    "street": "456 Oak Avenue",
    "city": "Los Angeles",
    "state": "CA",
    "country": "United States",
    "zipCode": "90001",
    "phoneNumber": "+1-213-555-5678",
    "recipientName": "Jane Smith",
    "addressType": "BILLING",
    "isDefault": true
  },
  "status": "200 OK"
}
```

### 6. Delete Address
```bash
curl -X DELETE http://localhost:8080/api/addresses/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Address deleted successfully",
  "timestamp": "2025-12-10T14:38:00",
  "address": {
    "addressId": 1,
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "country": "United States",
    "zipCode": "10001",
    "phoneNumber": "+1-212-555-1234",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": true
  },
  "status": "200 OK"
}
```

---

## ORDER MANAGEMENT EXAMPLES

### 1. Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 2,
    "orderNotes": "Please deliver between 10 AM - 6 PM",
    "discountAmount": 50.0
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "timestamp": "2025-12-10T14:39:00",
  "order": {
    "orderId": 1,
    "userId": 1,
    "userName": "johndoe",
    "userEmail": "john@example.com",
    "addressId": 2,
    "shippingAddressDetails": "456 Oak Avenue, Los Angeles, CA, United States",
    "orderItems": [
      {
        "orderItemId": 1,
        "productId": 1,
        "productName": "Laptop",
        "quantity": 5,
        "pricePerUnit": 450.0,
        "discount": 0.0,
        "itemTotal": 2250.0
      }
    ],
    "orderStatus": "PENDING",
    "totalAmount": 2250.0,
    "discountAmount": 50.0,
    "finalAmount": 2200.0,
    "orderDate": "2025-12-10T14:39:00",
    "estimatedDeliveryDate": "2025-12-17T14:39:00",
    "deliveryDate": null,
    "orderNotes": "Please deliver between 10 AM - 6 PM"
  },
  "status": "201 CREATED"
}
```

### 2. Get User Orders
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
  {
    "orderId": 1,
    "userId": 1,
    "userName": "johndoe",
    "userEmail": "john@example.com",
    "addressId": 2,
    "shippingAddressDetails": "456 Oak Avenue, Los Angeles, CA, United States",
    "orderItems": [...],
    "orderStatus": "PENDING",
    "totalAmount": 2250.0,
    "discountAmount": 50.0,
    "finalAmount": 2200.0,
    "orderDate": "2025-12-10T14:39:00",
    "estimatedDeliveryDate": "2025-12-17T14:39:00"
  }
]
```

### 3. Get Order Details
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "orderId": 1,
  "userId": 1,
  "userName": "johndoe",
  "userEmail": "john@example.com",
  "addressId": 2,
  "shippingAddressDetails": "456 Oak Avenue, Los Angeles, CA, United States",
  "orderItems": [
    {
      "orderItemId": 1,
      "productId": 1,
      "productName": "Laptop",
      "quantity": 5,
      "pricePerUnit": 450.0,
      "discount": 0.0,
      "itemTotal": 2250.0
    }
  ],
  "orderStatus": "PENDING",
  "totalAmount": 2250.0,
  "discountAmount": 50.0,
  "finalAmount": 2200.0,
  "orderDate": "2025-12-10T14:39:00",
  "estimatedDeliveryDate": "2025-12-17T14:39:00",
  "deliveryDate": null,
  "orderNotes": "Please deliver between 10 AM - 6 PM"
}
```

### 4. Update Order Status
```bash
curl -X PUT http://localhost:8080/api/orders/1/status/CONFIRMED \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order status updated successfully",
  "timestamp": "2025-12-10T14:40:00",
  "order": {
    "orderId": 1,
    "orderStatus": "CONFIRMED",
    ...
  },
  "status": "200 OK"
}
```

### 5. Cancel Order
```bash
curl -X PUT http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "timestamp": "2025-12-10T14:41:00",
  "order": {
    "orderId": 1,
    "orderStatus": "CANCELLED",
    ...
  },
  "status": "200 OK"
}
```

### 6. Get Orders by Status
```bash
curl -X GET "http://localhost:8080/api/orders/status/PENDING" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### 7. Get Recent Orders
```bash
curl -X GET "http://localhost:8080/api/orders/recent?limit=5" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

---

## PAYMENT MANAGEMENT EXAMPLES

### 1. Process Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 2200.0,
    "paymentMethod": "CREDIT_CARD",
    "paymentGateway": "STRIPE"
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "timestamp": "2025-12-10T14:42:00",
  "payment": {
    "paymentId": 1,
    "orderId": 1,
    "userId": 1,
    "transactionId": "TXN-1702225320000-1234",
    "amount": 2200.0,
    "paymentStatus": "COMPLETED",
    "paymentMethod": "CREDIT_CARD",
    "paymentGateway": "STRIPE",
    "paymentReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentDate": "2025-12-10T14:42:00",
    "notes": null
  },
  "transactionId": "TXN-1702225320000-1234",
  "status": "201 CREATED"
}
```

### 2. Get User Payments
```bash
curl -X GET http://localhost:8080/api/payments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
  {
    "paymentId": 1,
    "orderId": 1,
    "userId": 1,
    "transactionId": "TXN-1702225320000-1234",
    "amount": 2200.0,
    "paymentStatus": "COMPLETED",
    "paymentMethod": "CREDIT_CARD",
    "paymentGateway": "STRIPE",
    "paymentReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentDate": "2025-12-10T14:42:00"
  }
]
```

### 3. Get Payment Details
```bash
curl -X GET http://localhost:8080/api/payments/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "paymentId": 1,
  "orderId": 1,
  "userId": 1,
  "transactionId": "TXN-1702225320000-1234",
  "amount": 2200.0,
  "paymentStatus": "COMPLETED",
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "STRIPE",
  "paymentReference": "550e8400-e29b-41d4-a716-446655440000",
  "paymentDate": "2025-12-10T14:42:00",
  "notes": null
}
```

### 4. Get Payment by Transaction ID
```bash
curl -X GET "http://localhost:8080/api/payments/transaction/TXN-1702225320000-1234" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### 5. Get Payment Status
```bash
curl -X GET http://localhost:8080/api/payments/1/status \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```
"Completed"
```

### 6. Get Payment Receipt
```bash
curl -X GET http://localhost:8080/api/payments/receipt/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "paymentId": 1,
  "orderId": 1,
  "userId": 1,
  "transactionId": "TXN-1702225320000-1234",
  "amount": 2200.0,
  "paymentStatus": "COMPLETED",
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "STRIPE",
  "paymentReference": "550e8400-e29b-41d4-a716-446655440000",
  "paymentDate": "2025-12-10T14:42:00"
}
```

### 7. Refund Payment
```bash
curl -X POST http://localhost:8080/api/payments/1/refund \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment refunded successfully",
  "timestamp": "2025-12-10T14:43:00",
  "payment": {
    "paymentId": 1,
    "orderId": 1,
    "userId": 1,
    "transactionId": "TXN-1702225320000-1234",
    "amount": 2200.0,
    "paymentStatus": "REFUNDED",
    "paymentMethod": "CREDIT_CARD",
    "paymentGateway": "STRIPE",
    "paymentReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentDate": "2025-12-10T14:42:00"
  },
  "transactionId": "TXN-1702225320000-1234",
  "status": "200 OK"
}
```

### 8. Get Payments by Status
```bash
curl -X GET "http://localhost:8080/api/payments/status/COMPLETED" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

---

## ERROR RESPONSE EXAMPLES

### 400 Bad Request - Invalid Input
```json
{
  "success": false,
  "message": "Quantity must be a positive number",
  "timestamp": "2025-12-10T14:44:00",
  "status": "400 BAD REQUEST"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Product not found with productId: 999",
  "timestamp": "2025-12-10T14:45:00",
  "status": "404 NOT FOUND"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Error processing payment: Database connection failed",
  "timestamp": "2025-12-10T14:46:00",
  "status": "500 INTERNAL SERVER ERROR"
}
```

---

## Test Workflow

### Complete E-Commerce Flow
1. **Add to Cart** - Add products to cart
2. **Manage Cart** - Update quantities, remove items
3. **Create Addresses** - Add shipping addresses
4. **Create Order** - Convert cart to order
5. **Process Payment** - Pay for order
6. **Track Order** - Monitor order status

### Example Sequence
```
1. POST /api/products/1/quantity/2      → Add to cart
2. GET /api/cart                         → View cart
3. POST /api/addresses                   → Create address
4. POST /api/orders                      → Create order
5. POST /api/payments                    → Process payment
6. GET /api/orders/1                     → Check order details
7. GET /api/payments/1                   → Check payment receipt
8. PUT /api/orders/1/status/SHIPPED      → Update order status
```

---

## Notes

- All timestamps are in ISO 8601 format
- Amounts are in USD and support 2 decimal places
- JWT tokens expire based on configuration
- All operations are user-scoped (users can only access their own data)
- Database automatically generates IDs
- Estimated delivery is calculated as order date + 7 days

