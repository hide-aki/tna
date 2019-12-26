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
        "content": [{
            "id": 5,
            "modifiedAt": 1576391224503,
            "lastModifiedBy": "super_user",
            "name": "Muji",
            "code": "MUJI",
            "desc": null,
            "uid": "Buyer-Muji"
        }],
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
        data structure - 
    {
        "id": 5,
        "name": "Muji",
        "desc": null,
    }
```

2 . Fetch one

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
    {
        "id": 5,
        "name": "Muji",
        "desc": null,
    }
```

3 . Create

```$xslt
    url: ~/v1/api/buyers/
    method: POST
    action: 
    params: search, page, size, sort
    request data: 
        data structure - 
  
    response data: 
        data structure - 
```

4 . update 

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
        data structure - 
    response data: 
        data structure - 
```

5 . Delete 

```$xslt
    url: ~/v1/api/buyers/{buyerId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
            {
            
            }
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
        data structure - 
            {
                "id": 1,
                "name": "Spring 2020",
                "desc": "spring 2020",
            }
```

2 . Fetch one

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
```

3 . Create

```$xslt
    url: ~/v1/api/seasons
    method: POST
    action: 
    params: search, page, size, sort
    request data: 
        data structure - 
    response data: 
        data structure - 
```

4 . update 

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

5 . Delete 

```$xslt
    url: ~/v1/api/seasons/{seasonId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
            {
            
            }
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
        data structure - 
```

2 . Fetch one

```$xslt
    url: ~/v1/api/garmentTypes/{garmentTypeId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
```

3 . Create

```$xslt
url: ~/v1/api/garmentTypes
method: POST
action: 
params: search, page, size, sort
request data: 
    data structure - 
response data: 
    data structure - 
```

4 . update 

```$xslt
    url: ~/v1/api/garmentTypes/{garmentTypeId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
        data structure - 
    response data: 
        data structure - 
```

5 . Delete 

```$xslt
    url: ~/v1/api/garmentTypes/{garmentTypeId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
            {
            
            }
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
        data structure - 
```

2 . Fetch one

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
```

3 . Create

```$xslt
    url: ~/v1/api/departments
    method: POST
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

4 . update 

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
    response data: 

```

5 . Delete 

```$xslt
    url: ~/v1/api/departments/{departmentId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

**FetchAll Response Structure**
```$xslt
    {
        "content": [
            {
                "createdAt": "2019-12-24T07:01:31.888+0000",
                "modifiedAt": "2019-12-24T07:01:31.888+0000",
                "createdBy": "admin_tna",
                "lastModifiedBy": "admin_tna",
                "id": 2,
                "name": "Team 1",
                "desc": "team 1",
                "departmentId": 1
            }
        ],
        "pageable": {
            "sort": {
                "sorted": false,
                "unsorted": true,
                "empty": true
            },
            "offset": 0,
            "pageSize": 20,
            "pageNumber": 0,
            "paged": true,
            "unpaged": false
        },
        "last": true,
        "totalElements": 1,
        "totalPages": 1,
        "size": 20,
        "number": 0,
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "numberOfElements": 1,
        "first": true,
        "empty": false
    }
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
```

2 . Fetch one
 
```$xslt
    url: ~/v1/api/teams/{teamId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 

```

3 . Create

```$xslt
    url: ~/v1/api/teams
    method: POST
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

4 . update 

```$xslt
    url: ~/v1/api/teams/{teamId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

5 . Delete 

```$xslt
    url: ~/v1/api/teams/{teamId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 

```

**FetchAll Response Structure**

```$xslt
    {
        "content": [
            {
                "createdAt": "2019-12-24T06:21:56.005+0000",
                "modifiedAt": "2019-12-24T06:21:56.005+0000",
                "createdBy": "admin_tna",
                "lastModifiedBy": "admin_tna",
                "id": 11,
                "name": "Activity 2",
                "serialNo": 1,
                "notify": "1",
                "department": null,
                "subActivityList": [
                            {
                                "name":"subActivity 1",
                                "desc":"subActivity 1"
                            }
                        ],
                "departmentId": 1,
                "cLevel": true
            },
        ],
        "pageable": {
            "sort": {
                "sorted": false,
                "unsorted": true,
                "empty": true
            },
            "offset": 0,
            "pageSize": 20,
            "pageNumber": 0,
            "paged": true,
            "unpaged": false
        },
        "last": true,
        "totalElements": 4,
        "totalPages": 1,
        "size": 20,
        "number": 0,
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "numberOfElements": 4,
        "first": true,
        "empty": false
    }
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
        data structure - 
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

2 . Fetch one

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

3 . Create

```$xslt
    url: ~/v1/api/activities
    method: POST
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

4 . update 

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: PUT
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```

5 . Delete 

```$xslt
    url: ~/v1/api/activities/{activityId}
    method: DELETE
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
```