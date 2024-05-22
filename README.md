# Marketplace Backend Web Application

Welcome to our Marketplace Backend Web Application Project!

Our team is creating a marketplace backend web application as part of our learning journey. Currently in active development, we're eager to share our progress and features with you.

## Implemented Features

### 1. User Registration with Email Verification
- Users can sign up using their username and verify their account via email.

### 2. User Authentication with Spring Security
- Secure user authentication with roles, access tokens, and refresh tokens.

### 3. Integration with Stripe for Online Payments
- Enabling online payments with Stripe and automated invoice generation.

### 4. Admin Features
- Administrators can create categories and ban users.

### 5. Email Notifications to Sellers
- Sending email notifications to sellers for various events.

### 6. Swagger UI for Documentation
- Interactive documentation for APIs. When the app is running on port 8080, documentation is available [here](http://localhost:8080/swagger-ui.html).

### 7. Ads with Categories
- Implementing ads categorized for better organization.

### 8. Database Version Control with Flyway
- Managing and versioning database schema changes.

### 9. H2 Database for Testing
- Using H2 database for testing purposes.

### 10. Java Code Coverage with Jacoco
- Measuring and reporting code coverage for Java code.

### 11. Automated tests
- Implement automated integration tests for all endpoints, ensuring that each endpoint is tested with both a positive and a negative case.

### 12. CI/CD with Git Actions
- Automating build and tests using Git Actions.

### 13. Endpoint Logs
- Logging endpoint activities for tracking and debugging.

## Getting Started

### To run the application, you need to set environment variables:
for MySQL database:
<pre>
DB_PASSWORD
DB_USERNAME
</pre>


for MailTrap:
<pre>
EMAIL_PASSWORD
EMAIL_USERNAME
EMAIL_VERIFICATION (on, off)
</pre>
<pre>
JWT_SECRET_KEY
STRIPE_APIKEY
</pre>

### Installation

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Porostovo/Shopify.git
