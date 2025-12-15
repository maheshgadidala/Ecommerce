# 🔐 Sign In & Authentication Guide

## 📋 Overview

Before you can use any of the E-Commerce APIs (Cart, Address, Order, Payment), you **MUST sign in** and obtain a **JWT token**.

This guide explains how to register and sign in to the application.

---

## 🔑 Authentication Flow

```
1. Register (Create Account)
   ↓
2. Sign In (Get JWT Token)
   ↓
3. Use JWT Token in All API Requests
   ↓
4. Access Protected Endpoints (Cart, Address, Orders, Payments)
```

---

## 📝 Step 1: Register/Sign Up

### Endpoint: `POST /api/auth/signup` or `POST /api/signup`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Required Fields:**
- `username` (String): Unique username (20 characters max)
- `email` (String): Valid email address (must be unique)
- `password` (String): At least 8 characters

**Example curl command:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Success Response (201 Created):**
```json
{
  "message": "User registered successfully!"
}
```

**Error Response - Username exists:**
```json
{
  "message": "Error: Username is already taken!"
}
```

**Error Response - Email exists:**
```json
{
  "message": "Error: Email is already in use!"
}
```

---

## 🔓 Step 2: Sign In / Login

### Endpoint: `POST /api/auth/signin` or `POST /api/signin`

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Required Fields:**
- `username` (String): Your username
- `password` (String): Your password

**Example curl command:**
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAyMjI1Mzc1LCJleHAiOjE3MDIzMTE3NzV9.abc123...",
  "username": "johndoe",
  "roles": ["ROLE_USER"]
}
```

**Error Response - Invalid credentials:**
```json
{
  "message": "Error: Invalid username or password"
}
```

---

## 🔑 Step 3: Use JWT Token in API Requests

Once you have the JWT token from the login response, include it in all subsequent API requests.

### Authorization Header Format:
```
Authorization: Bearer <your_jwt_token>
```

### Example:
```bash
curl -X GET http://localhost:8080/api/addresses \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAyMjI1Mz..." \
  -H "Content-Type: application/json"
```

---

## 📊 Authentication Workflow Example

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "testpass123"
  }'
```

### 2. Sign In
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }'
```

**Response:**
```json
{
  "id": 5,
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "testuser",
  "roles": ["ROLE_USER"]
}
```

### 3. Copy the Token
Save the `token` value from the response.

### 4. Use Token in Protected Endpoints

**Create an Address:**
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "zipCode": "10001",
    "phoneNumber": "555-1234",
    "recipientName": "John Doe",
    "addressType": "SHIPPING",
    "isDefault": true
  }'
```

**Add to Cart:**
```bash
curl -X POST http://localhost:8080/api/products/1/quantity/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json"
```

**Create Order:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "orderNotes": "Please deliver after 5 PM"
  }'
```

**Process Payment:**
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 1000.0,
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## ⏰ Token Expiration

- **Default expiration time**: 24 hours (86400000 milliseconds)
- **After expiration**: You must sign in again to get a new token
- **Check your `application.properties`**: `app.jwtExpirationMs=86400000`

If you get a **401 Unauthorized** error, your token has expired. Sign in again to get a new one.

---

## 🔐 Security Notes

### Keep Your Token Secret
- Never share your JWT token
- Don't expose it in public repositories
- Treat it like a password

### Token in Header
- Always include the token in the `Authorization` header
- Format must be: `Bearer <token>`
- Do NOT include the token in URL or request body

### HTTPS Recommended
- Use HTTPS in production (not HTTP)
- Tokens are sent in plain text and need encryption

---

## 🛠️ Using Postman

### 1. Login Request
```
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

### 2. Save Token to Variable
- In Postman, go to the login request response
- Click on the response body
- You'll see the token field
- Copy the token value

### 3. Create Authorization Header Variable
- Go to Postman Collections
- Create a new variable: `token`
- Set it to your JWT token
- In requests, use: `Authorization: Bearer {{token}}`

### 4. Use in Requests
- All subsequent requests will use the token automatically
- When token expires, run login again to update

---

## 🚫 Common Errors & Solutions

### Error 1: Invalid Username or Password
**Message:** `"Error: Invalid username or password"`
**Solution:** 
- Check spelling of username
- Verify password is correct
- Make sure you registered first

### Error 2: Username Already Taken
**Message:** `"Error: Username is already taken!"`
**Solution:**
- Use a different username
- Or try logging in if you already have an account

### Error 3: Email Already in Use
**Message:** `"Error: Email is already in use!"`
**Solution:**
- Use a different email
- Or try logging in with that email

### Error 4: 401 Unauthorized
**Message:** `401 Unauthorized`
**Solution:**
- Token may have expired - sign in again
- Verify token is in Authorization header
- Check token format: `Bearer <token>`

### Error 5: 400 Bad Request
**Message:** `400 Bad Request`
**Solution:**
- Check request body format (JSON)
- Verify all required fields are present
- Check field types (username is string, not number)

---

## 📋 Complete API Flow Checklist

- [ ] **1. Register Account**
  ```bash
  POST /api/auth/signup
  username, email, password
  ```

- [ ] **2. Sign In**
  ```bash
  POST /api/auth/signin
  username, password
  ```

- [ ] **3. Copy JWT Token**
  Save the token from response

- [ ] **4. Create Address**
  ```bash
  POST /api/addresses (with token)
  street, city, state, country, zipCode, phoneNumber, recipientName
  ```

- [ ] **5. Add Products to Cart**
  ```bash
  POST /api/products/{productId}/quantity/{quantity} (with token)
  ```

- [ ] **6. Create Order**
  ```bash
  POST /api/orders (with token)
  addressId, orderNotes, discountAmount
  ```

- [ ] **7. Process Payment**
  ```bash
  POST /api/payments (with token)
  orderId, amount, paymentMethod
  ```

---

## 🎯 Quick Start (Copy & Paste)

### Register:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user1@example.com","password":"password123"}'
```

### Sign In:
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Copy Token from Response, Then:

### Get Addresses:
```bash
curl -X GET http://localhost:8080/api/addresses \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

### Create Address:
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "street":"123 Main St",
    "city":"New York",
    "state":"NY",
    "country":"USA",
    "zipCode":"10001",
    "phoneNumber":"555-1234",
    "recipientName":"John Doe",
    "addressType":"SHIPPING",
    "isDefault":true
  }'
```

---

## 🔄 Token Refresh

If your token expires while using the API:
1. You'll get a **401 Unauthorized** response
2. Sign in again using the same credentials
3. Copy the new token
4. Continue with the new token

There is currently no refresh token endpoint, so you must sign in again.

---

## 📞 Support

If you have issues with authentication:
1. Check the error message carefully
2. Verify your username and password
3. Make sure you registered before signing in
4. Check the token format in the Authorization header
5. Verify the token hasn't expired (24 hours)

---

## 🎉 You're Ready!

Once you have your JWT token, you can:
- ✅ Manage addresses
- ✅ Add products to cart
- ✅ Create and track orders
- ✅ Process payments
- ✅ View history and details

**Now go ahead and use the E-Commerce API!** 🚀

