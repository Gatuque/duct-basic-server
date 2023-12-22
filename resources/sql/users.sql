-- :name create-user! :! :>
-- :doc creates a new user record
INSERT INTO users
(first_name, last_name, email, pass)
VALUES (:first_name, :last_name, :email, :password)
returning id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT id, first_name, last_name, last_login, email, is_active FROM users
WHERE id = :id

-- :name get-all-users :? :*
-- :doc retrieve all users.
SELECT id, first_name, last_name, last_login, email, is_active FROM users

-- :name update-user! :! :1
-- :doc update a user given the id.
UPDATE users set first_name = :first_name, last_name = :last_name
WHERE id = :id

-- :name delete-user! :! :1
-- :doc delete a user given the id.
DELETE FROM users
WHERE id = :id