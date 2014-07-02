 
Schema
=====================


----------
##Project
DataType            | Key
---------           | -----
id (PFobject id)    | Project_id
String              | Name
String              | Description
String              | Purpose
String              | Org_description
String              | Project_type
String              | Area
String              | Adress
String              | Chapter
Date                | Year
Number              | Funds_donated
Number  < enum >    | Status
Image               | Image_array
User - PFUser       | Donor


----------
##Events
DataType            | Key
---------           | -----
id (PFobject id)    | Project_id
String              | Name
String              | Description
String              | Location
Date                    | Time and Date
Dictionary / HashTable  | Ticket ( Price, value) pairs (single, early bird)
String              | Ticketing Site URL
Image               | Image_array


----------
##Donations
DataType            | Key
---------           | -----
User                | Donor
Number                  | dondation_amount
Project_id              | Foriegn key to project table
Chapter_id              | Foriegn key to chapter table


----------

##Admin / Chapter Representatives
DataType            | Key
---------           | -----
User                | Admin_user
Chapter_id          | Foriegn key to chapter table


----------
##Chapter
DataType            | Key
---------           | -----
id (PFobject id)    | Chapter_id
String              | Name


----------
