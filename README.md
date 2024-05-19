# Welcome to our Marketplace Backend Web Application Project!
Our team is creating a marketplace backend web application as part of our learning journey. Currently in active development, we're eager to share our progress and features with you.


## Implemented Features:

- **User Registration with Email Verification:** Users can sign up using their username and verify their account via email.
- **User Authentication with Spring Security:** Secure user authentication with roles, access tokens, and refresh tokens.
- **Integration with Stripe for Online Payments:** Enabling online payments with Stripe and automated invoice generation.
- **Admin Features:** Administrators can create categories and ban users.
- **Email Notifications to Sellers:** Sending email notifications to sellers for various events.
- **Swagger UI for Documentation:** Interactive documentation for APIs. When app is running on 8080 port, documentation available [here](http://localhost:8080/swagger).
- **Ads with Categories:** Implementing ads categorized for better organization.
- **Database Version Control with Flyway:** Managing and versioning database schema changes.
- **H2 Database for Testing:** Using H2 database for testing purposes.
- **Java Code Coverage with Jacoco:** Measuring and reporting code coverage for Java code.
- **CI/CD with Git Actions:** Automating build and deployment processes using GIT ACTIONS.
- **Endpoint Logs:** Logging endpoint activities for tracking and debugging.

## To run the application, you need to set environment variables:
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


