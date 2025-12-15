# E-Commerce API - New Features Documentation

This document describes the newly implemented features for the E-Commerce application including Cart Management, Address Management, Orders, and Payments.

## Table of Contents
1. [Cart Management APIs](#cart-management-apis)
2. [Address Management APIs](#address-management-apis)
3. [Orders APIs](#orders-apis)
4. [Payments APIs](#payments-apis)
5. [Database Schema](#database-schema)

---

## Cart Management APIs

### Base URL: `/api/cart`

#### 1. Add Product to Cart
- **Endpoint:** `POST /api/products/{productId}/quantity/{quantity}`
- **Description:** Adds a product to the user's cart
- **Path Parameters:**
  - `productId` (Long): ID of the product to add
  - `quantity` (Integer): Quantity to add
- **Response:** CartResponse with cart details
- **Status Code:** 201 CREATED

**Example Request:**
```
POST /api/products/1/quantity/2
```

**Example Response:**
```json
{
  "success": true,
  "message": "Product added to cart successfully",
  "timestamp": "2025-12-10T13:54:00",
  "cart": {
    "cartId": 1,
    "totalPrice": 1000.0,
    "products": [...]
  },
  "status": "201 CREATED"
}
```

#### 2. Get Cart Details
- **Endpoint:** `GET /api/cart`
- **Description:** Retrieves the current user's cart with all items
- **Response:** CartResponse with cart details
- **Status Code:** 200 OK

#### 3. Update Product Quantity in Cart
- **Endpoint:** `PUT /api/cart/products/{productId}/quantity/{quantity}`
- **Description:** Updates the quantity of a product in the cart
- **Path Parameters:**
  - `productId` (Long): ID of the product
  - `quantity` (Integer): New quantity
- **Response:** CartResponse with updated cart
- **Status Code:** 200 OK

**Example Request:**
```
PUT /api/cart/products/1/quantity/5
```

#### 4. Delete Product from Cart
- **Endpoint:** `DELETE /api/cart/products/{productId}`
- **Description:** Removes a product from the cart
- **Path Parameters:**
  - `productId` (Long): ID of the product to remove
- **Response:** CartResponse with updated cart
- **Status Code:** 200 OK

#### 5. Clear Cart
- **Endpoint:** `DELETE /api/cart`
- **Description:** Removes all items from the cart
- **Response:** CartResponse with empty cart
- **Status Code:** 200 OK

#### 6. Get All Carts
- **Endpoint:** `GET /api/carts`
- **Description:** Retrieves all carts
- **Response:** List of CartDto
- **Status Code:** 302 FOUND

---

## Address Management APIs

### Base URL: `/api/addresses`

#### 1. Create Address
- **Endpoint:** `POST /api/addresses`
- **Description:** Creates a new address for the logged-in user
- **Request Body:**
```json
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
- **Response:** AddressResponse with created address
- **Status Code:** 201 CREATED

#### 2. Get User Addresses
- **Endpoint:** `GET /api/addresses`
- **Description:** Retrieves all addresses for the logged-in user
- **Response:** List of AddressDto
- **Status Code:** 200 OK

#### 3. Get Default Address
- **Endpoint:** `GET /api/addresses/default`
- **Description:** Retrieves the default address for the logged-in user
- **Response:** AddressDto
- **Status Code:** 200 OK

#### 4. Get Address by ID
- **Endpoint:** `GET /api/addresses/{addressId}`
- **Description:** Retrieves a specific address by ID
- **Path Parameters:**
  - `addressId` (Long): ID of the address
- **Response:** AddressDto
- **Status Code:** 200 OK

#### 5. Update Address
- **Endpoint:** `PUT /api/addresses/{addressId}`
- **Description:** Updates an existing address
- **Path Parameters:**
  - `addressId` (Long): ID of the address to update
- **Request Body:**
```json
{
  "street": "456 Oak Ave",
  "city": "Los Angeles",
  "state": "CA",
  "country": "USA",
  "zipCode": "90001",
  "phoneNumber": "555-5678",
  "recipientName": "Jane Doe",
  "addressType": "BILLING",
  "isDefault": false
}
```
- **Response:** AddressResponse with updated address
- **Status Code:** 200 OK

#### 6. Delete Address
- **Endpoint:** `DELETE /api/addresses/{addressId}`
- **Description:** Deletes an address
- **Path Parameters:**
  - `addressId` (Long): ID of the address to delete
- **Response:** AddressResponse
- **Status Code:** 200 OK

#### 7. Set Default Address
- **Endpoint:** `PUT /api/addresses/{addressId}/set-default`
- **Description:** Sets an address as the default address
- **Path Parameters:**
  - `addressId` (Long): ID of the address
- **Response:** AddressResponse
- **Status Code:** 200 OK

#### 8. Get Addresses by Type
- **Endpoint:** `GET /api/addresses/type/{addressType}`
- **Description:** Retrieves addresses by type (SHIPPING, BILLING, BOTH)
- **Path Parameters:**
  - `addressType` (String): Type of address (SHIPPING, BILLING, BOTH)
- **Response:** List of AddressDto
- **Status Code:** 200 OK

---

## Orders APIs

### Base URL: `/api/orders`

#### 1. Create Order
- **Endpoint:** `POST /api/orders`
- **Description:** Creates an order from the user's cart
- **Request Body:**
```json
{
  "addressId": 1,
  "orderNotes": "Please deliver after 5 PM",
  "discountAmount": 50.0
}
```
- **Response:** OrderResponse with created order
- **Status Code:** 201 CREATED

#### 2. Get User Orders
- **Endpoint:** `GET /api/orders`
- **Description:** Retrieves all orders for the logged-in user
- **Response:** List of OrderDto
- **Status Code:** 200 OK

#### 3. Get Order Details
- **Endpoint:** `GET /api/orders/{orderId}`
- **Description:** Retrieves details of a specific order
- **Path Parameters:**
  - `orderId` (Long): ID of the order
- **Response:** OrderDto
- **Status Code:** 200 OK

#### 4. Get Orders by Status
- **Endpoint:** `GET /api/orders/status/{status}`
- **Description:** Retrieves orders by status
- **Path Parameters:**
  - `status` (String): Order status (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED)
- **Response:** List of OrderDto
- **Status Code:** 200 OK

#### 5. Update Order Status
- **Endpoint:** `PUT /api/orders/{orderId}/status/{status}`
- **Description:** Updates the status of an order
- **Path Parameters:**
  - `orderId` (Long): ID of the order
  - `status` (String): New status
- **Response:** OrderResponse with updated order
- **Status Code:** 200 OK

#### 6. Cancel Order
- **Endpoint:** `PUT /api/orders/{orderId}/cancel`
- **Description:** Cancels an order
- **Path Parameters:**
  - `orderId` (Long): ID of the order to cancel
- **Response:** OrderResponse
- **Status Code:** 200 OK

#### 7. Get Order History
- **Endpoint:** `GET /api/orders/history?pageNumber=0&pageSize=10`
- **Description:** Retrieves paginated order history
- **Query Parameters:**
  - `pageNumber` (int, default: 0): Page number
  - `pageSize` (int, default: 10): Page size
- **Response:** List of OrderDto
- **Status Code:** 200 OK

#### 8. Get Recent Orders
- **Endpoint:** `GET /api/orders/recent?limit=5`
- **Description:** Retrieves recent orders
- **Query Parameters:**
  - `limit` (int, default: 5): Number of recent orders to retrieve
- **Response:** List of OrderDto
- **Status Code:** 200 OK

---

## Payments APIs

### Base URL: `/api/payments`

#### 1. Process Payment
- **Endpoint:** `POST /api/payments`
- **Description:** Processes a payment for an order
- **Request Body:**
```json
{
  "orderId": 1,
  "amount": 1000.0,
  "paymentMethod": "CREDIT_CARD",
  "paymentGateway": "STRIPE"
}
```
- **Response:** PaymentResponse with payment details
- **Status Code:** 201 CREATED

**Payment Methods Available:**
- CREDIT_CARD
- DEBIT_CARD
- NET_BANKING
- UPI
- WALLET
- CASH_ON_DELIVERY

#### 2. Get Payment Details
- **Endpoint:** `GET /api/payments/{paymentId}`
- **Description:** Retrieves details of a specific payment
- **Path Parameters:**
  - `paymentId` (Long): ID of the payment
- **Response:** PaymentDto
- **Status Code:** 200 OK

#### 3. Get User Payments
- **Endpoint:** `GET /api/payments`
- **Description:** Retrieves all payments for the logged-in user
- **Response:** List of PaymentDto
- **Status Code:** 200 OK

#### 4. Get Payments by Status
- **Endpoint:** `GET /api/payments/status/{status}`
- **Description:** Retrieves payments by status
- **Path Parameters:**
  - `status` (String): Payment status (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED)
- **Response:** List of PaymentDto
- **Status Code:** 200 OK

#### 5. Get Payment by Transaction ID
- **Endpoint:** `GET /api/payments/transaction/{transactionId}`
- **Description:** Retrieves a payment by transaction ID
- **Path Parameters:**
  - `transactionId` (String): Transaction ID
- **Response:** PaymentDto
- **Status Code:** 200 OK

#### 6. Get Payment History
- **Endpoint:** `GET /api/payments/history?pageNumber=0&pageSize=10`
- **Description:** Retrieves paginated payment history
- **Query Parameters:**
  - `pageNumber` (int, default: 0): Page number
  - `pageSize` (int, default: 10): Page size
- **Response:** List of PaymentDto
- **Status Code:** 200 OK

#### 7. Refund Payment
- **Endpoint:** `POST /api/payments/{paymentId}/refund`
- **Description:** Refunds a completed payment
- **Path Parameters:**
  - `paymentId` (Long): ID of the payment to refund
- **Response:** PaymentResponse
- **Status Code:** 200 OK

#### 8. Get Payment Receipt
- **Endpoint:** `GET /api/payments/receipt/{orderId}`
- **Description:** Retrieves the payment receipt for an order
- **Path Parameters:**
  - `orderId` (Long): ID of the order
- **Response:** PaymentDto
- **Status Code:** 200 OK

#### 9. Check Payment Status
- **Endpoint:** `GET /api/payments/{paymentId}/status`
- **Description:** Checks the status of a payment
- **Path Parameters:**
  - `paymentId` (Long): ID of the payment
- **Response:** String (status name)
- **Status Code:** 200 OK

---

## Database Schema

### Tables Created/Modified

#### 1. Orders Table
```sql
CREATE TABLE orders (
  order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  total_amount DECIMAL(10, 2) DEFAULT 0.0,
  discount_amount DECIMAL(10, 2) DEFAULT 0.0,
  final_amount DECIMAL(10, 2) DEFAULT 0.0,
  order_date DATETIME NOT NULL,
  estimated_delivery_date DATETIME,
  delivery_date DATETIME,
  order_notes TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);
```

#### 2. Order Items Table
```sql
CREATE TABLE order_items (
  order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  price_per_unit DECIMAL(10, 2) NOT NULL,
  discount DECIMAL(10, 2) DEFAULT 0.0,
  item_total DECIMAL(10, 2) NOT NULL
);
```

#### 3. Payments Table
```sql
CREATE TABLE payments (
  payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  payment_method VARCHAR(50),
  transaction_id VARCHAR(100),
  payment_gateway VARCHAR(50),
  payment_reference VARCHAR(255),
  payment_date DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  notes TEXT
);
```

#### 4. Addresses Table (Updated)
Added the following columns:
- `phone_number VARCHAR(20)`
- `recipient_name VARCHAR(50)`
- `address_type VARCHAR(20)` - Values: SHIPPING, BILLING, BOTH
- `is_default BOOLEAN`

#### 5. User Addresses Junction Table
```sql
CREATE TABLE user_addresses (
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, address_id)
);
```

---

## Models and Entities

### New Enums
1. **OrderStatus**: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED
2. **PaymentStatus**: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED
3. **PaymentMethod**: CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, CASH_ON_DELIVERY
4. **AddressType**: SHIPPING, BILLING, BOTH

### New DTOs
- `OrderDto`
- `OrderItemDto`
- `PaymentDto`
- `AddressDto`

### Request Objects
- `CreateOrderRequest`
- `CreateAddressRequest`
- `UpdateAddressRequest`
- `ProcessPaymentRequest`

### Response Objects
- `OrderResponse`
- `PaymentResponse`
- `AddressResponse`

---

## Service Layer

### Services Implemented
1. **AddressService** - CRUD operations for addresses with default address management
2. **OrderService** - Order creation, retrieval, and status management
3. **PaymentService** - Payment processing, refunds, and receipt generation

### Repository Interfaces
1. **OrderRepository** - Custom queries for order filtering
2. **OrderItemRepository** - Order item operations
3. **PaymentRepository** - Payment filtering and search
4. **AddressRepository** - Address filtering by user and type

---

## Error Handling

The application includes comprehensive error handling with the following exception types:

- **ResourceNotFoundException**: When a resource is not found
- **ApiException**: For general API errors
- **Validation Errors**: For invalid input data

All errors return appropriate HTTP status codes:
- 400: Bad Request
- 404: Not Found
- 500: Internal Server Error

---

## Authentication

All endpoints require JWT authentication. Include the JWT token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

---

## Future Enhancements

1. **Real Payment Gateway Integration**: Integrate with Stripe, PayPal, or other payment gateways
2. **Email Notifications**: Send order and payment confirmation emails
3. **Inventory Management**: Automatic stock decrement and restock alerts
4. **Order Tracking**: Real-time order tracking with status updates
5. **Invoice Generation**: PDF invoice generation for orders
6. **Coupon/Discount Codes**: Coupon management system
7. **Wishlist**: User wishlist functionality
8. **Reviews and Ratings**: Product review and rating system

---

## Testing

The application is tested with Maven and can be built using:

```bash
mvn clean compile
mvn test
mvn clean install
```

---

## Support

For issues or questions regarding these APIs, please refer to the project documentation or contact the development team.

