Postman collection and quick usage

Files added:
- postman_collection.json  (Postman collection you can import)

How to use
1) Open Postman -> Import -> Choose File -> select postman_collection.json
2) Optionally create an environment and set variable `baseUrl` (default: http://localhost:8080)
3) Run requests in order:
   - "Signup - Create Admin" to create an admin user
   - "Signup - Create Seller" to create a seller user
   - "Signin - Get JWT" to sign in. The collection contains a test script that tries to extract a token from the response and save it into the environment variable `token`.
   - "Protected - Example (uses token)" demonstrates using the `Authorization: Bearer {{token}}` header.

Curl examples (Windows cmd) - replace host/port if needed
- Create admin:
  curl -X POST "http://localhost:8080/api/auth/signup" -H "Content-Type: application/json" -d "{\"username\":\"adminuser\",\"password\":\"AdminPass123\",\"email\":\"admin@example.com\",\"role\":[\"admin\"]}"

- Sign in:
  curl -X POST "http://localhost:8080/api/auth/signin" -H "Content-Type: application/json" -d "{\"username\":\"adminuser\",\"password\":\"AdminPass123\"}"

- Call protected endpoint with token (replace <TOKEN>):
  curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/protected/example

Troubleshooting
- If you receive 401 on signup: ensure the app's security config permits `/api/auth/**` (the repo was updated to allow this).
- If signup fails with "Role is not found": ensure the database has roles (ROLE_USER, ROLE_SELLER, ROLE_ADMIN). The app includes a startup seeder that creates them automatically.
- If the app won't start because port 8080 is used:
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F

If you want, I can also:
- Export a Postman environment JSON that pre-creates `baseUrl` and `token` variables.
- Run a quick live test against your running server and paste the responses here.

{
  "info": {
    "_postman_id": "ecommerce-auth-collection",
    "name": "Ecommerce Auth (signup/signin)",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
    "description": "Collection with requests to create admin/seller users and sign in to get a JWT. Update {{baseUrl}} if your server runs on a different host/port."
  },
  "item": [
    {
      "name": "Signup - Create Admin",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"adminuser\",\n  \"password\": \"AdminPass123\",\n  \"email\": \"admin@example.com\",\n  \"role\": [\"admin\"]\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/auth/signup",
          "host": ["{{baseUrl}}"],
          "path": ["api","auth","signup"]
        }
      }
    },
    {
      "name": "Signup - Create Seller",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"seller1\",\n  \"password\": \"SellerPass1\",\n  \"email\": \"seller1@example.com\",\n  \"role\": [\"seller\"]\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/auth/signup",
          "host": ["{{baseUrl}}"],
          "path": ["api","auth","signup"]
        }
      }
    },
    {
      "name": "Signin - Get JWT",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"adminuser\",\n  \"password\": \"AdminPass123\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/auth/signin",
          "host": ["{{baseUrl}}"],
          "path": ["api","auth","signin"]
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "// Save token to environment variable 'token' if signin returns a JSON with a 'token' or 'jwt' field.",
              "var json = pm.response.json();",
              "var token = json.token || json.jwt || json.accessToken || json.tokenValue || json[Object.keys(json).find(k => k.toLowerCase().includes('token'))];",
              "if (token) { pm.environment.set('token', token); pm.test('Saved token', function(){pm.expect(token).to.be.a('string')}); } else { pm.test('No token found in response', function(){pm.expect(token).to.not.be.undefined}); }"
            ],
            "type": "text/javascript"
          }
        }
      ]
    },
    {
      "name": "Protected - Example (uses token)",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/protected/example",
          "host": ["{{baseUrl}}"],
          "path": ["api","protected","example"]
        }
      },
      "response": []
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080",
      "type": "string"
    },
    {
      "key": "token",
      "value": "",
      "type": "string"
    }
  ]
}

