# Quick Setup & Usage Guide

## Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+

## Database Setup

### 1. Create Database
```bash
mysql -u root -p
```

```sql
CREATE DATABASE ecommerce;
USE ecommerce;
```

### 2. Apply Schema
The schema.sql file will be automatically executed on first run, or you can manually run it:
```sql
SOURCE /path/to/schema.sql;
```

## Configuration

### Update application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
app.jwtSecret=your_secret_key_here_minimum_32_characters_long
app.jwtExpirationMs=86400000

# Server Configuration
server.port=8080
```

## Build & Run

### Build
```bash
mvn clean compile
mvn package
```

### Run
```bash
# Using Maven
mvn spring-boot:run

# Using JAR
java -jar target/Ecommerce-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Testing the APIs

### 1. Register/Login
First, register a user or login to get JWT token:

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

### 2. Use JWT Token
Include the token in all requests:
```
Authorization: Bearer <your_jwt_token>
```

### 3. Test Cart APIs

**Add Product to Cart:**
```bash
POST /api/products/1/quantity/2
Authorization: Bearer <token>
```

**Get Cart Details:**
```bash
GET /api/cart
Authorization: Bearer <token>
```

**Update Quantity:**
```bash
PUT /api/cart/products/1/quantity/5
Authorization: Bearer <token>
```

**Delete from Cart:**
```bash
DELETE /api/cart/products/1
Authorization: Bearer <token>
```

**Clear Cart:**
```bash
DELETE /api/cart
Authorization: Bearer <token>
```

### 4. Test Address APIs

**Create Address:**
```bash
POST /api/addresses
Authorization: Bearer <token>
Content-Type: application/json

{
  "street": "123 Main St",
  "city": "New York",
  "state": "NY",
  "country": "USA",
  "zipCode": "10001",
  "phoneNumber": "555-1234",
  "recipientName": "John Doe",
  "addressType": "SHIPPING",
  "isDefault": true
}
```

**Get User Addresses:**
```bash
GET /api/addresses
Authorization: Bearer <token>
```

**Get Default Address:**
```bash
GET /api/addresses/default
Authorization: Bearer <token>
```

**Update Address:**
```bash
PUT /api/addresses/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "street": "456 Oak Ave",
  "city": "Los Angeles",
  ...
}
```

**Delete Address:**
```bash
DELETE /api/addresses/1
Authorization: Bearer <token>
```

### 5. Test Order APIs

**Create Order:**
```bash
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "addressId": 1,
  "orderNotes": "Please deliver after 5 PM",
  "discountAmount": 50.0
}
```

**Get User Orders:**
```bash
GET /api/orders
Authorization: Bearer <token>
```

**Get Order Details:**
```bash
GET /api/orders/1
Authorization: Bearer <token>
```

**Update Order Status:**
```bash
PUT /api/orders/1/status/CONFIRMED
Authorization: Bearer <token>
```

**Cancel Order:**
```bash
PUT /api/orders/1/cancel
Authorization: Bearer <token>
```

**Get Recent Orders:**
```bash
GET /api/orders/recent?limit=5
Authorization: Bearer <token>
```

### 6. Test Payment APIs

**Process Payment:**
```bash
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": 1,
  "amount": 1000.0,
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "MOCK"
}
```

**Get User Payments:**
```bash
GET /api/payments
Authorization: Bearer <token>
```

**Get Payment Details:**
```bash
GET /api/payments/1
Authorization: Bearer <token>
```

**Get Payment by Transaction ID:**
```bash
GET /api/payments/transaction/TXN-1234567890
Authorization: Bearer <token>
```

**Get Payment Receipt:**
```bash
GET /api/payments/receipt/1
Authorization: Bearer <token>
```

**Refund Payment:**
```bash
POST /api/payments/1/refund
Authorization: Bearer <token>
```

**Check Payment Status:**
```bash
GET /api/payments/1/status
Authorization: Bearer <token>
```

## Using Postman

### Import Collection
A Postman collection is available in the `postman_collection.json` file. Import it to test all endpoints:

1. Open Postman
2. Click Import
3. Select `postman_collection.json`
4. Set the `baseUrl` variable to `http://localhost:8080`
5. Add JWT token to the `token` variable after login

## Troubleshooting

### Issue: Database Connection Failed
- Check MySQL is running
- Verify credentials in application.properties
- Ensure database exists

### Issue: JWT Token Invalid
- Ensure token is in Authorization header with "Bearer " prefix
- Check token hasn't expired
- Verify JWT secret matches in application.properties

### Issue: Port Already in Use
- Change port in application.properties: `server.port=8081`
- Or kill process on port 8080

### Issue: Schema Migration Failed
- Delete existing tables and let Hibernate recreate them
- Or manually run schema.sql

## Logging

Check logs in:
- Console output when running with `mvn spring-boot:run`
- Log files in `logs/` directory (if configured)

Enable debug logging in application.properties:
```properties
logging.level.com.JavaEcommerce=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## Project Structure

```
src/main/java/com/JavaEcommerce/Ecommerce/
├── controller/          # REST endpoints
├── model/              # JPA entities
├── service/            # Business logic
├── repo/               # Data access layer
├── payload/            # DTOs
├── request/            # Request objects
├── response/           # Response objects
├── exception/          # Custom exceptions
├── securityJwt/        # JWT utilities
├── securityServices/   # Security services
└── util/               # Utility classes
```

## Performance Tips

1. **Enable Query Caching**: Add to application.properties
   ```properties
   spring.jpa.properties.hibernate.cache.use_second_level_cache=true
   ```

2. **Use Pagination**: Always paginate large result sets
   ```bash
   GET /api/orders/history?pageNumber=0&pageSize=10
   ```

3. **Add Database Indexes**: Already included in schema.sql for:
   - order_id in orders table
   - user_id in orders and payments
   - payment_status in payments

## Security Best Practices

1. Change JWT secret in application.properties
2. Use HTTPS in production
3. Implement rate limiting
4. Add CSRF protection
5. Keep dependencies updated
6. Validate all user input

## Additional Resources

- **API Documentation**: See `API_DOCUMENTATION.md`
- **Implementation Summary**: See `IMPLEMENTATION_SUMMARY.md`
- **Code Examples**: Check individual controller classes

## Support

For issues or questions:
1. Check the logs for error messages
2. Review the API_DOCUMENTATION.md
3. Verify your request format matches examples
4. Check database connectivity

---

Happy coding! 🚀

