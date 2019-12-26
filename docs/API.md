**Search and Pagination**
    
   - `search`: [field]=='value'. RSQL parser link
   - `page`: zero indexed page number
   - `size`: 10, Page Size
   - `sort`: [field],asc|desc. e.g- ?sort=name,desc
   
    combined example
    ?search=name=='*Dress*'&page=0&sort=id,desc

**FetchAll Response Structure**
```$xslt
        {
        "content": [data1, data2, ..., datan],
        "last": true,
        "totalPages": 1,
        "totalElements": 5,
        "size": 10,
        "first": true,
        "number": 0,
        "sort": [{
            "direction": "DESC",
            "property": "id",
            "ignoreCase": false,
            "nullHandling": "NATIVE",
            "ascending": false,
            "descending": true
        }],
        "numberOfElements": 5
        }
```

**Buyer API**

1. Fetch All

```$xslt
    url: ~/v1/api/buyers
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data placeholder in fetch all - 
    {
        "id": 1,
        "name": "Muji",
        "desc": null,
    }
```

2 . Fetch one

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: GET
    action: 
    params:
    request data: 
    response data: 
    {
        "id": 1,
        "name": "Muji",
        "desc": null,
    }
```

3 . Create

```$xslt
    url: ~/v1/api/buyers/
    method: POST
    action: 
    params:
    request data: 
        {
               "name": "Dressman",
               "desc": "dressman",
         } 
    response data: 
          {
               "id": 2,
               "name": "Dressman",
               "desc": "dressman",
          } 
```

4 . update 

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: PUT
    action: 
    params:
    request data: 
          {
                "id": 2
                "name": "Van Hausen",
                "desc": "van hausen",
          } 
    response data: 
          {
                "id": 2
                "name": "Van Hausen",
                "desc": "van hausen",
          } 
```

5 . Delete 

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: DELETE
    action: 
    params:
    request data: 
    response data: 
```

**Season API**

1 . Fetch All

```$xslt
    url: ~/v1/api/seasons
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data placeholder in fetch all -
            {
                "id": 1,
                "name": "Spring 2020",
                "desc": "Spring 2020",
            }
```

2 . Fetch one

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: GET
    action: 
    params: 
    request data: 
    response data: 
        {
                "id": 1,
                "name": "Spring 2020",
                "desc": Spring 2020,
        } 
```

3 . Create

```$xslt
    url: ~/v1/api/seasons
    method: POST
    action: 
    params:
    request data: 
         {
                "name": "Spring 2020",
                "desc": Spring 2020,
         } 
    response data: 
         {
                "id": 2
                "name": "Spring 2020",
                "desc": Spring 2020,
         } 
```

4 . update 

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: PUT
    action: 
    params:
    request data: 
         {
                "id": 2
                "name": "Winter 2020",
                "desc": winter 2020,
         } 
    response data: 
        {
                "id": 2
                "name": "Winter 2020",
                "desc": winter 2020,
         } 
```

5 . Delete 

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: DELETE
    action: 
    params:
    request data: 
    response data: 
```

**Garment type API**

1 . Fetch All

```$xslt
    url: ~/v1/api/garmentTypes
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data placeholder in fetch all -
         {
                "id": 2
                "name": "Shirt",
                "desc": "Shirt",
         } 
      
```

2 . Fetch one

```$xslt
        url: ~/v1/api/garmentTypes/{garmentTypeId}
        method: GET
        action: 
        params:
        request data: 
        response data: 
            {
                 "id": 2
                 "name": "Shirt",
                 "desc": "Shirt",
            } 
```

3 . Create

```$xslt
    url: ~/v1/api/garmentTypes
    method: POST
    action: 
    params:
    request data: 
          {
                     "name": "Shirt",
                     "desc": "Shirt",
          } 
    response data: 
          {
                     "id": 2
                     "name": "Shirt",
                     "desc": "Shirt",
          } 
```

4 . update 

```$xslt
    url: ~/v1/api/garmentTypes/{garmentTypeId}
    method: PUT
    action: 
    params:
    request data: 
         {
                 "id": 2
                 "name": "Women's Shirt",
                 "desc": "Women's Shirt",
         } 
    response data: 
         {
                 "id": 2
                 "name": "Women's Shirt",
                 "desc": "Women's Shirt",
         } 
```

5 . Delete 

```$xslt
    url: ~/v1/api/garmentTypes/{garmentTypeId}
    method: DELETE
    action: 
    params:
    request data: 
    response data: 
```

**Department API**

1 . Fetch All

```$xslt
    url: ~/v1/api/departments
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data placeholder in fetch all -
         {
                "id": 2
                "name": "Cutting",
                "desc": "cutting",
         } 
```

2 . Fetch one

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: GET
    action: 
    params:
    request data: 
    response data: 
         {
                "id": 2
                "name": "Cutting",
                "desc": "cutting",
         } 
```

3 . Create

```$xslt
    url: ~/v1/api/departments
    method: POST
    action: 
    params:
    request data: 
          {
                     "name": "CAD",
                     "desc": "cad",
          } 
    response data: 
          {
                     "id": 2
                     "name": "CAD",
                     "desc": "cad",
          } 
```

4 . update 

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: PUT
    action: 
    params:
    request data: 
          {
                     "id": 2
                     "name": "Merchandiser",
                     "desc": "merchandiser",
          } 
    response data: 
          {
                     "id": 2
                     "name": "Merchandiser",
                     "desc": "merchandiser",
          } 

```

5 . Delete 

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: DELETE
    action: 
    params:
    request data: 
    response data: 
```


**Team API**

1 . Fetch All

```$xslt
    url: ~/v1/api/teams
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data placeholder in fetch all -
         {
                "id": 2
                "name": "Cutting",
                "desc": "cutting",
                "departmentId":1,
         } 
```

2 . Fetch one
 
```$xslt
    url: ~/v1/api/teams/{teamId}
    method: GET
    action: 
    params:
    request data: 
    response data: 
         {
                "id": 2
                "name": "Test Team ",
                "desc": "test team",
                "departmentId":1,
         } 
```

3 . Create

```$xslt
    url: ~/v1/api/teams
    method: POST
    action: 
    params:
    request data: 
          {
                     "name": "Test team 2",
                     "desc": "Test team 2",
                     "departmentId":1,
          } 
    response data: 
          {
                     "id": 2
                     "name": "Test team 2",
                     "desc": "Test team 2",
                     "departmentId":1,
          } 
```

4 . update 

```$xslt
    url: ~/v1/api/teams/{teamId}
    method: PUT
    action: 
    params:
    request data: 
          {
                     "id": 2
                     "name": "Test team 3",
                     "desc": "Test team 3",
                     "departmentId":1,
          } 
    response data: 
          {
                     "id": 2
                     "name": "Test team 3",
                     "desc": "Test team 3",
                     "departmentId":1,
          } 
```

5 . Delete 

```$xslt
    url: ~/v1/api/teams/{teamId}
    method: DELETE
    action: 
    params:
    request data: 
    response data:

```



**Activity API**

1 . Fetch All

```$xslt
    url: ~/v1/api/activities
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
       data placeholder in fetch all -
            {
                "id": 1
                "name": "Activity 1",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
            }
```

2 . Fetch one

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: GET
    action: 
    params:
    request data: 
    response data: 
            {
                "id": 1
                "name": "Activity 1",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
                "subActivityList": [
                    {
                        "name":"SubActivity 2",
                        "desc":"sub activity 2",
                        "activityId":1
                    }
                ]
            }
```

3 . Create

```$xslt
    url: ~/v1/api/activities
    method: POST
    action: 
    params:
    request data: 
            {
                "id": 1
                "name": "Activity 1",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
                "subActivityList": [
                    {
                        "name":"SubActivity 1",
                        "desc":"sub activity 1",
                        "activityId":1
                    }
                ]
                
            }
    response data: 
            {
                "id": 1
                "name": "Activity 1",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
                "subActivityList": [
                    {
                        "name":"SubActivity 1",
                        "desc":"sub activity 1",
                        "activityId":1
                    }
                ]
                
            }
```

4 . update 

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: PUT
    action: 
    params:
    request data: 
            {
                "id": 1
                "name": "Activity 2",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
                "subActivityList": [
                    {
                        "name":"SubActivity 2",
                        "desc":"sub activity 2",
                        "activityId":1
                    }
                ]
                
            }
    response data: 
            {
                "id": 1
                "name": "Activity 2",
                "serialNo": "1",
                "notify": "1",
                "cLevel": true,
                "departmentId":1,
                "subActivityList": [
                    {
                        "name":"SubActivity 2",
                        "desc":"sub activity 2",
                        "activityId":1
                    }
                ]
                
            }
```

5 . Delete 

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: DELETE
    action: 
    params:
    request data: 
    response data:
```