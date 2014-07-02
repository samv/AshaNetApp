 
Schema
=====================


----------
##Project
DataType            | Key
---------           | -----
PFobject id         | id
String              | Name
String              | Description
String              | Purpose
String              | Org_description
Number<enum>        | Project_type
Number<enum>        | focus
String              | Area
String              | Adress
String              | Chapter
Date                | Year
Number              | Funds_donated
Number<enum>        | Status
List<String>        | Images

Status type enums are listed at http://www.ashanet.org/projects/status.php

Project type enums are listed at http://www.ashanet.org/projects/project_type.php

Focus type enums are listed at http://www.ashanet.org/projects/focus.php

----------
##Events
DataType                  | Key
---------                 | -----
PFobject id               | id
PFobject id               | chapter_id
List<PFobject id>         | projects
String                    | Name
String                    | Description
String                    | Location
Date                      | event_start
Mapping<String, Currency> | event_pricing
String                    | Ticketing Site URL
List<String>              | Images

The Chapter ID is the closest group that is putting the event on.

The Projects ID list is/are the project(s) which the event is raising
funds for.

Event_pricing contains a mapping from user-readable descriptions (eg,
"single", "couple", "early bird") to a price *estimate*.  The
ticketing site enforces these rules.

----------
##Donations
DataType                | Key
---------               | -----
PFobject id             | id
Currency                | donation_amount
PFobject id             | project_id
PFobject id             | chapter_id
String                  | receipt_info

Record of a donation.  The project_id will not be set if the donation
was made to the chapter.  The "receipt info" should be human readable
reference information, such as the last 4 digits of the card used or
some such; however *extra fields* should be added to records to
indicate any other information that was received from the payment
provider/library/gateway.  Prefix this with the name of the gateway,
eg `paypal_txid` or `stripe_userid`.

----------

##Admin / Chapter Representatives
DataType            | Key
---------           | -----
PFobject id         | Admin_user
List<Chapter_id>    | Admin_chapters

The Admin table specifies which chapters administrators may create and
edit *events* for.

----------
##Chapter
DataType            | Key
---------           | -----
id (PFobject id)    | Chapter_id
String              | Name

The Chapter is a read-only list, for the drop-down menu when making a
payment, and for foreign keys for events.

----------
