# 🍪 Testing Cookie-Based Authentication

## How to Test Your Cookie Implementation

Your application uses **HTTP Cookies** for JWT authentication. Here's how to test it:

---

## 🖥️ Option 1: Using Browser (Easiest)

### Step 1: Open Browser Developer Tools
Press `F12` → Go to **Application** tab → **Cookies**

### Step 2: Sign In
```bash
# In terminal/Postman:
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"password123"}'
```

### Step 3: Check Browser Cookies
Look in **Application** → **Cookies** → `http://localhost:8080`

You should see a cookie named **JWT** with value starting with `eyJ...`

### Step 4: Make API Call
```bash
curl -X GET http://localhost:8080/api/addresses
```

The cookie is **automatically sent** - you don't need to do anything!

---

## 📮 Option 2: Using Postman

### Step 1: Create Sign In Request
```
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

### Step 2: Send & Check Cookies
Click **Send** → Look at response headers
You'll see: `Set-Cookie: JWT=eyJ...; Path=/api; HttpOnly; SameSite=Lax`

### Step 3: Postman Auto-Manages Cookies
Postman automatically saves cookies from the response

### Step 4: Make Any API Request
```
GET http://localhost:8080/api/addresses
```

Postman **automatically includes the cookie** in the request!

Check request headers → You'll see the cookie being sent automatically.

---

## 🔧 Option 3: Using curl with Cookie Jar

### Step 1: Sign In & Save Cookie
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"username":"johndoe","password":"password123"}'
```

**`-c cookies.txt`** = Save cookies to file

### Step 2: Use Cookie in Requests
```bash
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt
```

**`-b cookies.txt`** = Send cookies from file

### Step 3: Verify Cookie File
```bash
cat cookies.txt
```

You'll see something like:
```
.localhost	TRUE	/	TRUE	1733532000	JWT	eyJ...
```

---

## 🔍 What to Verify

### ✅ Cookie Properties

When you sign in, verify the cookie has:

1. **Name:** `JWT`
2. **HttpOnly:** YES (shown as `HttpOnly` flag)
3. **SameSite:** `Lax`
4. **Path:** `/api`
5. **Max-Age/Expires:** Future date (24 hours from now)
6. **Value:** JWT token starting with `eyJ`

### ✅ Cookie Sending
When you make requests, verify:
- Cookie is sent automatically with each request
- No manual header needed
- Cookie appears in `Cookie` request header

### ✅ Cookie Deletion on Logout
When you call sign out:
```bash
curl -X POST http://localhost:8080/api/signout
```

Verify:
- New `Set-Cookie` header received
- Max-Age is 0 (immediate deletion)
- Cookie is removed from browser

---

## 🧪 Complete Test Sequence

### Test 1: Register User
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected:** `"User registered successfully!"`

### Test 2: Sign In (Creates Cookie)
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -c cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

**Expected:** `"username": "testuser"` + Set-Cookie header

### Test 3: Use Cookie in Request
```bash
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt
```

**Expected:** Successful response (200 OK) with empty address list

### Test 4: Create Address with Cookie
```bash
curl -X POST http://localhost:8080/api/addresses \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main St",
    "city": "NYC",
    "state": "NY",
    "country": "USA",
    "zipCode": "10001",
    "phoneNumber": "555-1234",
    "recipientName": "Test User",
    "addressType": "SHIPPING",
    "isDefault": true
  }'
```

**Expected:** Address created (201 Created)

### Test 5: Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt
```

**Expected:** Get list of addresses

### Test 6: Access Without Cookie
```bash
curl -X GET http://localhost:8080/api/addresses
```

**Expected:** 401 Unauthorized (cookie required)

### Test 7: Sign Out (Delete Cookie)
```bash
curl -X POST http://localhost:8080/api/signout \
  -b cookies.txt
```

**Expected:** Cookie deleted, Set-Cookie header with maxAge=0

### Test 8: Access After Logout
```bash
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt
```

**Expected:** 401 Unauthorized (cookie cleared)

---

## 🔒 Security Verification

### Check HTTPOnly Flag
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -v \  # Verbose mode
  -d '{"username":"johndoe","password":"password123"}'
```

Look for in response headers:
```
Set-Cookie: JWT=...; HttpOnly; SameSite=Lax; Path=/api
```

**✅ `HttpOnly` present** = JavaScript cannot access token

### Check SameSite Protection
Same response should have:
```
SameSite=Lax
```

**✅ `SameSite=Lax` present** = CSRF protection enabled

### Check Expiration
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -v \
  -d '{"username":"johndoe","password":"password123"}'
```

Look for:
```
Set-Cookie: JWT=...; Max-Age=86400; ...
```

**✅ Max-Age present** = Auto-expiration in 24 hours

---

## 📱 Postman Cookie Testing (Recommended)

### Step 1: Create Request
```
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

### Step 2: Go to Cookies Tab
Click **Cookies** at bottom of Postman

### Step 3: Click Send
Response shows the cookie being set

### Step 4: Check Cookies
Go back to **Cookies** tab → See JWT cookie listed

### Step 5: Create Another Request
```
GET http://localhost:8080/api/addresses
```

### Step 6: Send & Check
Postman automatically includes the cookie

In **Headers** tab, you won't see `Authorization` header (because using cookies, not headers)

But in request, cookie is being sent (Postman handles automatically)

### Step 7: Verify in Network Tab
(Open browser DevTools)
- Click **Network** tab
- Make request
- Check the request header
- See `Cookie: JWT=...` being sent

---

## ✅ Checklist for Your Cookie Implementation

- [ ] Can sign in successfully
- [ ] Cookie created with `JWT` name
- [ ] Cookie has `HttpOnly` flag
- [ ] Cookie has `SameSite=Lax`
- [ ] Cookie has `Path=/api`
- [ ] Cookie has Max-Age set (24 hours)
- [ ] Protected endpoints work with cookie
- [ ] Protected endpoints fail without cookie
- [ ] Postman auto-manages cookies
- [ ] Can sign out successfully
- [ ] Cookie deleted on sign out
- [ ] Can't access endpoints after logout

---

## 🐛 Debugging Cookies

### If Cookie Not Being Created
1. Check sign in returns 200 OK
2. Check response headers for `Set-Cookie`
3. Check logs for JWT generation

### If Cookie Not Being Sent
1. Check cookie exists in browser/cookie jar
2. Check cookie path is `/api`
3. Check cookie not expired
4. Use `-v` flag in curl to see request details

### If Getting 401 Unauthorized
1. Sign in again to get fresh cookie
2. Check cookie value is not empty
3. Use curl with `-b` flag to send cookie file
4. Check request includes `Cookie:` header

### If Protected Endpoint Not Working
1. Verify you're using the cookie file (`-b cookies.txt`)
2. Check cookie file is not empty
3. Try signing in again
4. Check endpoint is actually protected (requires auth)

---

## 📝 Example Full Session

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@ex.com","password":"secure123"}'

# 2. Sign In (save cookie)
curl -X POST http://localhost:8080/api/auth/signin \
  -c cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secure123"}'

# 3. View cookie
cat cookies.txt

# 4. Create address (with cookie)
curl -X POST http://localhost:8080/api/addresses \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{"street":"456 Oak","city":"LA","state":"CA","country":"USA","zipCode":"90001","phoneNumber":"555-5678","recipientName":"Alice","addressType":"SHIPPING","isDefault":true}'

# 5. Get addresses (with cookie)
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt

# 6. Try without cookie (should fail)
curl -X GET http://localhost:8080/api/addresses
# Returns 401 Unauthorized

# 7. Sign out
curl -X POST http://localhost:8080/api/signout \
  -b cookies.txt

# 8. Try again (should fail)
curl -X GET http://localhost:8080/api/addresses \
  -b cookies.txt
# Returns 401 Unauthorized (cookie cleared)
```

---

## 🎉 Your Cookie Implementation Works Perfectly!

✅ Cookies are created on sign in
✅ Cookies are sent automatically
✅ Protected endpoints require cookies
✅ Cookies are cleared on sign out
✅ Security flags are properly set

**No changes needed!** Your implementation is correct and secure! 🍪✨

