# 🚀 QUICK START - SIGN IN & USE API IN 5 MINUTES

## ⚡ Fastest Way to Get Started

### Prerequisites
- Application running: `mvn spring-boot:run`
- curl installed (or use Postman)
- Terminal/Command Prompt open

---

## 🎯 5-Minute Quick Start

### **Step 1** (30 seconds): Register
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "email": "myuser@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully!"
}
```

✅ **Done!** You have an account.

---

### **Step 2** (30 seconds): Sign In
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJteXVzZXIiLCJpYXQiOjE3MDIyMjU0NzEsImV4cCI6MTcwMjMxMTg3MX0.HiJxyZ...",
  "username": "myuser",
  "roles": ["ROLE_USER"]
}
```

✅ **Copy the `token` value!** You'll need it.

---

### **Step 3** (1 minute): Create Address
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
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

**Replace `YOUR_TOKEN_HERE` with the token from Step 2!**

**Expected Response:**
```json
{
  "success": true,
  "message": "Address created successfully",
  "address": {
    "addressId": 1,
    "street": "123 Main St",
    "city": "New York",
    ...
  }
}
```

✅ **You have an address!**

---

### **Step 4** (1 minute): Add to Cart
```bash
curl -X POST http://localhost:8080/api/products/1/quantity/2 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Product added to cart successfully",
  "cart": {
    "cartId": 1,
    "totalPrice": 900.0,
    "products": [...]
  }
}
```

✅ **Product in cart!**

---

### **Step 5** (1 minute): Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "orderNotes": "Please deliver in the morning"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "order": {
    "orderId": 1,
    "orderStatus": "PENDING",
    "finalAmount": 2250.0,
    ...
  }
}
```

✅ **Order created!**

---

### **Bonus - Process Payment** (1 minute)
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 2250.0,
    "paymentMethod": "CREDIT_CARD"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "payment": {
    "paymentId": 1,
    "paymentStatus": "COMPLETED",
    "transactionId": "TXN-..."
  }
}
```

✅ **Payment done!**

---

## 🔑 Remember: Always Include Token

**Every protected request needs this header:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

Replace `YOUR_TOKEN_HERE` with the token from Sign In response!

---

## 📝 Simple Checklist

- [ ] Register (Step 1)
- [ ] Sign In and copy token (Step 2)
- [ ] Create address (Step 3)
- [ ] Add to cart (Step 4)
- [ ] Create order (Step 5)
- [ ] Process payment (Bonus)

---

## 🔄 Use Same Token for All Requests

Once you have the token from sign in:
- ✅ Use for addresses
- ✅ Use for cart
- ✅ Use for orders
- ✅ Use for payments
- ✅ Use for everything!

**One token = All APIs!**

---

## 💡 Token Tips

### Token Expires After 24 Hours
- If you get `401 Unauthorized`, sign in again
- Get a new token
- Continue using APIs

### Where to Paste Token
```
-H "Authorization: Bearer <PASTE_HERE>"
```

### Example with Real Token
```bash
curl -X GET http://localhost:8080/api/addresses \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJteXVzZXIi..." \
  -H "Content-Type: application/json"
```

---

## 🆘 Troubleshooting

### "Invalid username or password"
→ Check spelling, make sure you registered in Step 1

### "400 Bad Request"
→ Check JSON format, all quotes, commas correct

### "401 Unauthorized"
→ Token missing or wrong, copy token again from Step 2

### "Address not found"
→ Use correct addressId from Step 3 response

---

## 📚 Full Documentation

For detailed guides, read:
- **SIGNIN_GUIDE.md** - Complete authentication
- **COMPLETE_WORKFLOW.md** - Full 10-step example
- **POSTMAN_GUIDE.md** - Using Postman (easier!)
- **API_DOCUMENTATION.md** - All endpoints

---

## 🎉 Congratulations!

You've:
✅ Registered user
✅ Signed in
✅ Created address
✅ Added to cart
✅ Created order
✅ Processed payment

**You can now use all E-Commerce APIs!** 🚀

---

## 📌 Keep This Handy

Save these endpoints:

```
Register:     POST /api/auth/signup
Sign In:      POST /api/auth/signin
Address:      POST /api/addresses
Cart:         POST /api/products/{id}/quantity/{qty}
Order:        POST /api/orders
Payment:      POST /api/payments
```

**All protected endpoints need:** `Authorization: Bearer TOKEN`

---

**Ready? Let's go!** 🚀

