INSERT INTO role VALUES
                     (1, "ROLE_USER"),
                     (2, "ROLE_VIP_USER"),
                     (3, "ROLE_ADMIN");

INSERT INTO category (name, description) VALUES
                     ("Beverage", "Buy some good beer."),
                     ("Nourishment", "Buy some good beef.");

SET @johnUserId = UUID();
SET @johnAdminId = UUID();

INSERT INTO user_details (id, username, email, password, verified) VALUES
                     (@johnUserId, "JohnUSER", "email@email.com", "$2a$10$4KueE1A/G7JgU8WbwcFYVeW/umcKcx6ccgmeMBsI4uidRlEYvEVJ.", TRUE),
                     (@johnAdminId, "JohnADMIN", "emailA@email.com", "$2a$10$4KueE1A/G7JgU8WbwcFYVeW/umcKcx6ccgmeMBsI4uidRlEYvEVJ.", TRUE);
-- password: Password123%

INSERT INTO user_roles (role_id, user_id) VALUES
                    (1, @johnUserId),
                    (3, @johnAdminId);

INSERT INTO ad (title, description, price, zipcode, category_id, user_id, local_date_time) VALUES
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Pilsner Urquell", "Tasty Beer", "3000", "12345", 1, @johnUserId, CURRENT_TIMESTAMP),
            ("Budweiser", "Tasty Beer too", "4000", "67890", 1, @johnAdminId, CURRENT_TIMESTAMP),
            ("Budweiser", "Tasty Beer too", "4000", "67890", 1, @johnAdminId, CURRENT_TIMESTAMP),
            ("Budweiser", "Tasty Beer too", "4000", "67890", 1, @johnAdminId, CURRENT_TIMESTAMP),
            ("Budweiser", "Tasty Beer too", "4000", "67890", 1, @johnAdminId, CURRENT_TIMESTAMP),
            ("Budweiser", "Tasty Beer too", "4000", "67890", 1, @johnAdminId, CURRENT_TIMESTAMP),
            ("Scotch Steak", "Some good steak.", "6000", "11111", 2, @johnUserId, CURRENT_TIMESTAMP),
            ("Scotch Steak", "Some good steak.", "6000", "11111", 2, @johnUserId, CURRENT_TIMESTAMP),
            ("Scotch Steak", "Some good steak.", "6000", "11111", 2, @johnAdminId, CURRENT_TIMESTAMP);
