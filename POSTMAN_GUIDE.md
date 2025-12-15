# 📮 Postman Collection Guide

## Using Postman to Test E-Commerce APIs

### Prerequisites
- Download Postman: https://www.postman.com/downloads/
- Application running on `http://localhost:8080`

---

## 🔧 Setup

### 1. Create a New Collection
- Open Postman
- Click **Collections** → **Create New Collection**
- Name it: "E-Commerce API"

### 2. Create Environment Variables
- Click **Environments** → **Create New Environment**
- Name it: "Local Development"
- Add variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `token` | (empty) | (will be filled after login) |
| `username` | (empty) | (your username) |
| `user_id` | (empty) | (will be filled after login) |

---

## 📝 Requests Setup

### 1. Register Request

**Name:** Register User
**Method:** POST
**URL:** `{{base_url}}/api/auth/signup`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

---

### 2. Sign In Request

**Name:** Sign In
**Method:** POST
**URL:** `{{base_url}}/api/auth/signin`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Tests Tab (Auto-save token):**
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
    pm.environment.set("user_id", jsonData.id);
}
```

---

### 3. Create Address Request

**Name:** Create Address
**Method:** POST
**URL:** `{{base_url}}/api/addresses`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
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

---

### 4. Get All Addresses Request

**Name:** Get Addresses
**Method:** GET
**URL:** `{{base_url}}/api/addresses`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 5. Add Product to Cart Request

**Name:** Add to Cart
**Method:** POST
**URL:** `{{base_url}}/api/products/1/quantity/2`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 6. Get Cart Request

**Name:** Get Cart
**Method:** GET
**URL:** `{{base_url}}/api/cart`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 7. Update Cart Quantity Request

**Name:** Update Cart Quantity
**Method:** PUT
**URL:** `{{base_url}}/api/cart/products/1/quantity/5`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 8. Create Order Request

**Name:** Create Order
**Method:** POST
**URL:** `{{base_url}}/api/orders`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "addressId": 1,
  "orderNotes": "Please deliver after 5 PM",
  "discountAmount": 0.0
}
```

---

### 9. Get Orders Request

**Name:** Get Orders
**Method:** GET
**URL:** `{{base_url}}/api/orders`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 10. Get Order Details Request

**Name:** Get Order by ID
**Method:** GET
**URL:** `{{base_url}}/api/orders/1`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 11. Process Payment Request

**Name:** Process Payment
**Method:** POST
**URL:** `{{base_url}}/api/payments`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "orderId": 1,
  "amount": 2200.0,
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "STRIPE"
}
```

---

### 12. Get Payment Details Request

**Name:** Get Payment
**Method:** GET
**URL:** `{{base_url}}/api/payments/1`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

### 13. Get Payment Receipt Request

**Name:** Get Payment Receipt
**Method:** GET
**URL:** `{{base_url}}/api/payments/receipt/1`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 🚀 Quick Test Sequence

### Step 1: Register
1. Open "Register User" request
2. Change username, email (make unique)
3. Click **Send**
4. Verify response: `"User registered successfully!"`

### Step 2: Sign In
1. Open "Sign In" request
2. Use same username and password
3. Click **Send**
4. Verify response has `token` field
5. Token automatically saved to environment

### Step 3: Create Address
1. Open "Create Address" request
2. Verify token is in header: `Authorization: Bearer {{token}}`
3. Click **Send**
4. Note the `addressId` from response

### Step 4: Add to Cart
1. Open "Add to Cart" request
2. Click **Send**
3. Verify product added successfully

### Step 5: Create Order
1. Open "Create Order" request
2. Update `addressId` to match created address
3. Click **Send**
4. Note the `orderId` from response

### Step 6: Process Payment
1. Open "Process Payment" request
2. Update `orderId` to match created order
3. Update `amount` to match order `finalAmount`
4. Click **Send**
5. Verify payment status: `COMPLETED`

### Step 7: Check Receipt
1. Open "Get Payment Receipt" request
2. Update `orderId` to match order
3. Click **Send**
4. View payment details and receipt

---

## 📊 Complete Request List

Create folders in Postman:

### Folder: Authentication
- Register User
- Sign In

### Folder: Addresses
- Create Address
- Get Addresses
- Get Default Address
- Get Address by ID
- Update Address
- Delete Address
- Set Default Address
- Get Addresses by Type

### Folder: Cart
- Add to Cart
- Get Cart
- Update Cart Quantity
- Delete from Cart
- Clear Cart

### Folder: Orders
- Create Order
- Get Orders
- Get Order by ID
- Get Orders by Status
- Update Order Status
- Cancel Order
- Get Order History
- Get Recent Orders

### Folder: Payments
- Process Payment
- Get Payment
- Get Payments
- Get Payment by Status
- Get Payment by Transaction ID
- Get Payment History
- Refund Payment
- Get Payment Receipt
- Check Payment Status

---

## 💡 Postman Tips

### Auto-Generate Token
Add this in **Tests** tab of Sign In request:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
}
```

### Auto-Save IDs
Add this in **Tests** tab of Create Address request:
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("address_id", jsonData.address.addressId);
}
```

### Use Variables Everywhere
- Instead of hardcoding URLs, use `{{base_url}}`
- Instead of hardcoding tokens, use `{{token}}`
- Update variables in **Environment** → **Local Development**

### Pre-request Scripts
Set up pre-request script to validate token exists:
```javascript
var token = pm.environment.get("token");
if (!token) {
    console.warn("No token found. Please sign in first!");
}
```

---

## 🔐 Security in Postman

### ✅ DO
- Use environment variables for sensitive data
- Save token in environment, not in request
- Use {{token}} variable in headers
- Don't commit .env files to git

### ❌ DON'T
- Hardcode tokens in requests
- Share Postman collections with tokens
- Commit workspace files with tokens
- Use production credentials in local Postman

---

## 📤 Export & Share

### Export Collection (without sensitive data)
1. Click Collection → **More actions** (•••) → **Export**
2. Choose **Collection v2.1**
3. Save as `ecommerce-api.postman_collection.json`

### Export Environment (without sensitive data)
1. Click Environment → **More actions** (•••) → **Export**
2. Remove token values before exporting
3. Save as `ecommerce-api.postman_environment.json`

### Share with Team
- Share JSON files only
- Team members import and add their own tokens
- Never commit tokens to git

---

## 🧪 Test Cases

### Test Case 1: Complete Purchase Flow
```
Register → Sign In → Create Address → Add to Cart → Create Order → Process Payment
```

### Test Case 2: Multiple Products
```
Sign In → Add Product 1 → Add Product 2 → Update Quantity → Create Order → Payment
```

### Test Case 3: Multiple Addresses
```
Sign In → Create Address 1 → Create Address 2 → Set Address 1 as Default → Create Order with Address 2
```

### Test Case 4: Order Management
```
Create Order → Check Status → Get Order History → Cancel Order → Verify Status
```

### Test Case 5: Payment Management
```
Create Payment → Get Payment Details → Get Receipt → Refund Payment → Verify Status
```

---

## 🐛 Debugging

### Check Response Status
- 200 OK: Success
- 201 Created: Resource created
- 400 Bad Request: Check JSON format
- 401 Unauthorized: Token missing or expired
- 404 Not Found: Resource doesn't exist
- 500 Internal Error: Server issue

### View Response Headers
- Click **Headers** tab in response
- Check `Content-Type: application/json`
- Look for any error headers

### Use Console
- Click **View** → **Show Postman Console**
- See all requests and responses
- Helpful for debugging

---

## 📌 Checklist for Complete Testing

- [ ] Register new user
- [ ] Sign in and get token
- [ ] Create shipping address
- [ ] Get all addresses
- [ ] Set default address
- [ ] Add product to cart
- [ ] View cart details
- [ ] Update product quantity
- [ ] Remove product from cart
- [ ] Create order
- [ ] Get order details
- [ ] Get all orders
- [ ] Update order status
- [ ] Process payment
- [ ] Get payment details
- [ ] Get payment receipt
- [ ] Refund payment
- [ ] Get payment history
- [ ] Cancel order
- [ ] Check all error cases

---

## 🎉 You're Ready to Test!

Follow these steps and you can thoroughly test all E-Commerce APIs in Postman.

**Key Points:**
1. Always sign in first to get token
2. Use {{token}} variable in Authorization header
3. Use {{base_url}} variable for endpoints
4. Save important IDs for reuse
5. Check response status codes

Happy Testing! 🚀

