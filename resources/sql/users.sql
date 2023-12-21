-- :name create-user! :! :>
-- :doc creates a new user record
INSERT INTO txn.customers
(first_name, last_name, email, pass)
VALUES (:first_name, :last_name, :email, :pass)
returning id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-all-users :? :*
-- :doc retrieve all users.
SELECT * FROM users