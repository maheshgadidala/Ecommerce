# 🚀 Complete E-Commerce API Workflow

## Authentication & API Usage Flow

### Step-by-Step Guide with Real Examples

---

## 📍 STEP 1: REGISTER A NEW ACCOUNT

**Purpose:** Create a user account to access the system

**Endpoint:** `POST /api/auth/signup`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "SecurePassword123"
  }'
```

**Success Response (201 Created):**
```json
{
  "message": "User registered successfully!"
}
```

**Error Responses:**
```json
{
  "message": "Error: Username is already taken!"
}
```
or
```json
{
  "message": "Error: Email is already in use!"
}
```

---

## 🔓 STEP 2: SIGN IN (LOGIN)

**Purpose:** Get JWT token to authenticate API requests

**Endpoint:** `POST /api/auth/signin`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePassword123"
  }'
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAyMjI1Mzc1LCJleHAiOjE3MDIzMTE3NzV9.AbCdEfGhIjKlMnOpQrStUvWxYz...",
  "username": "johndoe",
  "roles": ["ROLE_USER"]
}
```

**⚠️ IMPORTANT:** Save the `token` value! You'll need it for all subsequent requests.

**Error Response:**
```json
{
  "message": "Error: Invalid username or password"
}
```

---

## 🔑 TOKEN USAGE

All protected endpoints require the JWT token in the Authorization header:

**Format:**
```
Authorization: Bearer <your_jwt_token>
```

**Example with token:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAyMjI1Mzc1LCJleHAiOjE3MDIzMTE3NzV9.AbCdEfGhIjKlMnOpQrStUvWxYz...
```

---

## 📍 STEP 3: CREATE A SHIPPING ADDRESS

**Purpose:** Add a shipping address for orders

**Endpoint:** `POST /api/addresses`

**Request:**
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
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

**Success Response (201 Created):**
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

**Address Fields:**
- `street` (Required): Street address
- `city` (Required): City name
- `state` (Required): State/Province
- `country` (Required): Country name
- `zipCode` (Required): Postal code
- `phoneNumber` (Required): Contact phone
- `recipientName` (Optional): Name for delivery
- `addressType` (Required): SHIPPING, BILLING, or BOTH
- `isDefault` (Optional): Set as default address

---

## 📍 STEP 4: ADD PRODUCTS TO CART

**Purpose:** Add items to your shopping cart

**Endpoint:** `POST /api/products/{productId}/quantity/{quantity}`

**Request:**
```bash
# Add 2 units of product ID 1 to cart
curl -X POST http://localhost:8080/api/products/1/quantity/2 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Product added to cart successfully",
  "timestamp": "2025-12-10T14:30:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 900.0,
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
  "quantityAdded": 2,
  "priceAdded": 900.0,
  "status": "201 CREATED"
}
```

---

## 📍 STEP 5: VIEW YOUR CART

**Purpose:** See all items in your cart

**Endpoint:** `GET /api/cart`

**Request:**
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart details retrieved successfully",
  "timestamp": "2025-12-10T14:31:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 1350.0,
    "products": [
      {
        "productId": 1,
        "productName": "Laptop",
        "quantity": 2,
        "price": 450.0
      },
      {
        "productId": 2,
        "productName": "Mouse",
        "quantity": 3,
        "price": 50.0
      }
    ]
  },
  "status": "200 OK"
}
```

---

## 📍 STEP 6: UPDATE CART QUANTITY (Optional)

**Purpose:** Change quantity of a product in cart

**Endpoint:** `PUT /api/cart/products/{productId}/quantity/{quantity}`

**Request:**
```bash
# Update product 1 to 5 units
curl -X PUT http://localhost:8080/api/cart/products/1/quantity/5 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Product quantity updated successfully",
  "timestamp": "2025-12-10T14:32:00",
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
  "quantityAdded": 5,
  "priceAdded": 2250.0,
  "status": "200 OK"
}
```

---

## 📍 STEP 7: CREATE AN ORDER

**Purpose:** Convert cart items to an order

**Endpoint:** `POST /api/orders`

**Request:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "orderNotes": "Please deliver between 10 AM and 6 PM",
    "discountAmount": 50.0
  }'
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "timestamp": "2025-12-10T14:39:00",
  "order": {
    "orderId": 1,
    "userId": 1,
    "userName": "johndoe",
    "userEmail": "john.doe@example.com",
    "addressId": 1,
    "shippingAddressDetails": "123 Main Street, New York, NY, United States",
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
    "orderNotes": "Please deliver between 10 AM and 6 PM"
  },
  "status": "201 CREATED"
}
```

**Order Parameters:**
- `addressId` (Required): ID of the shipping address
- `orderNotes` (Optional): Special instructions
- `discountAmount` (Optional): Discount to apply

---

## 📍 STEP 8: PROCESS PAYMENT

**Purpose:** Pay for the order

**Endpoint:** `POST /api/payments`

**Request:**
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 2200.0,
    "paymentMethod": "CREDIT_CARD",
    "paymentGateway": "STRIPE"
  }'
```

**Success Response (201 Created):**
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

**Payment Methods Available:**
- CREDIT_CARD
- DEBIT_CARD
- NET_BANKING
- UPI
- WALLET
- CASH_ON_DELIVERY

---

## 📍 STEP 9: CHECK ORDER STATUS

**Purpose:** Track your order

**Endpoint:** `GET /api/orders/{orderId}`

**Request:**
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200 OK):**
```json
{
  "orderId": 1,
  "userId": 1,
  "userName": "johndoe",
  "userEmail": "john.doe@example.com",
  "addressId": 1,
  "shippingAddressDetails": "123 Main Street, New York, NY, United States",
  "orderItems": [...],
  "orderStatus": "PENDING",
  "totalAmount": 2250.0,
  "discountAmount": 50.0,
  "finalAmount": 2200.0,
  "orderDate": "2025-12-10T14:39:00",
  "estimatedDeliveryDate": "2025-12-17T14:39:00"
}
```

---

## 📍 STEP 10: GET PAYMENT RECEIPT

**Purpose:** Retrieve payment receipt for order

**Endpoint:** `GET /api/payments/receipt/{orderId}`

**Request:**
```bash
curl -X GET http://localhost:8080/api/payments/receipt/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Success Response (200 OK):**
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

---

## 📊 Complete Workflow Summary

| Step | Endpoint | Method | Purpose |
|------|----------|--------|---------|
| 1 | `/api/auth/signup` | POST | Register account |
| 2 | `/api/auth/signin` | POST | Get JWT token |
| 3 | `/api/addresses` | POST | Create address |
| 4 | `/api/products/{id}/quantity/{qty}` | POST | Add to cart |
| 5 | `/api/cart` | GET | View cart |
| 6 | `/api/cart/products/{id}/quantity/{qty}` | PUT | Update quantity |
| 7 | `/api/orders` | POST | Create order |
| 8 | `/api/payments` | POST | Process payment |
| 9 | `/api/orders/{id}` | GET | Check status |
| 10 | `/api/payments/receipt/{orderId}` | GET | Get receipt |

---

## 💡 Tips & Tricks

### Keep Token Safe
- Copy token from login response
- Store it securely
- Include in every protected request

### Update Cart Before Order
- Always review cart before creating order
- Update quantities if needed
- Delete items you don't want

### Multiple Addresses
- Create multiple addresses
- Set one as default
- Choose different address for each order

### Track Payment
- Save transaction ID from payment response
- Use it to track payment status
- Helpful for customer support

---

## 🔄 Common Workflows

### Workflow 1: Quick Purchase
1. Register → Sign in
2. Create address
3. Add product to cart
4. Create order
5. Process payment

### Workflow 2: Browse & Order
1. Register → Sign in
2. Create multiple addresses
3. Add multiple products to cart
4. Update cart as needed
5. Create order with specific address
6. Process payment

### Workflow 3: Gift Purchase
1. Register → Sign in
2. Create recipient's address
3. Add gift products to cart
4. Create order with recipient's address
5. Process payment
6. Add special order notes

---

## ❌ Error Handling

### 401 Unauthorized
**Cause:** Missing or expired token
**Solution:** Sign in again to get new token

### 404 Not Found
**Cause:** Resource doesn't exist
**Solution:** Check the ID (addressId, orderId, etc.)

### 400 Bad Request
**Cause:** Invalid request format
**Solution:** Check JSON format and required fields

### 500 Server Error
**Cause:** Server issue
**Solution:** Check server logs, retry request

---

## ✅ Verification Checklist

Before moving to next step, verify:

- [ ] User registered successfully
- [ ] Sign in returned a token
- [ ] Token included in Authorization header
- [ ] Address created with correct details
- [ ] Product added to cart
- [ ] Cart total is correct
- [ ] Order created successfully
- [ ] Payment processed (status COMPLETED)
- [ ] Order status is PENDING or CONFIRMED
- [ ] Can retrieve payment receipt

---

## 🚀 You're All Set!

Follow these 10 steps and you've completed a full e-commerce transaction!

**Key Takeaway:** Always remember to include the JWT token in the Authorization header for all protected endpoints.

Happy Shopping! 🛍️

