# Pasithea

Pasithea *(one of the Greek Charities (Graces) who was the personification of sleep, meditation and REST)* is a basic REST API built with Spring to be able to view products and add them to a shopping cart 

## Getting Started

### Prerequisites

- [JDK 1.8](https://adoptopenjdk.net/) or later
- [cURL](https://curl.haxx.se/download.html)
- [Gradle](https://gradle.org/install/) (Optional) - *You can install Gradle or you can just use the gradlew file in the project root directory. The documentation will assume Gradle is NOT installed*

### Installing

Download the project using Git and move into the directory

```
git clone https://github.com/zackhotte/pasithea.git
cd pasithea
```
And then use the gradle wrapper to run the web application
<br><br>
**Linux/macOS**

```
./gradlew bootRun
```
**Windows**

```
./gradlew.bat bootRun
```

### Query list of products

To get a list of all products, you can make a GET request to */api/products*<br>
For the moment, all the default products in the API are books.
<br><br>
```
curl http://localhost:8080/api/products
```
*Optional*<br>
You can pipe the curl output to **json_pp** to "pretty print" the results of the json data.
```
curl http://localhost:8080/api/products | json_pp
```
**Output**
```
[
    {
        "id": 1,
        "name": "The Hunger Games",
        "category": "BOOK",
        "quantity": 290,
        "price": 1.53,
        "rating": 4.34,
        "imageUrl": "https://images.gr-assets.com/books/1447303603m/2767052.jpg",
        "link": [
            {
                "href": "http://localhost:8080/products/1",
                "rel": "self"
            }
        ]
  },
  {
        "id": 2,
        "name": "Harry Potter and the Philosopher's Stone",
        "category": "BOOK",
        "quantity": 124,
        "price": 4.03,
        "rating": 4.44,
        "imageUrl": "https://images.gr-assets.com/books/1474154022m/3.jpg",
        "link": [
            {
                "href": "http://localhost:8080/products/2",
                "rel": "self"
            }
        ]
  },
 ...
]
```

### Query a Specific Product

To query a specific product and to get more details about the product, just simply add the product id to the route as follows
```
curl http://localhost:8080/api/products/2
```
**Output**
```
{
   "rating" : 4.44,
   "pageCount" : 352,
   "format" : "PAPERBACK",
   "name" : "Harry Potter and the Philosopher's Stone",
   "quantity" : 124,
   "authors" : [
      {
         "firstName" : "J.k.",
         "lastName" : "Rowling",
         "fullName" : "J.k. Rowling"
      }
   ],
   "description" : "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris semper.",
   "id" : 2,
   "originalPublicationYear" : "1997",
   "publishedDate" : 1409544000000,
   "imageUrl" : "https://images.gr-assets.com/books/1474154022m/3.jpg",
   "price" : 4.03,
   "inStock" : true,
   "publisher" : "Bloomsbury Children's Books"
}
```

Or, you can query a product by name using the **q** query parameter. This will return any product that matches the query paremeter as well as return the total number of results found

```
curl http://localhost:8080/api/products?q=Harry+Potter
```

**Output**
```
{
   "q" : "Harry",
   "res" : {
      "results" : [
         {
            "format" : "PAPERBACK",
            "pageCount" : 352,
            "publisher" : "Bloomsbury Children's Books",
            "name" : "Harry Potter and the Philosopher's Stone",
            "price" : 4.03,
            "rating" : 4.44,
            "inStock" : true,
            "authors" : [
               {
                  "fullName" : "J.k. Rowling",
                  "lastName" : "Rowling",
                  "firstName" : "J.k."
               }
            ],
            "description" : "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris semper.",
            "imageUrl" : "https://images.gr-assets.com/books/1474154022m/3.jpg",
            "quantity" : 124,
            "publishedDate" : 1409544000000,
            "originalPublicationYear" : "1997",
            "id" : 2
         },
         {
            "originalPublicationYear" : "1999",
            "id" : 18,
            "publishedDate" : 1409544000000,
            "rating" : 4.53,
            "inStock" : true,
            "quantity" : 56,
            "authors" : [
               {
                  "fullName" : "J.k. Rowling",
                  "lastName" : "Rowling",
                  "firstName" : "J.k."
               }
            ],
            "description" : "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris semper.",
            "imageUrl" : "https://images.gr-assets.com/books/1499277281m/5.jpg",
            "name" : "Harry Potter and the Prisoner of Azkaban",
            "price" : 16.29,
            "format" : "PAPERBACK",
            "pageCount" : 480,
            "publisher" : "Bloomsbury Children's Books (Sept. 1 2014)"
         },
...
```

### Add to your shopping cart

To add an item to your shopping cart, you must send a POST request to */api/shoppingcart/addtocart* with the product id and quantity as keys in a JSON payload.<br>
A response object is sent back with the result details as well as a link to the shopping cart item that was added.<br><br>
**JSON Payload Example**
```
{
    "id": "2", 
    "quantity": "5"
}
```
**Request**
```
curl http://localhost:8080/api/shoppingcart/addtocart -d '{"id": "2", "quantity": "5"}' -H "Content-Type: application/json" | json_pp
```
**Output**
```
{
   "message" : "Product id 2 has been added to your shopping cart",
   "code" : 200,
   "error" : null,
   "link" : "http://localhost:8080/api/shoppingcart/1"
}
```

### Query your shopping cart

To view your shopping cart, send a GET request to the route */products/shoppingcart*
```
curl http://localhost:8080/api/shoppingcart
```
**Output**
```
[
   {
      "ordered" : false,
      "hasBeenDelivered" : false,
      "hasShipped" : false,
      "quantity" : 5,
      "book" : {
         "quantity" : 119,
         "imageUrl" : "https://images.gr-assets.com/books/1474154022m/3.jpg",
         "originalPublicationYear" : "1997",
         "inStock" : true,
         "price" : 4.03,
         "authors" : [
            {
               "fullName" : "J.k. Rowling",
               "lastName" : "Rowling",
               "firstName" : "J.k."
            }
         ],
         "rating" : 4.44,
         "publisher" : "Bloomsbury Children's Books",
         "description" : "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris semper.",
         "name" : "Harry Potter and the Philosopher's Stone",
         "format" : "PAPERBACK",
         "publishedDate" : 1409544000000,
         "pageCount" : 352,
         "id" : 2
      },
      "id" : 1,
      "unitPrice" : 4.03,
      "totalPrice" : 20.150002
   }
]
```

### Remove an item from your shopping cart

To remove an item from your shopping cart, you simply make a POST request to */products/removefromcart* and you only need pass the product ID as a JSON payload.<br>
The request will return a response object with the result and details of the request<br>

**JSON Payload Example**
```
{
    "id": "2"
}
```

```
curl http://localhost:8080/api/shoppingcart/removefromcart -d '{"id": "2"}' -H "Content-Type: application/json"
```

**Output**
```
{
   "code" : 200,
   "link" : "http://localhost:8080/products/shoppingcart",
   "error" : null,
   "message" : "Product id 2 has been removed from the shopping cart"
}
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Web Framework
* [Gradle](https://gradle.org/) - Dependency Management & Build Tool
* [H2](http://www.h2database.com/html/main.html) - In-Memory SQL Database

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
